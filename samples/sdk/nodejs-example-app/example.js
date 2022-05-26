'use strict'

const express = require('express')
const http = require('http')
const readline = require('readline')
const fs = require('fs')
const sdk = require('verity-sdk')
const vdrtools = require('vdr-tools')
const Spinner = require('cli-spinner').Spinner
const QRCode = require('qrcode')
const request = require('request-promise-native')
const { v4: uuidv4 } = require('uuid')

const LISTENING_PORT = 4000
const CONFIG_PATH = 'verity-context.json'
const INSTITUTION_NAME = 'Faber College'
const LOGO_URL = 'https://freeiconshop.com/wp-content/uploads/edd/bank-flat.png'
const WALLET_NAME = 'examplewallet1'
const WALLET_KEY = 'examplewallet1'
const ANSII_GREEN = '\u001b[32m'
const ANSII_RESET = '\x1b[0m'
const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
})

const handlers = new sdk.Handlers()
let listener

let context
let issuerDID
let issuerVerkey

async function example () {
  await setup()
  const schemaId = await writeLedgerSchema()
  const defId = await writeLedgerCredDef(schemaId)

  const relDID = await createRelationship()
  await createConnection(relDID)

  await askQuestion(relDID)

  await issueCredential(relDID, defId)

  await requestProof(relDID)
}

//* ***********************
//       RELATIONSHIP
//* ***********************
async function createRelationship () {
  // Relationship protocol has two steps
  // 1. create relationship key
  // 2. create invitation

  // Step 1

  // Constructor for the Connecting API
  const relProvisioning = new sdk.protocols.v1_0.Relationship()
  let spinner = new Spinner('Waiting to create relationship ... %s').setSpinnerDelay(450) // Console spinner

  // handler for the response to the request to start the Relationship protocol.
  const firstStep = new Promise((resolve) => {
    handlers.addHandler(relProvisioning.msgFamily, relProvisioning.msgFamilyVersion, async (msgName, message) => {
      switch (msgName) {
        case relProvisioning.msgNames.CREATED: {
          spinner.stop()
          printMessage(msgName, message)
          const threadId = message['~thread'].thid
          const relDID = message.did
          resolve([relDID, threadId])
          break
        }
        default:
          printMessage(msgName, message)
          nonHandle('Message Name is not handled - ' + msgName)
      }
    })
  })

  spinner.start()
  // starts the relationship protocol
  await relProvisioning.create(context)
  const relationshipKeys = await firstStep // wait for response from verity application
  const relDID = relationshipKeys[0]
  const threadId = relationshipKeys[1]

  // Step 2

  spinner = new Spinner('Waiting to create invitation ... %s').setSpinnerDelay(450) // Console spinner
  // handler for the accept message sent when relationship is accepted
  const secondStep = new Promise((resolve) => {
    handlers.addHandler(relProvisioning.msgFamily, relProvisioning.msgFamilyVersion, async (msgName, message) => {
      switch (msgName) {
        case relationship.msgNames.INVITATION: {
          spinner.stop()
          printMessage(msgName, message)
          const inviteURL = message.inviteURL

          await QRCode.toFile('qrcode.png', inviteURL)

          console.log()
          if (process.env.HTTP_SERVER_URL) {
            console.log('Open the following URL in your browser and scan presented QR code')
            console.log(`${ANSII_GREEN}https://chart.googleapis.com/chart?cht=qr&chs=512x512&chl=${encodeURI(inviteURL)}${ANSII_RESET}`)
          } else {
            console.log('QR code generated at: qrcode.png')
            console.log('Open this file and scan QR code to establish a connection')
          }
          resolve(null)
          break
        }
        default:
          printMessage(msgName, message)
          nonHandle('Message Name is not handled - ' + msgName)
      }
    })
  })

  spinner.start()
  const relationship = new sdk.protocols.v1_0.Relationship(relDID, threadId)

  await relationship.outOfBandInvitation(context)
  await secondStep // wait for acceptance from connect.me user
  return relDID // return owning DID for the connection
}

//* ***********************
//       CONNECTION
//* ***********************
async function createConnection () {
  // Connecting protocol is started from the Holder's side (ConnectMe)
  // by scanning the QR code containing connection invitation
  // Connection is established when the Holder accepts the connection on the device
  // i.e. when the RESPONSE_SENT control message is received

  // Constructor for the Connecting API
  const connecting = new sdk.protocols.v1_0.Connecting()
  const spinner = new Spinner('Waiting to respond to connection ... %s').setSpinnerDelay(450) // Console spinner

  // handler for messages in Connecting protocol
  const connection = new Promise((resolve) => {
    handlers.addHandler(connecting.msgFamily, connecting.msgFamilyVersion, async (msgName, message) => {
      switch (msgName) {
        case connecting.msgNames.REQUEST_RECEIVED:
          printMessage(msgName, message)
          break
        case connecting.msgNames.RESPONSE_SENT:
          spinner.stop()
          printMessage(msgName, message)
          resolve(null)
          break
        default:
          printMessage(msgName, message)
          nonHandle('Message Name is not handled - ' + msgName)
      }
    })
    // handler for relationship-reused message which is sent by the ConnectMe app if it is already connected with this example app agent
    handlers.addHandler('out-of-band', '1.0', async (msgName, message) => {
      switch (msgName) {
        case 'relationship-reused':
          printMessage(msgName, message)
          console.log('The mobile wallet app signalled that it already has the connection with this example app agent')
          console.log('This example app does not support relationship-reuse since it does not store the data about previous relationships')
          console.log('Please delete existing connection in your mobile wallet and re-run this application')
          console.log('To learn how relationship-reuse can be used check out "ssi-auth" or "out-of-band" sample apps')
          process.exit(1)
        default:
          printMessage(msgName, message)
          nonHandle('Message Name is not handled - ' + msgName)
      }
    })
  })

  spinner.start()
  // starts the connecting protocol
  await connection // wait for response from verity application
}

//* ***********************
//        QUESTION
//* ***********************
async function askQuestion (forDID) {
  const questionText = 'Hi Alice, how are you today?'
  const questionDetail = 'Checking up on you today.'
  const validAnswers = ['Great!', 'Not so good.']

  const committedAnswer = new sdk.protocols.CommittedAnswer(forDID, null, questionText, null, questionDetail, validAnswers, true)
  const spinner = new Spinner('Waiting for Connect.Me to answer the question ... %s').setSpinnerDelay(450) // Console spinner

  const firstStep = new Promise((resolve) => {
    handlers.addHandler(committedAnswer.msgFamily, committedAnswer.msgFamilyVersion, async (msgName, message) => {
      switch (msgName) {
        case committedAnswer.msgNames.ANSWER_GIVEN:
          spinner.stop()
          printMessage(msgName, message)

          resolve(null)
          break
        default:
          printMessage(msgName, message)
          nonHandle('Message Name is not handled - ' + msgName)
      }
    })
  })
  spinner.start()
  await committedAnswer.ask(context)
  return firstStep
}

//* ***********************
//        SCHEMA
//* ***********************
async function writeLedgerSchema () {
  // input parameters for schema
  const schemaName = 'Diploma ' + uuidv4().substring(0, 8)
  const schemaVersion = '0.1'
  const schemaAttrs = ['name', 'degree']

  // constructor for the Write Schema protocol
  const schema = new sdk.protocols.WriteSchema(schemaName, schemaVersion, schemaAttrs)
  const spinner = new Spinner('Waiting to write schema to ledger ... %s').setSpinnerDelay(450) // Console spinner

  // handler for message received when schema is written
  const firstStep = new Promise((resolve) => {
    handlers.addHandler(schema.msgFamily, schema.msgFamilyVersion, async (msgName, message) => {
      switch (msgName) {
        case 'status-report':
          spinner.stop()
          printMessage(msgName, message)
          resolve(message.schemaId)
          break
        case 'needs-endorsement':
          spinner.stop()
          printMessage(msgName, message)
          console.log('Please manually endorse this transaction on the ledger: ')
          console.log(message.schemaJson)
          console.log('If you do not have a valid endorser DID send the transaction to Evernym for endorsement.')
          await readlineInput('Press ENTER when the transaction has been endorsed')
          resolve(message.schemaId)
          break
        default:
          printMessage(msgName, message)
          nonHandle('Message Name is not handled - ' + msgName)
      }
    })
  })

  spinner.start()
  // request schema be written to ledger
  await schema.write(context) // wait for operation to be complete
  return firstStep // returns ledger schema identifier
}

//* ***********************
//        CRED DEF
//* ***********************
async function writeLedgerCredDef (schemaId) {
  // input parameters for cred definition
  const credDefName = 'Trinity College Diplomas'
  const credDefTag = 'latest'

  // constructor for the Write Credential Definition protocol
  const def = new sdk.protocols.WriteCredentialDefinition(credDefName, schemaId, credDefTag)
  const spinner = new Spinner('Waiting to write cred def to ledger ... %s').setSpinnerDelay(450) // Console spinner

  // handler for message received when schema is written
  const firstStep = new Promise((resolve) => {
    handlers.addHandler(def.msgFamily, def.msgFamilyVersion, async (msgName, message) => {
      switch (msgName) {
        case 'status-report':
          spinner.stop()
          printMessage(msgName, message)
          resolve(message.credDefId)
          break
        case 'needs-endorsement':
          spinner.stop()
          printMessage(msgName, message)
          console.log('Please manually endorse this transaction on the ledger: ')
          console.log(message.credDefJson)
          console.log('If you do not have a valid endorser DID send the transaction to Evernym for endorsement.')
          await readlineInput('Press ENTER when the transaction has been endorsed')
          resolve(message.credDefId)
          break
        default:
          printMessage(msgName, message)
          nonHandle('Message Name is not handled - ' + msgName)
      }
    })
  })

  spinner.start()
  // request the cred def be writen to ledger
  await def.write(context) // wait for operation to be complete and returns ledger cred def identifier
  return firstStep
}

//* ***********************
//         ISSUE
//* ***********************
async function issueCredential (relDID, defId) {
  // input parameters for issue credential
  const credentialName = 'Degree'
  const credentialData = {
    name: 'Alice Smith',
    degree: 'Bachelors'
  }

  // constructor for the Issue Credential protocol
  const issue = new sdk.protocols.v1_0.IssueCredential(relDID, null, defId, credentialData, credentialName, 0, true)
  const spinner = new Spinner('Wait for Connect.me to accept the Credential Offer ... %s').setSpinnerDelay(450) // Console spinner

  // handler for 'sent` message when the offer is sent
  const offerSent = new Promise((resolve) => {
    handlers.addHandler(issue.msgFamily, issue.msgFamilyVersion, async (msgName, message) => {
      switch (msgName) {
        case issue.msgNames.SENT:
          spinner.stop()
          printMessage(msgName, message)

          resolve(null)
          break
        default:
          printMessage(msgName, message)
          nonHandle('Message Name is not handled - ' + msgName)
      }
    })
  })

  spinner.start()
  // request that credential is offered
  await issue.offerCredential(context)
  await offerSent // wait for offer to be sent

  // handler for 'sent` message when the offer for credential is accepted and credential sent
  const credIssued = new Promise((resolve) => {
    handlers.addHandler(issue.msgFamily, issue.msgFamilyVersion, async (msgName, message) => {
      switch (msgName) {
        case issue.msgNames.SENT:
          spinner.stop()
          printMessage(msgName, message)

          resolve(null)
          break
        default:
          printMessage(msgName, message)
          nonHandle('Message Name is not handled - ' + msgName)
      }
    })
  })

  spinner.start()
  await credIssued // wait for connect.me user to accept offer and credential to be sent immediately after.
  return sleep(3000) // Wait a few seconds for the credential to arrive before sending the proof
}

//* ***********************
//         PROOF
//* ***********************
async function requestProof (relDID) {
  // input parameters for request proof
  const proofName = 'Proof of Degree'
  const proofAttrs = [
    {
      name: 'name',
      restrictions: [{ issuer_did: issuerDID }]
    },
    {
      name: 'degree',
      restrictions: [{ issuer_did: issuerDID }]
    }
  ]

  // constructor for the Present Proof protocol
  const proof = new sdk.protocols.v1_0.PresentProof(relDID, null, proofName, proofAttrs)
  const spinner = new Spinner('Waiting for proof presentation from Connect.me ... %s').setSpinnerDelay(450) // Console spinner

  // handler for the result of the proof presentation
  const firstStep = new Promise((resolve) => {
    handlers.addHandler(proof.msgFamily, proof.msgFamilyVersion, async (msgName, message) => {
      switch (msgName) {
        case proof.msgNames.PRESENTATION_RESULT:
          spinner.stop()
          printMessage(msgName, message)

          resolve(null)
          break
        default:
          printMessage(msgName, message)
          nonHandle('Message Name is not handled - ' + msgName)
      }
    })
  })
  spinner.start()

  // request proof
  await proof.request(context)
  return firstStep // wait for connect.me user to present the requested proof
}

//* ***********************
//         SETUP
//* ***********************
async function setup () {
  let config = ''
  if (fs.existsSync(CONFIG_PATH)) {
    if (await readlineYesNo('Reuse Verity Context (in ' + CONFIG_PATH + ')', true)) {
      config = fs.readFileSync(CONFIG_PATH)
    } else {
      try {
        await vdrtools.deleteWallet({ id: WALLET_NAME }, { key: WALLET_KEY })
      } catch (e) {
        if (e.message !== 'WalletNotFoundError') {
          throw (e)
        }
      }
    }
  }

  if (config) {
    context = await sdk.Context.createWithConfig(config)
  } else {
    context = await provisionAgent()
  }

  fs.writeFileSync(CONFIG_PATH, JSON.stringify(context.getConfig()))

  await updateWebhookEndpoint()

  printObject(context.getConfig(), '>>>', 'Context Used:')

  fs.writeFileSync(CONFIG_PATH, JSON.stringify(context.getConfig()))

  await updateConfigs()

  await issuerIdentifier()

  if (issuerDID == null) {
    console.log('\nIssuer DID is not created. Performing Issuer setup now...')
    await setupIssuer()
  } else {
    console.log(`Issuer DID:  ${ANSII_GREEN}${issuerDID}${ANSII_RESET}`)
    console.log(`Issuer Verkey: ${ANSII_GREEN}${issuerVerkey}${ANSII_RESET}`)
  }
}

async function provisionAgent () {
  let token = null
  if (await readlineYesNo('Provide Provision Token', true)) {
    token = await readlineInput('Token', process.env.TOKEN)
    token.trim()
    console.log(`Using provision token: ${ANSII_GREEN}${token}${ANSII_RESET}`)
  }

  let verityUrl = await readlineInput('Verity Application Endpoint', process.env.VERITY_SERVER)
  verityUrl = verityUrl.trim()
  if (verityUrl === '') {
    verityUrl = 'http://localhost:9000'
  }

  console.log(`Using Verity Application Endpoint Url: ${ANSII_GREEN}${verityUrl}${ANSII_RESET}`)

  // create initial Context
  const ctx = await sdk.Context.create(WALLET_NAME, WALLET_KEY, verityUrl)
  console.log('wallet created')
  const provision = new sdk.protocols.v0_7.Provision(null, token)
  // console.log(`provision object ${JSON.stringify(provision)}`)

  // ask that an agent by provision (setup) and associated with created key pair
  try {
    const provisioningResponse = await provision.provision(ctx)
    return provisioningResponse
  } catch (e) {
    console.log(e)
    console.log('Provisioning failed! Likely causes:')
    console.log('- token not provided but Verity Endpoint requires it')
    console.log('- token provided but is invalid or expired')
    process.exit(1)
  }
}

async function updateWebhookEndpoint () {
  const webhookFromCtx = context.endpointUrl

  let webhook = await readlineInput(`Ngrok endpoint for port(${LISTENING_PORT})[${webhookFromCtx}]`, process.env.WEBHOOK_URL)
  if (webhook === '') {
    webhook = webhookFromCtx
  }

  console.log(`Using Webhook: ${ANSII_GREEN}${webhook}${ANSII_RESET}`)
  context.endpointUrl = webhook

  // request that verity application use specified webhook endpoint
  await new sdk.protocols.UpdateEndpoint().update(context)
}

async function updateConfigs () {
  const updateConfigs = new sdk.protocols.UpdateConfigs(INSTITUTION_NAME, LOGO_URL)
  await updateConfigs.update(context)
}

async function setupIssuer () {
  // constructor for the Issuer Setup protocol
  const issuerSetup = new sdk.protocols.IssuerSetup()
  const spinner = new Spinner('Waiting for setup to complete ... %s').setSpinnerDelay(450) // Console spinner

  // handler for created issuer identifier message
  const step = new Promise((resolve) => {
    handlers.addHandler(issuerSetup.msgFamily, issuerSetup.msgFamilyVersion, async (msgName, message) => {
      switch (msgName) {
        case issuerSetup.msgNames.PUBLIC_IDENTIFIER_CREATED: {
          spinner.stop()
          printMessage(msgName, message)
          issuerDID = message.identifier.did
          issuerVerkey = message.identifier.verKey
          console.log(`Issuer DID:  ${ANSII_GREEN}${issuerDID}${ANSII_RESET}`)
          console.log(`Issuer Verkey: ${ANSII_GREEN}${issuerVerkey}${ANSII_RESET}`)
          console.log('The issuer DID and Verkey must be registered on the ledger.')
          const automatedRegistration = false// await readlineYesNo(`Attempt automated registration via ${ANSII_GREEN}https://selfserve.sovrin.org${ANSII_RESET}`, true)
          if (automatedRegistration) {
            const res = await request.post({
              uri: 'https://selfserve.sovrin.org/nym',
              json: {
                network: 'stagingnet',
                did: issuerDID,
                verkey: issuerVerkey,
                paymentaddr: ''
              }
            })
            if (res.statusCode !== 200) {
              console.log('Something went wrong with contactig Sovrin portal')
              console.log('Please add Issuer DID and Verkey to the ledger manually')
              await readlineInput('Press ENTER when DID is on ledger')
            } else {
              console.log(`Got response from Sovrin portal: ${ANSII_GREEN}${res.body}${ANSII_RESET}`)
            }
          } else {
            console.log('Automated registration is currently unavailable')
            console.log('Please add Issuer DID and Verkey to the ledger manually')
            await readlineInput('Press ENTER when DID is on ledger')
          }
          resolve(null)
          break
        }
        default:
          printMessage(msgName, message)
          nonHandle('Message Name is not handled - ' + msgName)
      }
    })
  })

  spinner.start()
  // request that issuer identifier be created
  await issuerSetup.create(context)
  return step // wait for request to complete
}

async function issuerIdentifier () {
  // constructor for the Issuer Setup protocol
  const issuerSetup = new sdk.protocols.IssuerSetup()
  const spinner = new Spinner('Waiting for current issuer DID ... %s').setSpinnerDelay(450)

  // handler for current issuer identifier message
  const step = new Promise((resolve) => {
    handlers.addHandler(issuerSetup.msgFamily, issuerSetup.msgFamilyVersion, async (msgName, message) => {
      spinner.stop()
      switch (msgName) {
        case issuerSetup.msgNames.PUBLIC_IDENTIFIER:
          printMessage(msgName, message)
          issuerDID = message.did
          issuerVerkey = message.verKey
          break
      }
      resolve(null)
    })
  })

  spinner.start()
  // query the current identifier
  await issuerSetup.currentPublicIdentifier(context)
  return step // wait for response from verity application
}

//* ***********************
//         MAIN
//* ***********************
main()

async function main () {
  await start()
  await example()
  await end()
}

async function start () {
  const app = express()
  app.use(express.text({
    type: function (_) {
      return 'text'
    }
  }))

  app.post('/', async (req, res) => {
    await handlers.handleMessage(context, Buffer.from(req.body, 'utf8'))
    res.send('Success')
  })

  listener = http.createServer(app).listen(LISTENING_PORT)
  console.log(`Listening on port ${LISTENING_PORT}`)
}

async function end () {
  listener.close()
  rl.close()
  process.exit(0)
}

//* ***********************
//         UTILS
//* ***********************

// Simple utility functions for the Example app.

async function readlineInput (request, defaultValue) {
  console.log()

  return new Promise((resolve) => {
    if (defaultValue) {
      console.log(`${request}:`)
      console.log(`${ANSII_GREEN}${defaultValue}${ANSII_RESET} is set via environment variable`)
      rl.question('Press any key to continue', (response) => { resolve(defaultValue) })
    } else {
      rl.question(request + ': ', (response) => { resolve(response) })
    }
  })
}

async function readlineYesNo (request, defaultYes) {
  const yesNo = defaultYes ? '[y]/n' : 'y/n'
  const modifiedRequest = request + '? ' + yesNo + ': '

  return new Promise((resolve) => {
    rl.question(modifiedRequest, (response) => {
      const normalized = response.trim().toLocaleLowerCase()
      if (defaultYes && normalized === '') {
        resolve(true)
      } else if (normalized === 'y') {
        resolve(true)
      } else if (normalized === 'n') {
        resolve(false)
      } else {
        console.error("Did not get a valid response -- '" + response + "' is not y or n")
        process.exit(-1)
      }
    })
  })
}

function printMessage (msgName, msg) {
  printObject(msg, '<<<', `Incomming Message -- ${msgName}`)
}

function printObject (obj, prefix, preamble) {
  console.log()
  console.log(prefix + '  ' + preamble)
  const lines = JSON.stringify(obj, null, 2).split('\n')
  lines.forEach(line => {
    console.log(prefix + '  ' + line)
  })
  console.log()
}

function nonHandle (msg) {
  console.error(msg)
  process.exit(-1)
}

function sleep (ms) {
  return new Promise(resolve => setTimeout(resolve, ms))
}

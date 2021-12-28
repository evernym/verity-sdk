const fs = require('fs')
const express = require('express')
const readline = require('readline')
const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
})
const request = require('request-promise-native')
const { v4: uuidv4 } = require('uuid')
const QR = require('qrcode')

const sdk = require('verity-sdk')
const handlers = new sdk.Handlers()

const ISSUER_NAME = 'Issuer'
const ISSUER_LOGO = 'https://freeiconshop.com/wp-content/uploads/edd/bank-flat.png'
const VERIFIER_NAME = 'Verifier'
const VERIFIER_LOGO = 'https://freeiconshop.com/wp-content/uploads/edd/bank-flat.png'
const ISSUER_CONFIG_FILE = 'issuer-context.json'
const VERIFIER_CONFIG_FILE = 'verifier-context.json'
const CRED_DEF_FILE = 'cred_def_id.txt'
const LISTENING_PORT = 4000
const ANSII_GREEN = '\u001b[32m'
const ANSII_RESET = '\x1b[0m'

let issuerContext
let verifierContext
let credDefId

// Maps containing unresolved promises for the started interactions. The threadId is used as the map key
// Issuer setup promises
const issuerSetupMap = new Map()
// Schema create promises
const schemaMap = new Map()
// Credential definition create promises
const credDefMap = new Map()
// Relationship create promises
const relCreateMap = new Map()
// Credential issuance protocol invitation promises
const issueProtInviteMap = new Map()
// Credential issuance credential sent promises
const issueCredSentMap = new Map()
// Proof request protocol invitation promises
const proofProtInviteMap = new Map()
// Proof request presentation sent promises
const proofPresentationSentMap = new Map()

// Initialize function is run at the application start up
// It will create Issuer context, Verifier context, schema and credential definition in the first-time run
// It will reload Issuer and Verifier contexts and cred_def_id from files in subsequent runs
async function initialize () {
  registerHandlers()

  if (fs.existsSync(ISSUER_CONFIG_FILE)) {
    const issuerConfig = fs.readFileSync(ISSUER_CONFIG_FILE)
    issuerContext = await sdk.Context.createWithConfig(issuerConfig)
    await updateWebhookEndpoint(issuerContext, 'issuer')
  } else {
    await provisionIssuer()
  }
  if (fs.existsSync(VERIFIER_CONFIG_FILE)) {
    const verifierConfig = fs.readFileSync(VERIFIER_CONFIG_FILE)
    verifierContext = await sdk.Context.createWithConfig(verifierConfig)
    await updateWebhookEndpoint(verifierContext, 'verifier')
  } else {
    await provisionVerifier()
  }
  if (fs.existsSync(CRED_DEF_FILE)) {
    credDefId = fs.readFileSync(CRED_DEF_FILE, 'utf8')
  } else {
    console.log('ERROR: Credential Definition was not created')
    process.exit(1)
  }
}

// Provisions Issuer
// It will create Issuer context, update webhook, setup Issuer DID/Verkey and write it to the ledger
// It will then create schema and credential definition needed for the credential issuance
async function provisionIssuer () {
  console.log('Provisioning Issuer...')
  issuerContext = await provisionAgent('issuer')
  printObject(issuerContext.getConfig(), '>>>', 'Issuer context:')

  await updateWebhookEndpoint(issuerContext, 'issuer')

  const updateConfigs = new sdk.protocols.UpdateConfigs(ISSUER_NAME, ISSUER_LOGO)
  await updateConfigs.update(issuerContext)

  const [issuerDID, issuerVerkey] = await setupIssuerKeys(issuerContext)

  await writeIssuerKeysOnLedger(issuerDID, issuerVerkey)

  const schemaId = await writeLedgerSchema()
  console.log(`Schema Id: ${ANSII_GREEN}${schemaId}${ANSII_RESET}`)

  const credDefId = await writeLedgerCredDef(schemaId)
  console.log(`Cred Def Id: ${ANSII_GREEN}${credDefId}${ANSII_RESET}`)
  fs.writeFileSync(CRED_DEF_FILE, credDefId)

  fs.writeFileSync(ISSUER_CONFIG_FILE, JSON.stringify(issuerContext.getConfig()))
  console.log('Issuer provisioning completed')
}

// Registers webhook endpoint with Verity Application Service
// Verity will send responses back to the registered webhook address
async function updateWebhookEndpoint (context, role) {
  const ngrokAddress = await readlineInput(`Ngrok tunnel endpoint for port ${LISTENING_PORT}`, process.env.WEBHOOK_URL)
  const webhookEndpoint = `${ngrokAddress}/webhook/${role}`
  console.log(`Using webhook endpoint: ${ANSII_GREEN}${webhookEndpoint}${ANSII_RESET}`)

  context.endpointUrl = webhookEndpoint
  await new sdk.protocols.UpdateEndpoint().update(context)
}

// Creates Issuer DID and public/private keypair for the context
async function setupIssuerKeys (context) {
  const issuerSetup = new sdk.protocols.IssuerSetup()

  const setupIssuerKeysPromise =
  new Promise(function (resolve, reject) {
    issuerSetupMap.set(issuerSetup.threadId, resolve)
  })

  await issuerSetup.create(context)

  return setupIssuerKeysPromise
}

// In order to issue credentials Issuer DID and public key (Verkey) need to be written to the ledger
// This function writes Issuer DID/Verkey to the Sovrin Staging Net using Sovrin SelfServe portal
async function writeIssuerKeysOnLedger (issuerDID, issuerVerkey) {
  console.log('The issuer DID and Verkey must be registered on the ledger.')
  const automatedRegistration = await readlineYesNo(`Attempt automated registration via ${ANSII_GREEN}https://selfserve.sovrin.org${ANSII_RESET}`, true)
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
    console.log('Please add Issuer DID and Verkey to the ledger manually')
    await readlineInput('Press ENTER when DID is on ledger')
  }
}

// Writes schema on the ledger
async function writeLedgerSchema () {
  const schemaName = 'Diploma ' + uuidv4().substring(0, 8)
  const schemaVersion = '0.1'
  const schemaAttrs = ['name', 'degree']

  const schemaCreate = new sdk.protocols.WriteSchema(schemaName, schemaVersion, schemaAttrs)

  const schemaPromise = new Promise((resolve, reject) => {
    schemaMap.set(schemaCreate.threadId, resolve)
  })

  await schemaCreate.write(issuerContext)

  return schemaPromise
}

// Writes credential definition on the ledger
async function writeLedgerCredDef (schemaId) {
  const credDefName = 'Trinity College Diplomas'
  const credDefTag = 'latest'

  const credDefCreate = new sdk.protocols.WriteCredentialDefinition(credDefName, schemaId, credDefTag)

  const credDefPromise = new Promise((resolve) => {
    credDefMap.set(credDefCreate.threadId, resolve)
  })

  await credDefCreate.write(issuerContext)

  return credDefPromise
}

// provisions an agent on the Verity Application Service
// This function will consume the provisioning token and will return the context
// NOTE: The context and the indy wallet ($HOME/.indy_client/wallet) need to be persisted to re-gain access to your agent
async function provisionAgent (role) {
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

  const ctx = await sdk.Context.create(`${role}_wallet`, `${role}_wallet_key`, verityUrl)

  const provision = new sdk.protocols.v0_7.Provision(null, token)

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

// Provisions Verifier
// Create Verifier context, update webhook, setup Issuer keys and persist the context to a file
async function provisionVerifier () {
  console.log('Provision Verifier')
  verifierContext = await provisionAgent('verifier')
  printObject(verifierContext.getConfig(), '>>>', 'Verifier context:')

  await updateWebhookEndpoint(verifierContext, 'verifier')

  const updateConfigs = new sdk.protocols.UpdateConfigs(VERIFIER_NAME, VERIFIER_LOGO)
  await updateConfigs.update(verifierContext)

  // Although we will not be issuing credentials with the Verifier, we should perform setupIssuerKeys step if we want to enable connection reuse
  // If Issuer DID is created for the context it will be included in the inviteURL (as the "public_did" field)
  // The ConnectMe app uses the "public_did" field from the connection invitation to check if it already has the connection with the Inviter
  // If this step is skipped (i.e. Issuer DID is not created) it would be possible to establish multiple connections with the Verifier in ConnectMe app
  const [issuerDID, issuerVerkey] = await setupIssuerKeys(verifierContext)

  fs.writeFileSync(VERIFIER_CONFIG_FILE, JSON.stringify(verifierContext.getConfig()))
  console.log('Verifier provisioning completed')
}

// Create a new relationship and return relationship DID
async function createRelationship (role) {
  const relProvisioning = new sdk.protocols.v1_0.Relationship()

  const relationshipCreate =
    new Promise(function (resolve) {
      relCreateMap.set(relProvisioning.threadId, resolve)
    })

  if (role === 'issuer') {
    await relProvisioning.create(issuerContext)
  } else {
    await relProvisioning.create(verifierContext)
  }
  const relDID = await relationshipCreate

  return relDID
}

// This function registers handlers for the messages received from Verity Application Service
function registerHandlers () {
  const issuerSetup = new sdk.protocols.IssuerSetup()
  handlers.addHandler(issuerSetup.msgFamily, issuerSetup.msgFamilyVersion, async (msgName, message) => {
    switch (msgName) {
      case issuerSetup.msgNames.PUBLIC_IDENTIFIER_CREATED: {
        printMessage(msgName, message)
        const threadId = message['~thread'].thid
        const issuerDID = message.identifier.did
        const issuerVerkey = message.identifier.verKey
        console.log(`Issuer DID:  ${ANSII_GREEN}${issuerDID}${ANSII_RESET}`)
        console.log(`Issuer Verkey: ${ANSII_GREEN}${issuerVerkey}${ANSII_RESET}`)
        issuerSetupMap.get(threadId)([issuerDID, issuerVerkey])
      }
        break
      default:
        printMessage(msgName, message)
        nonHandle('Message Name is not handled - ' + msgName)
    }
  })

  const schema = new sdk.protocols.WriteSchema('dummy', '0.1', ['dummy'])
  handlers.addHandler(schema.msgFamily, schema.msgFamilyVersion, async (msgName, message) => {
    const threadId = message['~thread'].thid
    switch (msgName) {
      case schema.msgNames.STATUS:
        printMessage(msgName, message)
        schemaMap.get(threadId)(message.schemaId)
        break
      default:
        printMessage(msgName, message)
        nonHandle('Message Name is not handled - ' + msgName)
    }
  })

  const def = new sdk.protocols.WriteCredentialDefinition()
  handlers.addHandler(def.msgFamily, def.msgFamilyVersion, async (msgName, message) => {
    const threadId = message['~thread'].thid
    switch (msgName) {
      case def.msgNames.STATUS:
        printMessage(msgName, message)
        credDefMap.get(threadId)(message.credDefId)
        break
      default:
        printMessage(msgName, message)
        nonHandle('Message Name is not handled - ' + msgName)
    }
  })

  const relProvisioning = new sdk.protocols.v1_0.Relationship()
  handlers.addHandler(relProvisioning.msgFamily, relProvisioning.msgFamilyVersion, async (msgName, message) => {
    const threadId = message['~thread'].thid
    switch (msgName) {
      case relProvisioning.msgNames.CREATED:
        printMessage(msgName, message)
        relCreateMap.get(threadId)(message.did)
        break
      default:
        printMessage(msgName, message)
        nonHandle('Message Name is not handled - ' + msgName)
    }
  })

  // add handler for Connecting protocol
  // Connecting protocol is initiated from the ConnectMe app after the QR code with the inviteURL is scanned
  const connecting = new sdk.protocols.v1_0.Connecting()
  handlers.addHandler(connecting.msgFamily, connecting.msgFamilyVersion, async (msgName, message) => {
    switch (msgName) {
      case connecting.msgNames.REQUEST_RECEIVED:
        printMessage(msgName, message)
        break
      case connecting.msgNames.RESPONSE_SENT:
        printMessage(msgName, message)
        break
      default:
        printMessage(msgName, message)
        nonHandle('Message Name is not handled - ' + msgName)
    }
  })

  const issue = new sdk.protocols.v1_0.IssueCredential()
  handlers.addHandler(issue.msgFamily, issue.msgFamilyVersion, async (msgName, message) => {
    const threadId = message['~thread'].thid
    switch (msgName) {
      case issue.msgNames.PROTOCOL_INVITATION:
        printMessage(msgName, message)
        issueProtInviteMap.get(threadId)(message.shortInviteURL)
        break
      case issue.msgNames.SENT:
        printMessage(msgName, message)
        issueCredSentMap.get(threadId)(null)
        break
      default:
        printMessage(msgName, message)
        nonHandle('Message Name is not handled - ' + msgName)
    }
  })

  const proof = new sdk.protocols.v1_0.PresentProof()
  handlers.addHandler(proof.msgFamily, proof.msgFamilyVersion, async (msgName, message) => {
    const threadId = message['~thread'].thid
    switch (msgName) {
      case proof.msgNames.PROTOCOL_INVITATION:
        printMessage(msgName, message)
        proofProtInviteMap.get(threadId)(message.shortInviteURL)
        break
      case proof.msgNames.PRESENTATION_RESULT:
        printMessage(msgName, message)
        proofPresentationSentMap.get(threadId)([message.verification_result, message.requested_presentation])
        break
      default:
        printMessage(msgName, message)
        nonHandle('Message Name is not handled - ' + msgName)
    }
  })
}

async function main () {
  const app = express()

  // Serve static files (F/E) contained in the 'public' folder
  app.use(express.static('public'))

  app.use(express.text({
    type: function (_) {
      return 'text'
    }
  }))

  // Webhook endpoint for responses received from VAS
  // role could be either 'issuer' or 'verifier'
  app.post('/webhook/:role', async (req, res) => {
    const role = req.params.role
    if (role === 'issuer') {
      await handlers.handleMessage(issuerContext, Buffer.from(req.body, 'utf8'))
    } else {
      await handlers.handleMessage(verifierContext, Buffer.from(req.body, 'utf8'))
    }
    res.send('Success')
  })

  // route called by F/E to get the QR code for the out-of-band credential issuance
  // QR code will contain connection invitation with credential offer attached
  app.post('/issueCredential', async (req, res) => {
    const relDID = await createRelationship('issuer')
    console.log(`Relationship DID: ${relDID}`)

    const credentialName = 'Degree'
    const credentialData = {
      name: 'Alice Smith',
      degree: 'Bachelors'
    }

    // constructor for the Issue Credential protocol. The field 'byInvitation' is set to 'true'
    const issue = new sdk.protocols.v1_0.IssueCredential(relDID, null, credDefId, credentialData, credentialName, 0, true, true)

    // Set promise for the 'protocol-invitation' message expected when the credential is offered
    const protocolInvitationPromise = new Promise((resolve) => {
      issueProtInviteMap.set(issue.threadId, resolve)
    })

    // offer the credential
    await issue.offerCredential(issuerContext)

    // Get inviteURL from the resolved protocol-invitation promise and send it to F/E
    const inviteURL = await protocolInvitationPromise
    res.status(200).send(await QR.toDataURL(inviteURL))

    // wait till the user scans the QR code and accepts credentials
    const credentialSentPromise = new Promise((resolve) => {
      issueCredSentMap.set(issue.threadId, resolve)
    })

    await credentialSentPromise
    // At this time F/E could be notified that the credential has been issued
    // e.g. A notification to F/E could be sent via IO sockets
    // Alternatively, polling for the credential status could be implemented on F/E
  })

  // route called by F/E to get the QR code for the out-of-band proof request
  // QR code will contain connection invitation with proof request attached
  app.post('/requestProof', async (req, res) => {
    const relDID = await createRelationship('verifier')
    console.log(`Relationship DID: ${relDID}`)
    const proofName = 'Proof of Degree'
    const proofAttrs = [
      {
        name: 'name',
        restrictions: [{ cred_def_id: credDefId }]
      },
      {
        name: 'degree',
        restrictions: [{ cred_def_id: credDefId }]
      }
    ]

    // constructor for the Present Proof protocol. The field 'byInvitation' is set to 'true'
    const proof = new sdk.protocols.v1_0.PresentProof(relDID, null, proofName, proofAttrs, null, true)

    // Set promise for the 'protocol-invitation' message expected when the proof request is sent
    const protocolInvitationPromise = new Promise((resolve) => {
      proofProtInviteMap.set(proof.threadId, resolve)
    })

    // send the proof request
    await proof.request(verifierContext)

    // Get inviteURL from the resolved protocol-invitation promise and send it to F/E
    const inviteURL = await protocolInvitationPromise
    res.status(200).send(await QR.toDataURL(inviteURL))

    // wait till the user scans the QR code and shares the proof response
    const presentationSentPromise = new Promise((resolve) => {
      proofPresentationSentMap.set(proof.threadId, resolve)
    })

    const [verificationResult, requestedPresentation] = await presentationSentPromise
    if (verificationResult === 'ProofValidated') {
      console.log('Proof is validated')
      console.log(`Recieved data:\n${JSON.stringify(requestedPresentation, null, 4)}`)
    } else {
      console.log('Proof is invalid')
    }
    // At this time F/E could be notified with the verification results
    // e.g. A notification to F/E could be sent via IO sockets
    // Alternatively, polling for the proof status could be implemented on F/E
  })

  app.listen(LISTENING_PORT, async () => {
    await initialize()
    console.log(`\nListening on port ${LISTENING_PORT}`)
  })
}

main()

//* ***********************
//         UTILS
//* ***********************

// Simple utility functions for read from terminal input or write to terminal output

async function readlineInput (request, defaultValue) {
  console.log()

  return new Promise((resolve) => {
    if (defaultValue) {
      console.log(`${request}:`)
      console.log(`${ANSII_GREEN}${defaultValue}${ANSII_RESET} is set via environment variable`)
      resolve(defaultValue)
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

'use strict'
const express = require('express')
const http = require('http')
const bodyParser = require('body-parser')
const readline = require('readline')
const fs = require('fs')
const sdk = require('./src/index')

const LISTENING_PORT = 4507
const CONFIG_PATH = 'verityConfig.json'
const CONNECTION_CACHE = 'connectionId.txt'
const VERITY_URL = 'http://localhost:9000'
// const VERITY_URL = 'http://vas-team1.pdev.evernym.com'
const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
})

let credDefId
let connectionId

exampleFlow()

async function exampleFlow () {
  const handlers = new sdk.Handlers()
  handlers.setDefaultHandler(defaultHandler)

  var app = express()
  app.use(bodyParser.text({
    type: function (_) {
      return 'text'
    }
  }))
  app.post('/', async (req, res) => {
    console.log('Handling new message')
    await handlers.handleMessage(context, Buffer.from(req.body, 'utf8'))
    res.send('Success')
  })
  http.createServer(app).listen(LISTENING_PORT)

  // Provision Protocol. Delete verityConfig.json to reprovision
  let context
  if (fs.existsSync(CONFIG_PATH)) { // If provisioning has already happened
    context = await sdk.Context.createWithConfig(fs.readFileSync(CONFIG_PATH)) // Read config from file
    await updateTestEndpoint()
    await writeTestSchema()
  } else {
    context = await sdk.Context.create(sdk.utils.miniId(), '12345', VERITY_URL, 'http://localhost:' + LISTENING_PORT)
    const provision = new sdk.protocols.Provision()
    context = await provision.provisionSdk(context)
    fs.writeFileSync(CONFIG_PATH, JSON.stringify(context.getConfig()))
    await updateTestEndpoint()
    await testIssuerSetup()
  }

  // UpdateEndpoint Protocol
  async function updateTestEndpoint () {
    const updateEndpoint = new sdk.protocols.UpdateEndpoint()
    await updateEndpoint.update(context)
  }

  // IssuerSetup Protocol
  async function testIssuerSetup () {
    const issuerSetup = new sdk.protocols.IssuerSetup()
    handlers.addHandler(issuerSetup.msgFamily, issuerSetup.msgFamilyVersion, async (msgName, message) => {
      switch (msgName) {
        case issuerSetup.msgNames.PUBLIC_IDENTIFIER_CREATED:
          console.log(message.identifier)
          rl.question('IssuerSetup complete. This key needs to be written to the ledger. Press enter when done.', async () => {
            rl.close()
            console.log('Asking Verity to write Schema to ledger...')
            await writeTestSchema()
          })
          break
        default:
          defaultHandler(msgName, message)
          break
      }
    })
    await issuerSetup.create(context)
  }

  // WriteSchema Protocol
  async function writeTestSchema () {
    const schemaVersion = `${sdk.utils.randInt(1000)}.${sdk.utils.randInt(1000)}.${sdk.utils.randInt(1000)}`
    const schemaAttrs = ['name', 'birthday']
    const writeSchema = new sdk.protocols.WriteSchema('testSchema', schemaVersion, schemaAttrs)
    if (!handlers.hasHandler(writeSchema.msgFamily, writeSchema.msgFamilyVersion)) {
      handlers.addHandler(writeSchema.msgFamily, writeSchema.msgFamilyVersion, async (msgName, message) => {
        switch (msgName) {
          case writeSchema.msgNames.STATUS:
            if ('schemaId' in message) {
              const schemaId = message.schemaId
              console.log(`Schema successfully written to ledger. SchemaId = "${schemaId}"`)
              await writeTestCredDef(schemaId)
            }
            break
          default:
            defaultHandler(msgName, message)
            break
        }
      })
    }
    await writeSchema.write(context)
  }

  // WriteCredentialDefinition Protocol
  async function writeTestCredDef (schemaId) {
    const writeCredDef = new sdk.protocols.WriteCredentialDefinition('testCredDef', schemaId)
    if (!handlers.hasHandler(writeCredDef.msgFamily, writeCredDef.msgFamilyVersion)) {
      handlers.addHandler(writeCredDef.msgFamily, writeCredDef.msgFamilyVersion, async (msgName, message) => {
        switch (msgName) {
          case writeCredDef.msgNames.STATUS:
            credDefId = message.credDefId
            console.log(`Credential Definition successfully written to ledger. credDefId = ${credDefId}`)
            await connectWithConnectMe()
            break
          default:
            defaultHandler(msgName, message)
            break
        }
      })
    }
    await writeCredDef.write(context)
  }

  // Connecting Protocol
  async function connectWithConnectMe () {
    const connecting = new sdk.protocols.Connecting()
    if (!handlers.hasHandler(connecting)) {
      handlers.addHandler(connecting.msgFamily, connecting.msgFamilyVersion, async (msgName, message) => {
        switch (msgName) {
          case connecting.msgNames.INVITE_DETAIL:
            console.log(`Invitation Detail: ${sdk.utils.truncateInviteDetailKeys(message.inviteDetail)}`)
            connectionId = message.inviteDetail.senderDetail.DID
            break
          case connecting.msgNames.CONN_REQ_ACCEPTED:
            console.log('Connection Accepted!')
            fs.writeFileSync(CONNECTION_CACHE, connectionId) // Connection
            await issueTestCredential(connectionId)
            break
          default:
            defaultHandler(msgName, message)
            break
        }
      })
    }

    if (fs.existsSync(CONNECTION_CACHE)) { // If connection already created
      connectionId = fs.readFileSync(CONNECTION_CACHE) // Get connectionId from cache
      await issueTestCredential(connectionId) // And continue to next protocol
      // await askQuestion()
    } else {
      await connecting.connect(context) // Else begin connecting protocol
    }
  }

  // IssueCredential
  async function issueTestCredential () {
    const values = {
      name: 'Jim',
      birthday: '01/01/1970'
    }
    const issueCredential = new sdk.protocols.IssueCredential(connectionId, null, 'Test Credential From Verity', values, credDefId)
    if (!handlers.hasHandler(issueCredential.msgFamily, issueCredential.msgFamilyVersion)) {
      handlers.addHandler(issueCredential.msgFamily, issueCredential.msgFamilyVersion, async (msgName, message) => {
        switch (msgName) {
          case issueCredential.msgNames.ASK_ACCEPT:
            console.log('Credential accepted. Issuing...')
            await issueCredential.issueCredential(context)
            await sendTestProofRequest()
            break
          default:
            defaultHandler(msgName, message)
            break
        }
      })
    }
    console.log('Issuing test credential')
    await issueCredential.offerCredential(context)
  }

  // PresentProof Protocol
  async function sendTestProofRequest () {
    const proofAttrs = [
      {
        name: 'name',
        restrictions: [{ cred_def_id: credDefId }]
      },
      {
        name: 'birthday',
        restrictions: [{ cred_def_id: credDefId }]
      }
    ]
    const presentProof = new sdk.protocols.PresentProof(connectionId, null, 'Requesting Proof of Test Credential', proofAttrs)
    if (!handlers.hasHandler(presentProof.msgFamily, presentProof.msgFamilyVersion)) {
      handlers.addHandler(presentProof.msgFamily, presentProof.msgFamilyVersion, async (msgName, message) => {
        switch (msgName) {
          case presentProof.msgNames.PROOF_RESULT:
            console.log('Proof received:')
            console.log(message.requestedProof)
            // await askQuestion() // AskQuestion protocol not working yet. Message never arrives at Connect.Me
            break
          default:
            defaultHandler(msgName, message)
            break
        }
      })
    }
    await presentProof.request(context)
  }

  // // CommittedAnswer Protocol
  // async function askQuestion() {
  //   const questionText = 'Hi Alice, how are you today?'
  //   const questionDetail = ' '
  //   const validResponses = ['Great!', 'Not so good.']
  //   const committedAnswer = new sdk.protocols.CommittedAnswer(connectionId, null, questionText, null, questionDetail, validResponses, true)
  //   if (!handlers.hasHandler(committedAnswer.msgFamily, committedAnswer.msgFamilyVersion)) {
  //     handlers.addHandler(committedAnswer.msgFamily, committedAnswer.msgFamilyVersion, async (msgName, message) => {
  //       switch (msgName) {
  //         default:
  //           defaultHandler(msgName, message)
  //           break
  //       }
  //     })
  //   }
  //   console.log("Asking question")
  //   await committedAnswer.ask(context)
  // }
}

async function defaultHandler (_, message) {
  console.log('Unhandled message:')
  console.log(message)
}

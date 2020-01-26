'use strict'
const express = require('express')
const http = require('http')
const bodyParser = require('body-parser')
const readline = require('readline')
const sdk = require('./src/index')

const listeningPort = 4507

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
})

exampleFlow()

async function exampleFlow () {
  const verityUrl = 'http://vas-team1.pdev.evernym.com'
  // const verityUrl = 'http://localhost:4002'
  let context = await sdk.Context.create(sdk.utils.miniId(), '12345', verityUrl, 'http://localhost:' + listeningPort)
  const handlers = new sdk.Handlers()
  handlers.setDefaultHandler(defaultHandler)

  // Provision Protocol
  const provision = new sdk.protocols.Provision()
  context = await provision.provisionSdk(context)

  // UpdateEndpoint Protocol
  const updateEndpoint = new sdk.protocols.UpdateEndpoint()
  await updateEndpoint.update(context)
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
  http.createServer(app).listen(listeningPort)

  // IssuerSetup Protocol
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
            console.log('Credential Definition successfully written to ledger. Message = ')
            console.log(message)
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

  // IssueCredential Protocol

  // PresentProof Protocol

  // CommittedAnswer Protocol
}

async function defaultHandler (msgName, message) {
  console.log('Unhandled message:')
  console.log(message)
}

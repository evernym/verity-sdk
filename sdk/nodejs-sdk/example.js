'use strict'
const express = require('express')
const http = require('http')
const bodyParser = require('body-parser')
const sdk = require('./src/index')

const listeningPort = 4507

exampleFlow()

async function exampleFlow () {
  let context = await sdk.Context.create(sdk.utils.miniId(), '12345', 'http://vas-team1.pdev.evernym.com', 'http://localhost:' + listeningPort)
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
        console.log('IssuerSetup complete, writing test schema to ledger')
        await writeTestSchema()
        break
      default:
        defaultHandler(msgName, message)
        break
    }
  })
  await issuerSetup.create(context)

  // WriteSchema Protocol
  async function writeTestSchema () {
    const schemaVersion = `${sdk.utils.randInt(0, 1000)}.${sdk.utils.randInt(0, 1000)}.${sdk.utils.randInt(0, 1000)}`
    const schemaAttrs = ['name', 'birthday']
    const writeSchema = new sdk.protocols.WriteSchema('testSchema', schemaVersion, schemaAttrs)
    if (!handlers.hasHandler(writeSchema.msgFamily, writeSchema.msgFamilyVersion)) {
      handlers.addHandler(writeSchema.msgFamily, writeSchema.msgFamilyVersion, async (msgName, message) => {
        switch (msgName) {
          case writeSchema.msgNames.STATUS:
            if (message.status === writeSchema.statuses.WRITE_SUCCESSFUL) {
              console.log('Schema written successfully to ledger')
            }
        }
      })
    }
  }

  // WriteCredentialDefinition Protocol

  // Connecting Protocol

  // IssueCredential Protocol

  // PresentProof Protocol

  // CommittedAnswer Protocol
}

async function defaultHandler (msgName, message) {
  console.log('Unhandled message:')
  console.log(message)
}

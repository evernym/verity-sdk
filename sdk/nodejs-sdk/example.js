'use strict'
const http = require('http')
const sdk = require('./src/index')

const listeningPort = 4507

exampleFlow()

async function exampleFlow () {
  let context = await sdk.Context.create(sdk.utils.miniId(), '12345', 'http://vas-team1.pdev.evernym.com', 'http://localhost:' + listeningPort)
  const handlers = new sdk.Handlers()
  handlers.defaultHandler(defaultHandler)

  // Provision Protocol
  const provision = new sdk.protocols.Provision()
  context = await provision.provisionSdk(context)

  // UpdateEndpoint Protocol
  const updateEndpoint = new sdk.protocols.UpdateEndpoint()
  await updateEndpoint.update(context)
  http.createServer(async (req, res) => {
    console.log('Handling new message')
    await handlers.handleMessage(req.body)
    res.writeHead(200, { 'Content-Type': 'text/html' })
    res.write('Success')
    res.end()
  }).listen(listeningPort)

  // IssuerSetup Protocol
  const issuerSetup = new sdk.protocols.IssuerSetup()
  handlers.addHandler(issuerSetup.msgFamily, issuerSetup.msgFamilyVersion, async (msgName, message) => {
    switch (msgName) {
      case issuerSetup.msgNames.PUBLIC_IDENTIFIER_CREATED:
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
    // const writeSchema = WriteSchema()
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

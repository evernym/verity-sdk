'use strict'

const axios = require('axios')
const bodyParser = require('body-parser')
const express = require('express')
const QR = require('qrcode')
const uuid4 = require('uuid4')

const PORT = 4000
const verityUrl = '<< PUT VERITY APPLICATION SERVER URL HERE >>' // address of Verity Application Server
const domainDid = '<< PUT DOMAIN DID HERE >>' // your Domain DID on the multi-tenant Verity Application Server
const xApiKey = '<< PUT X-API-KEY HERE >>' // REST API key associated with your Domain DID
const webhookUrl = '<< PUT WEBHOOK URL HERE >>' // public URL for the webhook endpoint

// Sends Verity REST API call to Verity Application server
async function sendVerityRESTMessage (qualifier, msgFamily, msgFamilyVersion, msgName, message, threadId) {
  // Add @type and @id fields to the message
  // Field @type is dinamycially constructed based on the function arguments and added into the message payload
  message['@type'] = `did:sov:${qualifier};spec/${msgFamily}/${msgFamilyVersion}/${msgName}`
  message['@id'] = uuid4()
  if (!threadId) {
    threadId = uuid4()
  }
  const url = `${verityUrl}/api/${domainDid}/${msgFamily}/${msgFamilyVersion}/${threadId}`
  console.log(`Posting message to ${url}`)
  console.log(message)
  return axios({
    method: 'POST',
    url: url,
    data: message,
    headers: {
      'X-API-key': xApiKey // <-- REST API Key is added in the header
    }
  })
}

// Registers issuer DID/Verkey as Endorser on Sovrin Staging Net
async function registerDid (issuerDid, issuerVerkey) {
  return axios.post('https://selfserve.sovrin.org/nym',
    {
      network: 'stagingnet',
      did: issuerDid,
      verkey: issuerVerkey,
      paymentaddr: ''
    })
}

let webhookResolve
let setupResolve
let schemaResolve
let credDefResolve
let relCreateResolve
let relInvitationResolve
let connectionResolve
let credOfferResolve

async function issuer () {
  // STEP 1 - Update webhook endpoint
  const webhookMessage = {
    comMethod: {
      id: 'webhook',
      type: 2,
      value: webhookUrl,
      packaging: {
        pkgType: 'plain'
      }
    }
  }

  const updateWebhook =
  new Promise(function (resolve, reject) {
    webhookResolve = resolve
    sendVerityRESTMessage('123456789abcdefghi1234', 'configs', '0.6', 'UPDATE_COM_METHOD', webhookMessage)
  })

  await updateWebhook

  // STEP 2 - Setup Issuer
  const setupIssuer =
    new Promise(function (resolve, reject) {
      setupResolve = resolve
      sendVerityRESTMessage('123456789abcdefghi1234', 'issuer-setup', '0.6', 'create', {})
    })
  const [issuerDid, issuerVerkey] = await setupIssuer
  // Automatic registration of DID/Verkey as Endorser using Sovrin SelfServe portal
  const sovrinResponse = await registerDid(issuerDid, issuerVerkey)
  console.log(`DID registration response from Sovrin SelfServe portal:\n${sovrinResponse.data.body}`)

  // STEP 3 - Create schema
  const schemaMessage = {
    name: 'Diploma ' + uuid4().substring(0, 8),
    version: '0.1',
    attrNames: ['name', 'degree']
  }
  const schemaCreate =
    new Promise(function (resolve, reject) {
      schemaResolve = resolve
      sendVerityRESTMessage('123456789abcdefghi1234', 'write-schema', '0.6', 'write', schemaMessage)
    })
  const schemaId = await schemaCreate
  console.log(`Created schema: ${schemaId}`)

  // STEP 4 - Create credential definition
  const credDefMessage = {
    name: 'Trinity College Diplomas',
    schemaId: schemaId,
    tag: 'latest'
  }
  const credDefCreate =
    new Promise(function (resolve, reject) {
      credDefResolve = resolve
      sendVerityRESTMessage('123456789abcdefghi1234', 'write-cred-def', '0.6', 'write', credDefMessage)
    })
  const credDefId = await credDefCreate
  console.log(`Created credential definition: ${credDefId}`)

  // STEP 5 - Relationship creation
  // create relationship key
  const relationshipCreateMessage = {
    label: 'Trinity College',
    logoUrl: 'https://robohash.org/65G.png'
  }
  const relationshipCreate =
    new Promise(function (resolve, reject) {
      relCreateResolve = resolve
      sendVerityRESTMessage('123456789abcdefghi1234', 'relationship', '1.0', 'create', relationshipCreateMessage)
    })
  const [relationshipDid, relThreadId] = await relationshipCreate
  // create invitation
  const relationshipInvitationMessage = {
    '~for_relationship': relationshipDid
  }
  const relationshipInvitation =
    new Promise(function (resolve, reject) {
      relInvitationResolve = resolve
      sendVerityRESTMessage('123456789abcdefghi1234', 'relationship', '1.0', 'connection-invitation', relationshipInvitationMessage, relThreadId)
    })
  const inviteUrl = await relationshipInvitation
  console.log(`Invite URL is:\n${inviteUrl}`)
  await QR.toFile('qrcode.png', inviteUrl)
  // establish connection
  console.log('Open file qrcode.png and scan it with ConnectMe app')
  const connection =
    new Promise(function (resolve, reject) {
      connectionResolve = resolve
    })
  await connection

  // STEP 6 - Credential issuance
  const credentialData = {
    name: 'Joe Smith',
    degree: 'Bachelors'
  }
  const credentialMessage = {
    '~for_relationship': relationshipDid,
    name: 'Diploma',
    cred_def_id: credDefId,
    credential_values: credentialData,
    price: 0,
    comment: 'Diploma',
    auto_issue: true
  }

  const credentialOffer =
    new Promise(function (resolve, reject) {
      credOfferResolve = resolve
      sendVerityRESTMessage('BzCbsNYhMrjHiqZDTUASHg', 'issue-credential', '1.0', 'offer', credentialMessage)
    })

  await credentialOffer

  console.log('Demo completed!')
  process.exit(0)
}

const app = express()

app.use(bodyParser.json())

// Verity Application Server will send REST API callbacks to this endpoint
app.post('/webhook', async (req, res) => {
  const message = req.body
  console.log('Got message on the webhook')
  console.log(message)
  res.status(202).send('Accepted')
  // Handle received message differently based on the message type
  switch (message['@type']) {
    case 'did:sov:123456789abcdefghi1234;spec/configs/0.6/COM_METHOD_UPDATED':
      webhookResolve(null)
      break
    case 'did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/public-identifier-created':
      setupResolve([message.identifier.did, message.identifier.verKey])
      break
    case 'did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/problem-report':
      if (
        message.message === 'Issuer Identifier is already created or in the process of creation'
      ) {
        await sendVerityRESTMessage('123456789abcdefghi1234', 'issuer-setup', '0.6', 'current-public-identifier', {})
      }
      break
    case 'did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/public-identifier':
      setupResolve([message.did, message.verKey])
      break
    case 'did:sov:123456789abcdefghi1234;spec/write-schema/0.6/status-report':
      schemaResolve(message.schemaId)
      break
    case 'did:sov:123456789abcdefghi1234;spec/write-cred-def/0.6/status-report':
      credDefResolve(message.credDefId)
      break
    case 'did:sov:123456789abcdefghi1234;spec/relationship/1.0/created':
      relCreateResolve([message.did, message['~thread'].thid])
      break
    case 'did:sov:123456789abcdefghi1234;spec/relationship/1.0/invitation':
      relInvitationResolve(message.inviteURL)
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/response-sent':
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/trust_ping/1.0/sent-response':
      connectionResolve(null)
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/request-received':
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/issue-credential/1.0/sent':
      if (message.msg.hasOwnProperty('credentials~attach')) {
        credOfferResolve()
      }
      break
    default:
      console.log(`Unexpected message type ${message['@type']}`)
      process.exit(1)
  }
})

app.listen(PORT, () => {
  console.log(`Webhook listening on port ${PORT}`)
  issuer()
})

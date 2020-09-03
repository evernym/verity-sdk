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

let webhookResolve
let relCreateResolve
let relInvitationResolve
let connectionResolve
let proofResolve

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

  // STEP 2 - Relationship creation
  // create relationship key
  const relationshipCreateMessage = {
    label: 'Trinity College',
    logoUrl: 'https://png.pngtree.com/png-vector/20190215/ourlarge/pngtree-vector-bank-icon-png-image_515454.jpg'
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

  // STEP 3 - Proof request

  const proofMessage = {
    '~for_relationship': relationshipDid,
    name: 'Proof of diploma',
    proof_attrs: [
      {
        name: 'name',
        restrictions: [],
        self_attest_allowed: true
      },
      {
        name: 'degree',
        restrictions: [],
        self_attest_allowed: true
      }
    ]
  }

  const requestProof =
    new Promise(function (resolve, reject) {
      proofResolve = resolve
      sendVerityRESTMessage('BzCbsNYhMrjHiqZDTUASHg', 'present-proof', '1.0', 'request', proofMessage)
    })

  await requestProof

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
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/present-proof/1.0/presentation-result':
      if (message.verification_result === 'ProofValidated') {
        console.log('Proof is validated!')
      } else {
        console.log('Proof is NOT validated')
      }
      console.log(`Received presentation is:\n ${JSON.stringify(message.requested_presentation, null, 4)}`)
      proofResolve(null)
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

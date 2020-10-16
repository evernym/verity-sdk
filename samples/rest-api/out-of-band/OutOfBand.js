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

async function createConnection () {
  // create relationship
  const relationshipCreateMessage = {
    label: 'Trinity College',
    profileUrl: 'https://robohash.org/65G.png'
  }
  const relThreadId = uuid4()
  const relationshipCreate =
    new Promise(function (resolve, reject) {
      relCreateResolveMap.set(relThreadId, resolve)
      sendVerityRESTMessage('123456789abcdefghi1234', 'relationship', '1.0', 'create', relationshipCreateMessage, relThreadId)
    })
  const relationshipDid = await relationshipCreate

  // create relationship invitation using the Out-of-Band protocol
  const relationshipInvitationMessage = {
    '~for_relationship': relationshipDid
  }
  const relationshipInvitation =
    new Promise(function (resolve, reject) {
      relInvitationResolveMap.set(relThreadId, resolve)
      sendVerityRESTMessage('123456789abcdefghi1234', 'relationship', '1.0', 'out-of-band-invitation', relationshipInvitationMessage, relThreadId)
    })
  const [inviteUrl, invitationId] = await relationshipInvitation
  InviteToDidMap.set(invitationId, relationshipDid)
  console.log(`Invite URL is:\n${inviteUrl}`)
  await QR.toFile('qrcode.png', inviteUrl)

  // Present QR code and wait for the Holder to scan the QR code and initiate Connection protocol
  console.log('Open file qrcode.png and scan it with ConnectMe app')

  // Wait for connection to resolve (either as 'accepted' or 'redirected')
  const ConnectionPromise =
    new Promise(function (resolve, reject) {
      connectionResolveMap.set(relationshipDid, resolve)
    })

  const [status, redirectDID] = await ConnectionPromise
  console.log(`Relationship with the DID ${relationshipDid} has been ${status}!`)
  if (redirectDID) {
    console.log(`Please use the DID ${redirectDID} instead of ${relationshipDid} for future communication`)
  }
}

function sleep (ms) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

let webhookResolve
let threadId
let pthid

// Stores relationship create promises. ThreadId is used as a key
const relCreateResolveMap = new Map()
// Stores relationship invitation promises. ThreadId is used as a key
const relInvitationResolveMap = new Map()
// Store pending connection promises. Relationship DID is used a key
const connectionResolveMap = new Map()
// Maps Out-of-band invitationId to the relationship DID
const InviteToDidMap = new Map()

// Main function demonstrating Out-of-band protocol
async function oob () {
  // Update webhook endpoint
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

  // Create the first connection based on the Out-of-Band invitation
  console.log('Establishing the first connection with the same Holder')
  await createConnection()

  // Sleep
  console.log('Waiting 15 seconds now - simulating returning user...')
  await sleep(15000)

  // Create the second connection based on the Out-of-Band invitation
  console.log('Now establishing the second connection with the same Holder')
  await createConnection()
  console.log('Demo completed')
  process.exit(0)
}

const app = express()

app.use(bodyParser.json())

// Verity Application Server will send REST API callbacks to this endpoint
app.post('/webhook', async (req, res) => {
  const message = req.body
  console.log('Got message on the webhook')
  console.log(JSON.stringify(message, null, 4))
  res.status(202).send('Accepted')
  if (message['~thread']) {
    threadId = message['~thread'].thid
  }
  // Handle received message differently based on the message type
  switch (message['@type']) {
    case 'did:sov:123456789abcdefghi1234;spec/configs/0.6/COM_METHOD_UPDATED':
      webhookResolve(null)
      break
    case 'did:sov:123456789abcdefghi1234;spec/relationship/1.0/created':
      // Resolve relationship creation promise with the DID of created relationship
      relCreateResolveMap.get(threadId)(message.did)
      break
    case 'did:sov:123456789abcdefghi1234;spec/relationship/1.0/invitation':
      // Resolve relationship invitation promise with inviteUrl and invitationId of created OoB invitation
      relInvitationResolveMap.get(threadId)([message.inviteURL, message.invitationId])
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/request-received':
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/response-sent':
      // Resolve connection promise with the status ('accepted' or 'redirected') and pthid
      connectionResolveMap.get(message.myDID)(['accepted', null])
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/trust_ping/1.0/sent-response':
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/out-of-band/1.0/relationship-reused':
      pthid = message['~thread'].pthid
      // Resolve connection promise with the status ('accepted' or 'redirected') and pthid
      connectionResolveMap.get(InviteToDidMap.get(pthid))(['redirected', message.relationship])
      break
    default:
      console.log(`Unexpected message type ${message['@type']}`)
      console.log(`Message was: ${JSON.stringify(message)}`)
      process.exit(1)
  }
})

app.listen(PORT, () => {
  console.log(`Webhook listening on port ${PORT}`)
  oob()
})

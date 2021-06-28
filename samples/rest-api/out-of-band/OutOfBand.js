'use strict'

const axios = require('axios')
const bodyParser = require('body-parser')
const express = require('express')
const QR = require('qrcode')
const uuid4 = require('uuid4')
const urljoin = require('url-join')

const ANSII_GREEN = '\u001b[32m'
const ANSII_RESET = '\x1b[0m'
const PORT = 4000

const verityUrl = '<< PUT VERITY APPLICATION SERVICE URL HERE >>' // address of Verity Application Service
const domainDid = '<< PUT DOMAIN DID HERE >>' // your Domain DID on the multi-tenant Verity Application Service
const xApiKey = '<< PUT X-API-KEY HERE >>' // REST API key associated with your Domain DID
const webhookUrl = '<< PUT WEBHOOK URL HERE >>' // public URL for the webhook endpoint

// Sends a message to the Verity Application Service via the Verity REST API
async function sendVerityRESTMessage (qualifier, msgFamily, msgFamilyVersion, msgName, message, threadId) {
  // qualifier - either 'BzCbsNYhMrjHiqZDTUASHg' for Aries community protocols or '123456789abcdefghi1234' for Evernym-specific protocols
  // msgFamily - message family (e.g. 'present-proof')
  // msgFamilyVersion - version of the message family (e.g. '1.0')
  // msgName - name of the protocol message to perform (e.g. 'request')
  // message - message to be sent in the body payload
  // threadId - unique identifier of the protocol interaction. The threadId is used to distinguish between simultaenous interactions

  // Add @type and @id fields to the message in the body payload
  // Field @type is dinamycially constructed from the function arguments and added into the message payload
  message['@type'] = `did:sov:${qualifier};spec/${msgFamily}/${msgFamilyVersion}/${msgName}`
  message['@id'] = uuid4()

  if (!threadId) {
    threadId = uuid4()
  }

  // send prepared message to Verity and return Axios request promise
  const url = urljoin(verityUrl, 'api', domainDid, msgFamily, msgFamilyVersion, threadId)
  console.log(`Posting message to ${ANSII_GREEN}${url}${ANSII_RESET}`)
  console.log(`${ANSII_GREEN}${JSON.stringify(message, null, 4)}${ANSII_RESET}`)
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
    label: 'Faber',
    profileUrl: 'https://freeiconshop.com/wp-content/uploads/edd/bank-flat.png'
  }
  const relThreadId = uuid4()
  const relationshipCreate =
    new Promise(function (resolve, reject) {
      relCreateResolveMap.set(relThreadId, resolve)
    })
  await sendVerityRESTMessage('123456789abcdefghi1234', 'relationship', '1.0', 'create', relationshipCreateMessage, relThreadId)
  const relationshipDid = await relationshipCreate

  // create relationship invitation using the Out-of-Band protocol
  const relationshipInvitationMessage = {
    '~for_relationship': relationshipDid
  }
  const relationshipInvitation =
    new Promise(function (resolve, reject) {
      relInvitationResolveMap.set(relThreadId, resolve)
    })
  await sendVerityRESTMessage('123456789abcdefghi1234', 'relationship', '1.0', 'out-of-band-invitation', relationshipInvitationMessage, relThreadId)
  const [inviteUrl, invitationId] = await relationshipInvitation
  InviteToDidMap.set(invitationId, relationshipDid)
  console.log(`Invite URL is:\n${ANSII_GREEN}${inviteUrl}${ANSII_RESET}`)
  await QR.toFile('qrcode.png', inviteUrl)

  // Present QR code and wait for the Holder to scan the QR code and initiate Connection protocol
  console.log('Open the file "qrcode.png" and scan it with the ConnectMe app')

  // Wait for connection to resolve (either as 'accepted' or 'redirected')
  const ConnectionPromise =
    new Promise(function (resolve, reject) {
      connectionResolveMap.set(relationshipDid, resolve)
    })

  const [status, redirectDID] = await ConnectionPromise
  console.log(`Relationship with the DID ${ANSII_GREEN}${relationshipDid}${ANSII_RESET} has been ${ANSII_GREEN}${status}${ANSII_RESET}!`)
  if (redirectDID) {
    console.log(`Please use the DID ${ANSII_GREEN}${redirectDID}${ANSII_RESET} instead of ${ANSII_GREEN}${relationshipDid}${ANSII_RESET} for future communication`)
  }
}

function sleep (ms) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

let webhookResolve
let pthid
// Setup Issuer
const setupIssuerMap = new Map()
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
    })
  await sendVerityRESTMessage('123456789abcdefghi1234', 'configs', '0.6', 'UPDATE_COM_METHOD', webhookMessage)
  await updateWebhook
  // ISSUER SETUP
  let issuerDid
  let issuerVerkey

  // check if Issuer Keys were already created
  const getIssuerKeysMsg = {}
  const getIssuerKeysThreadId = uuid4()

  const getIssuerKeys =
     new Promise(function (resolve, reject) {
       setupIssuerMap.set(getIssuerKeysThreadId, resolve)
     })

  await sendVerityRESTMessage('123456789abcdefghi1234', 'issuer-setup', '0.6', 'current-public-identifier', getIssuerKeysMsg, getIssuerKeysThreadId);

  [issuerDid, issuerVerkey] = await getIssuerKeys

  if (issuerDid === undefined) {
    // if issuer Keys were not created, create Issuer keys
    const setupIssuerMsg = {}
    const setupIssuerThreadId = uuid4()
    const setupIssuer =
         new Promise(function (resolve, reject) {
           setupIssuerMap.set(setupIssuerThreadId, resolve)
         })

    await sendVerityRESTMessage('123456789abcdefghi1234', 'issuer-setup', '0.6', 'create', setupIssuerMsg, setupIssuerThreadId);
    [issuerDid, issuerVerkey] = await setupIssuer
    console.log(`Issuer DID: ${ANSII_GREEN}${issuerDid}${ANSII_RESET}`)
    console.log(`Issuer Verkey: ${ANSII_GREEN}${issuerVerkey}${ANSII_RESET}`)
  }

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

// Verity Application Service will send REST API callbacks to this endpoint
app.post('/webhook', async (req, res) => {
  const message = req.body
  const threadId = message['~thread'] ? message['~thread'].thid : null
  console.log('Got message on the webhook')
  console.log(`${ANSII_GREEN}${JSON.stringify(message, null, 4)}${ANSII_RESET}`)
  res.status(202).send('Accepted')
  // Handle received message differently based on the message type
  switch (message['@type']) {
    case 'did:sov:123456789abcdefghi1234;spec/configs/0.6/COM_METHOD_UPDATED':
      webhookResolve(null)
      break
    case 'did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/public-identifier-created':
      setupIssuerMap.get(threadId)([message.identifier.did, message.identifier.verKey])
      break
    case 'did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/problem-report':
      if (
        message.message === 'Issuer Identifier has not been created yet'
      ) {
        setupIssuerMap.get(threadId)([undefined, undefined])
      }
      break
    case 'did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/public-identifier':
      setupIssuerMap.get(threadId)([message.did, message.verKey])
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
      process.exit(1)
  }
})

app.listen(PORT, () => {
  console.log(`Webhook listening on port ${PORT}`)
  oob()
})

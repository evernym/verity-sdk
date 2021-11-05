'use strict'

const axios = require('axios')
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
const credDefId = '<< PUT YOUR CREDENTIAL DEFINITION ID HERE >>' // credential definition Id you'll use for issuing credentials - it has to be endorsed on the Sovrin Staging Net
const credentialData = '<< PUT CREDENTIAL DATA HERE >>' // Data you'll issue in a credential. Credential data should be in the following format, NOT between the quotation marks:
// {
//   field1Name: 'Field Value',
//   field2Name: 'Field Value',
// }
// Example of the credential data:
// {
//   name: 'Joe Smith',
//   degree: 'Bachelors'
// }
// NOTE: Make sure you use ALL fields from the schema you used for a credential definition.

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

// Maps containing promises for the started interactions - threadId is used as the map key
// Update configs
const updateConfigsMap = new Map()
// Relationship create
const relCreateMap = new Map()
// Relationship invitation
const relInvitationMap = new Map()
// Issue Credential
const issueCredentialMap = new Map()

// Map for connection accepted promise - relationship DID is used as the map key
const connectionAccepted = new Map()

// Update webhook protocol is synchronous and does not support threadId
let webhookResolve

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

  // STEP 2 - Update configuration
  const updateConfigMessage = {
    configs: [
      {
        name: 'logoUrl',
        value: 'https://freeiconshop.com/wp-content/uploads/edd/bank-flat.png'
      },
      {
        name: 'name',
        value: 'Issuer'
      }
    ]
  }

  const updateConfigsThreadId = uuid4()
  const updateConfigs =
  new Promise(function (resolve, reject) {
    updateConfigsMap.set(updateConfigsThreadId, resolve)
  })

  await sendVerityRESTMessage('123456789abcdefghi1234', 'update-configs', '0.6', 'update', updateConfigMessage, updateConfigsThreadId)

  await updateConfigs

  // STEP 3 - Relationship creation
  // create relationship key
  const relationshipCreateMessage = {}
  const relThreadId = uuid4()
  const relationshipCreate =
    new Promise(function (resolve, reject) {
      relCreateMap.set(relThreadId, resolve)
    })

  await sendVerityRESTMessage('123456789abcdefghi1234', 'relationship', '1.0', 'create', relationshipCreateMessage, relThreadId)
  const relationshipDid = await relationshipCreate

  // create invitation for the relationship
  const relationshipInvitationMessage = {
    '~for_relationship': relationshipDid,
    goalCode: 'issue-vc',
    goal: 'To issue a credential'
  }
  const relationshipInvitation =
    new Promise(function (resolve, reject) {
      relInvitationMap.set(relThreadId, resolve)
    })

  await sendVerityRESTMessage('123456789abcdefghi1234', 'relationship', '1.0', 'out-of-band-invitation', relationshipInvitationMessage, relThreadId)
  const inviteUrl = await relationshipInvitation
  console.log(`Invite URL is:\n${ANSII_GREEN}${inviteUrl}${ANSII_RESET}`)
  await QR.toFile('qrcode.png', inviteUrl)

  // wait for the user to scan the QR code and accept the connection
  const connection =
    new Promise(function (resolve, reject) {
      connectionAccepted.set(relationshipDid, resolve)
    })
  console.log('Open the file "qrcode.png" and scan it with the ConnectMe app')

  await connection

  // STEP 6 - Credential issuance

  const credentialMessage = {
    '~for_relationship': relationshipDid,
    cred_def_id: credDefId,
    credential_values: credentialData,
    price: 0,
    comment: 'MyCredential',
    auto_issue: true
  }
  const issueCredThreadId = uuid4()

  const credentialOffer =
    new Promise(function (resolve, reject) {
      issueCredentialMap.set(issueCredThreadId, resolve)
    })

  await sendVerityRESTMessage('BzCbsNYhMrjHiqZDTUASHg', 'issue-credential', '1.0', 'offer', credentialMessage, issueCredThreadId)
  await credentialOffer

  console.log('Demo completed!')
  process.exit(0)
}

const app = express()

app.use(express.json())

// Verity Application Server will send REST API callbacks to this endpoint
app.post('/webhook', async (req, res) => {
  const message = req.body
  const threadId = message['~thread'] ? message['~thread'].thid : null
  console.log('Got message on the webhook')
  console.log(`${ANSII_GREEN}${JSON.stringify(message, null, 4)}${ANSII_RESET}`)
  res.status(202).send('Accepted')
  // Handle received message differently based on the message type
  switch (message['@type']) {
    case 'did:sov:123456789abcdefghi1234;spec/configs/0.6/COM_METHOD_UPDATED':
      webhookResolve('webhook updated')
      break
    case 'did:sov:123456789abcdefghi1234;spec/update-configs/0.6/status-report':
      updateConfigsMap.get(threadId)('config updated')
      break
    case 'did:sov:123456789abcdefghi1234;spec/relationship/1.0/created':
      relCreateMap.get(threadId)(message.did)
      break
    case 'did:sov:123456789abcdefghi1234;spec/relationship/1.0/invitation':
      relInvitationMap.get(threadId)(message.inviteURL)
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/request-received':
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/response-sent':
      connectionAccepted.get(message.myDID)('connection accepted')
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/trust_ping/1.0/sent-response':
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/out-of-band/1.0/relationship-reused':
      console.log('The mobile wallet app signalled that it already has the connection with this Issuer')
      console.log('This application does not support relationship-reuse since it does not store the data about previous relationships')
      console.log('Please delete existing connection with this Issuer in your mobile app and re-run the application')
      console.log('To learn how relationship-reuse can be used check out "ssi-auth" or "out-of-band" sample apps')
      process.exit(1)
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/issue-credential/1.0/sent':
      if (message.msg['credentials~attach']) {
        issueCredentialMap.get(threadId)('credential issued')
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

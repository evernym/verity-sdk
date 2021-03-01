'use strict'

const fs = require('fs')
const axios = require('axios')
const bodyParser = require('body-parser')
const express = require('express')
const QR = require('qrcode')
const uuid4 = require('uuid4')
const urljoin = require('url-join')

const ANSII_GREEN = '\u001b[32m'
const ANSII_RESET = '\x1b[0m'
const CRED_DEF_FILE = 'cred_def_id.txt'
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

// Maps containing promises for the started interactions. The threadId is used as the map key
// Update configs
const updateConfigsMap = new Map()
// Setup Issuer
const setupIssuerMap = new Map()
// Schema create
const schemaCreateMap = new Map()
// Credential definition create
const credDefCreateMap = new Map()
// Relationship create
const relCreateMap = new Map()
// Issue credential protocol invitation
const credProtInviteMap = new Map()
// Issue Credential credential sent
const credIssuedMap = new Map()
// Proof request protocol invitation
const proofProtInviteMap = new Map()
// Proof request presentation sent
const proofPresentationSentMap = new Map()

// Update webhook protocol is synchronous and does not support threadId
let webhookResolve

async function oobRequestAttach () {
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
        value: 'OOB request attach'
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

  // STEP 3 - Setup Issuer
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

    // Automatic registration of DID/Verkey as Endorser on the Sovrin Staging Net ledger using Sovrin SelfServe portal
    const sovrinResponse = await registerDid(issuerDid, issuerVerkey)
    console.log(`DID registration response from Sovrin SelfServe portal:\n${ANSII_GREEN}${sovrinResponse.data.body}${ANSII_RESET}`)
  }

  // STEP 4 - Create schema and credential definition that will be used for credentials
  let credDefId
  if (fs.existsSync(CRED_DEF_FILE)) {
    // if the credential definition was created in the previous runs of this app, read credDefId from the file
    credDefId = fs.readFileSync(CRED_DEF_FILE, 'utf8')
  } else {
    // if the file does not exist create a new schema and credential definition and store credDefId in the file
    const schemaMessage = {
      name: 'Diploma ' + uuid4().substring(0, 8),
      version: '0.1',
      attrNames: ['name', 'degree']
    }
    const schemaThreadId = uuid4()
    const schemaCreate =
       new Promise(function (resolve, reject) {
         schemaCreateMap.set(schemaThreadId, resolve)
       })

    await sendVerityRESTMessage('123456789abcdefghi1234', 'write-schema', '0.6', 'write', schemaMessage, schemaThreadId)
    const schemaId = await schemaCreate
    console.log(`Created schema: ${ANSII_GREEN}${schemaId}${ANSII_RESET}`)

    const credDefMessage = {
      name: 'Diploma',
      schemaId: schemaId,
      tag: 'latest'
    }
    const credDefThreadId = uuid4()
    const credDefCreate =
       new Promise(function (resolve, reject) {
         credDefCreateMap.set(credDefThreadId, resolve)
       })

    await sendVerityRESTMessage('123456789abcdefghi1234', 'write-cred-def', '0.6', 'write', credDefMessage, credDefThreadId)
    credDefId = await credDefCreate
    console.log(`Created credential definition: ${ANSII_GREEN}${credDefId}${ANSII_RESET}`)
    fs.writeFileSync(CRED_DEF_FILE, credDefId)
  }

  // STEP 5 - Create relationship that will be used for credential issuance
  // create relationship key
  const issueRelationshipCreateMessage = {}
  const issueRelThreadId = uuid4()
  const issueRelationshipCreate =
    new Promise(function (resolve, reject) {
      relCreateMap.set(issueRelThreadId, resolve)
    })

  await sendVerityRESTMessage('123456789abcdefghi1234', 'relationship', '1.0', 'create', issueRelationshipCreateMessage, issueRelThreadId)
  const issueRelationshipDid = await issueRelationshipCreate

  // STEP 6 - Send invitiation to the relationship and attach credential offer to it.
  const credentialData = {
    name: 'Joe Smith',
    degree: 'Bachelors'
  }
  const credentialMessage = {
    '~for_relationship': issueRelationshipDid,
    cred_def_id: credDefId,
    credential_values: credentialData,
    price: 0,
    comment: 'Diploma',
    auto_issue: true,
    by_invitation: true
  }
  const issueCredThreadId = uuid4()

  const credentialOffer =
    new Promise(function (resolve, reject) {
      credProtInviteMap.set(issueCredThreadId, resolve)
    })

  await sendVerityRESTMessage('BzCbsNYhMrjHiqZDTUASHg', 'issue-credential', '1.0', 'offer', credentialMessage, issueCredThreadId)
  const credInviteURL = await credentialOffer
  console.log(`Invite URL is:\n${ANSII_GREEN}${credInviteURL}${ANSII_RESET}`)
  await QR.toFile('issue-qrcode.png', credInviteURL)
  console.log('Open the file "issue-qrcode.png" and scan the QR code with the ConnectMe app')

  // wait till the user scans QR code and accepts credential
  const credentialIssuedPromise = new Promise((resolve) => {
    credIssuedMap.set(issueCredThreadId, resolve)
  })

  await credentialIssuedPromise

  // STEP 7 - Create relationship that will be used for proof request
  // we need to create a different relationship than the one created in the step 5, since the invitation for rel in step 5 has already been generated
  // However, the ConnectMe app will signal that it already has the connection with the Inviter when processing invitation for the relationship created in step 7
  // and the original connection (established during execution of IssueCredential protocol) will be reused

  // create relationship key
  const proofRelationshipCreateMessage = {}
  const proofRelThreadId = uuid4()
  const proofRelationshipCreate =
    new Promise(function (resolve, reject) {
      relCreateMap.set(proofRelThreadId, resolve)
    })

  await sendVerityRESTMessage('123456789abcdefghi1234', 'relationship', '1.0', 'create', proofRelationshipCreateMessage, proofRelThreadId)
  const proofRelationshipDid = await proofRelationshipCreate

  // STEP 8 - Send invitiation to the relationship and attach proof request to it.

  const proofMessage = {
    '~for_relationship': proofRelationshipDid,
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
    ],
    by_invitation: true
  }

  const proofThreadId = uuid4()
  const requestProof =
      new Promise(function (resolve, reject) {
        proofProtInviteMap.set(proofThreadId, resolve)
      })

  await sendVerityRESTMessage('BzCbsNYhMrjHiqZDTUASHg', 'present-proof', '1.0', 'request', proofMessage, proofThreadId)

  const proofInviteURL = await requestProof
  console.log(`Invite URL is:\n${ANSII_GREEN}${proofInviteURL}${ANSII_RESET}`)
  await QR.toFile('verify-qrcode.png', proofInviteURL)
  console.log('Open the file "verify-qrcode.png" and scan the QR code with the ConnectMe app')

  // wait till the user scans QR code and shares proof
  const presentationSentPromise = new Promise((resolve) => {
    proofPresentationSentMap.set(proofThreadId, resolve)
  })

  const verificationResult = await presentationSentPromise

  if (verificationResult === 'ProofValidated') {
    console.log('Proof is validated!')
  } else {
    console.log('Proof is NOT validated')
  }

  console.log('Demo completed!')
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
      webhookResolve('webhook updated')
      break
    case 'did:sov:123456789abcdefghi1234;spec/update-configs/0.6/status-report':
      updateConfigsMap.get(threadId)('config updated')
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
    case 'did:sov:123456789abcdefghi1234;spec/write-schema/0.6/status-report':
      schemaCreateMap.get(threadId)(message.schemaId)
      break
    case 'did:sov:123456789abcdefghi1234;spec/write-cred-def/0.6/status-report':
      credDefCreateMap.get(threadId)(message.credDefId)
      break
    case 'did:sov:123456789abcdefghi1234;spec/relationship/1.0/created':
      relCreateMap.get(threadId)(message.did)
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/request-received':
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/response-sent':
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/trust_ping/1.0/sent-response':
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/out-of-band/1.0/move-protocol':
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/out-of-band/1.0/relationship-reused':
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/issue-credential/1.0/protocol-invitation':
      credProtInviteMap.get(threadId)(message.shortInviteURL)
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/issue-credential/1.0/sent':
      if (message.msg['credentials~attach']) {
        credIssuedMap.get(threadId)('credentail issued')
      }
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/present-proof/1.0/protocol-invitation':
      proofProtInviteMap.get(threadId)(message.shortInviteURL)
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/present-proof/1.0/presentation-result':
      proofPresentationSentMap.get(threadId)(message.verification_result)
      break
    default:
      console.log(`Unexpected message type ${message['@type']}`)
      process.exit(1)
  }
})

app.listen(PORT, () => {
  console.log(`Webhook listening on port ${PORT}`)
  oobRequestAttach()
})

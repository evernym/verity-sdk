# How to build Issuer using REST API

This is the tutorial for building a sample Issuer application using [Verity REST API](https://app.swaggerhub.com/apis/evernym/verity-rest-api/1.0).

This tutorial is written for Node.js framework, but it can be easily adapted to any language that supports asynchronous web requests. For more information on handling Verity asynchronous responses in your client application read [this document](../howto/Asynchronous-Architecture.md)

## Pre-requisites

This Issuer app requires Verity Application instance provided in the cloud by Evernym in order to have end2end functionality.

The Issuer application will require you to specify:
* Verity Application Service endpoint
* Domain DID
* X-API-KEY

You should have received this data from Evernym

The issuer application will also need to expose a webhook endpoint. The webhook endpoint (**/webhook**) needs to be served on a public URL so that Verity Application service can send messages to it. For development purposes, the Issuer application listening port can be assigned a public URL via means of the Ngrok tool.

## Issuer app preparation

We will start with the following skeleton for our app:
```javascript
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

// Maps containing promises for the started interactions - threadId is used as the map key
// Update configs
const updateConfigsMap = new Map()
// Setup Issuer
const setupIssuerMap = new Map()
// Schema create
const schemaCreateMap = new Map()
// Credential definition Create
const credDefCreateMap = new Map()
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

  // STEP 2 - Update configuration

  // STEP 3 - Setup Issuer

  // STEP 4 - Create schema and credential definition that will be used for credentials

  // STEP 5 - Relationship creation

  // STEP 6 - Credential issuance

  console.log('Demo completed!')
  process.exit(0)
}

const app = express()

app.use(bodyParser.json())

// Verity Application Server will send REST API callbacks to this endpoint
app.post('/webhook', async (req, res) => {
  const message = req.body
  const threadId = message['~thread'] ? message['~thread'].thid : null
  console.log('Got message on the webhook')
  console.log(`${ANSII_GREEN}${JSON.stringify(message, null, 4)}${ANSII_RESET}`)
  res.status(202).send('Accepted')
  // Handle received message differently based on the message type
  switch (message['@type']) {
    //
    // HERE: Add handlers for the messages received from Verity Application service
    //
    default:
      console.log(`Unexpected message type ${message['@type']}`)
      process.exit(1)
  }
})

app.listen(PORT, () => {
  console.log(`Webhook listening on port ${PORT}`)
  issuer()
})

```

In a separate terminal start ngrok tunnel for the application listening port and leave it running:
```sh
ngrok http 4000
```
From the output of the Ngrok command, take the URL of the forwarding tunnel to  **http://localhost:4000** and append **/webhook** to that URL to construct **webhookUrl** for the Issuer app.

Change the values for **verityUrl**, **domainDid**, **xApiKey** and **webhookUrl** with your values here:
```javascript
const verityUrl = '<< PUT VERITY APPLICATION SERVICE URL HERE >>' // address of Verity Application Service
const domainDid = '<< PUT DOMAIN DID HERE >>' // your Domain DID on the multi-tenant Verity Application Service
const xApiKey = '<< PUT X-API-KEY HERE >>' // REST API key associated with your Domain DID
const webhookUrl = '<< PUT WEBHOOK URL HERE >>' // public URL for the webhook endpoint
```
Sample values might look like this:
```javascript
const verityUrl = 'https://vas.pps.evernym.com'
const domainDid = 'W1TWvjCTGzHGEgbzdh5U4b'
const xApiKey = 'AkdrCwUhNXiQi3zgwKw2KhR6muAX1Q18phP4cfuMtvq4:4cBQC9EsbMa9T96KA4noZwLJQuVcd6KBwaqFhRqZQKFWT45VEm3jbPCm8S6bqhwh3UKEKAPkHeLz9Gb1d1YE1dWv'
const webhookUrl = 'https://1326d835655f.ngrok.io/webhook'
```
> **NOTE**: These are just sample reference values. These values will NOT work if left unchanged. You should specify DOMAIN_DID and X_API_KEY that you received from Evernym

> **NOTE**: **Ngrok** is used here as a developer tool to provide a publicly available endpoint that tunnels to the local listening port of the Issuer App. If you have capabilities to start the Issuer application on a cloud infrastructure then you don't need to install and start ngrok - you just need to specify your URL address in the **webhookUrl** parameter (e.g. `http://<your_cloud_ip>:4000/webhook`)

## Issuer app

### STEP 1 - Update webhook endpoint

The Issuer app first needs to update webhook endpoint on the Verity Application Service with the URL where it wants to receive asynchronous responses. *UpdateEndpoint* protocol is used for setting up the address of the endpoint.
Add the following code to the Issuer app to register webhook URL with Verity Application Service
```javascript
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
```
and the handler for the expected response message:
```javascript
  switch (message['@type']) {
    case 'did:sov:123456789abcdefghi1234;spec/configs/0.6/COM_METHOD_UPDATED':
      webhookResolve('webhook updated')
      break
} 
```

### STEP 2 - Update configuration

Issuer can update the `logoUrl` and the `name` values.
The values set here will be encoded in the invitation URL and will be shown in the ConnectMe app

Add the following code to the Issuer app to update configuration with Verity Application Service
```javascript
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
```
and the handler for the expected response message:
```javascript
  switch (message['@type']) {
    case 'did:sov:123456789abcdefghi1234;spec/update-configs/0.6/status-report':
      updateConfigsMap.get(threadId)('config updated')
      break
} 
```

### STEP 3 - Setup Issuer

Issuer needs to have Issuer keys created in order to create schemas, credential definitions, and issue credentials.

IssuerSetup should be called only once for a certain domain DID. If the **create** method is called for the second time Verity application will return a **problem-report** stating that Issuer Identifier is already created. In that case one can fetch their Issuer DID/Verkey by calling **current-public-identifier** method.

Add the following code to the Issuer app to setup Issuer keys:
```javascript
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
```
and the handler for the expected response message:
```javascript
  switch (message['@type']) {
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
```

### STEP 4 - Create schema and credential definition that will be used for credentials

Once Issuer keys are created and registered on the ledger, Issuer can create a schema and credential definition which will be used to issue credentials.

Add the following code to the Issuer app to write schema and credential definition to the ledger:
```javascript
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
```
and the handler for the expected response messages:
```javascript
  switch (message['@type']) {
    case 'did:sov:123456789abcdefghi1234;spec/write-schema/0.6/status-report':
      schemaCreateMap.get(threadId)(message.schemaId)
      break
    case 'did:sov:123456789abcdefghi1234;spec/write-cred-def/0.6/status-report':
      credDefCreateMap.get(threadId)(message.credDefId)
      break
} 
```


### STEP 5 - Relationship creation

Before we can issue a credential, we first need to create a connection with the Holder. This is achieved with *Relationship* protocol.

We first create a relationship DID for the new relationship.

We then generate inviteURL for the new relationship DID. We also encode inviteURL into QR code so that it can be conveniently scanned with the Holder's app (e.g. ConnectMe).

We then await for the Holder to initiate *Connection* protocol and accept the connection.
```javascript
  // STEP 5 - Relationship creation
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
    '~for_relationship': relationshipDid
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
  ```
  Handlers for relationship/connection protocols messages:
  ```javascript
  switch (message['@type']) {
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
  ```

### STEP 6 - Credential issuance

In this step we issue a credential to the connection established in the previous step
```javascript
  // STEP 6 - Credential issuance
  const credentialData = {
    name: 'Joe Smith',
    degree: 'Bachelors'
  }
  const credentialMessage = {
    '~for_relationship': relationshipDid,
    cred_def_id: credDefId,
    credential_values: credentialData,
    price: 0,
    comment: 'Diploma',
    auto_issue: true
  }
  const issueCredThreadId = uuid4()

  const credentialOffer =
    new Promise(function (resolve, reject) {
      issueCredentialMap.set(issueCredThreadId, resolve)
    })

  await sendVerityRESTMessage('BzCbsNYhMrjHiqZDTUASHg', 'issue-credential', '1.0', 'offer', credentialMessage, issueCredThreadId)
  await credentialOffer
```
Handler for credential exchange messages:
```javascript
  switch (message['@type']) {
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/issue-credential/1.0/sent':
      if (message.msg['credentials~attach']) {
        issueCredentialMap.get(threadId)('credential issued')
      }
      break
}
```

Completed Issuer application code is available [here](../../samples/rest-api/issuer)

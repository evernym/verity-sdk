# How to build Issuer using REST API

This is the tutorial for building a sample Issuer application using [Verity REST API](https://app.swaggerhub.com/apis/evernym/verity-rest-api/1.0).

This tutorial is written for Node.js framework, but it can be easily adapted to any language that supports asynchronous web requests. For more information on handling Verity asynchronous responses in your client application read [this document](../howto/Asynchronous-Architecture.md)

## Pre-requisites

This Issuer app requires Verity Application instance provided in the cloud by Evernym in order to have end2end functionality.

The Issuer application will require you to specify:
* Verity Application Server endpoint
* Domain DID
* X-API-KEY

You should have received this data from Evernym

The issuer application will also need to expose a webhook endpoint. The webhook endpoint (**/webhook**) needs to be served on a public URL so that Verity Application server can send messages to it. For development purposes, the Issuer application listening port can be assigned a public URL via means of the Ngrok tool.

## Issuer app preparation

We will start with the following skeleton for our app:
```javascript
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

async function issuer () {

  // STEP 1 - Update webhook endpoint

  // STEP 2 - Setup Issuer

  // STEP 3 - Create schema

  // STEP 4 - Create credential definition

  // STEP 5 - Relationship creation

  // STEP 6 - Credential issuance
}

const app = express()

app.use(bodyParser.json())

// Verity Application Server will send REST API callbacks to this endpoint
app.post('/webhook', async (req, res) => {
  const message = req.body
  console.log('Got message on the webhook')
  console.log(message)
  res.status(202).send('Received')
  // Handle received message differently based on the message type
  switch (message['@type']) {
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
const verityUrl = '<< PUT VERITY APPLICATION SERVER URL HERE >>' // address of Verity Application Server
const domainDid = '<< PUT DOMAIN DID HERE >>' // your Domain DID on the multi-tenant Verity Application Server
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

> **NOTE**: **Ngrok** is used here as a developer tool to provide a publicly available endpoint that tunnels to the local listening port of the Issuer App. If you have capabilities to start the Issuer application on a cloud infrastructure then you don't need to install and start ngrok - you just need to specify your URL address in the **webhookUrl** parameter (e.g. `http://<your_cloud_ip>:4000/webhook`)

## Issuer app

### STEP 1 - Update webhook endpoint

The Issuer app first needs to update webhook endpoint on the Verity Application Server with the URL where it wants to receive asynchronous responses. *UpdateEndpoint* protocol is used for setting up the address of the endpoint.
Add the following code to the Issuer app to register webhook URL with Verity Application Server
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
      webhookResolve(null)
      break
} 
```

### STEP 2 - Setup Issuer

Issuer needs to have Issuer keys created in order to create schemas, credential definitions, and issue credentials.

This is accomplished with *IssuerSetup* protocol:
```javascript
  // STEP 2 - Setup Issuer
  const setupIssuer =
    new Promise(function (resolve, reject) {
      setupResolve = resolve
      sendVerityRESTMessage('123456789abcdefghi1234', 'issuer-setup', '0.6', 'create', {})
    })
  const [issuerDid, issuerVerkey] = await setupIssuer
```
IssuerSetup should be called only once for a certain domain DID. If the **create** method is called for the second time Verity application will return a **problem-report** stating that Issuer Identifier is already created. In that case one can fetch their Issuer DID/Verkey by calling **current-public-identifier** method.

Logic for this is added in the webhook message handling section:
```javascript
  switch (message['@type']) {
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
```

Issuer also needs to register Issuer DID/Verkey as a Transaction Endorser on the ledger in order to perform writes to the ledger (like schema and credential definition creation). This can be automated in the case of Sovrin Staging Net
```javascript
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
...
// Automatic registration of DID/Verkey as Endorser on Staging Net using Sovrin SelfServe portal
  const sovrinResponse = await registerDid(issuerDid, issuerVerkey)
  console.log(`DID registration response from Sovrin SelfServe portal:\n${sovrinResponse.data.body}`)
```

### STEP 3 - Create schema

Once Issuer keys are created and registered on the ledger, Issuer can create a schema with *WriteSchema* protocol:
```javascript
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
```
Handler for schema creation response message:
```javascript
switch (message['@type']) {
    case 'did:sov:123456789abcdefghi1234;spec/write-schema/0.6/status-report':
      schemaResolve(message.schemaId)
      break
} 
```

### STEP 4 - Create credential definition

Once schema is created, Issuer can create a credential definition based on that schema with *WriteCredDef* protocol
```javascript
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
```
Handler for credential definition creation response message:
```javascript
switch (message['@type']) {
    case 'did:sov:123456789abcdefghi1234;spec/write-cred-def/0.6/status-report':
      credDefResolve(message.credDefId)
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
  ```
  Handlers for relationship/connection protocols messages:
  ```javascript
  switch (message['@type']) {
    case 'did:sov:123456789abcdefghi1234;spec/relationship/1.0/created':
      relCreateResolve([message.did, message['~thread'].thid])
      break
    case 'did:sov:123456789abcdefghi1234;spec/relationship/1.0/invitation':
      relInvitationResolve(message.inviteURL)
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/request-received':
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/response-sent':
      connectionResolve(null)
      break
    case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/trust_ping/1.0/sent-response':
      break
  }
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
```

Handler for credential exchange messages:
```javascript
switch (message['@type']) {
  case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/issue-credential/1.0/sent':
    if (message.msg.hasOwnProperty('credentials~attach')) {
    credOfferResolve()
    }
    break
}
```

Completed Issuer application code is available [here](../../samples/rest-api/issuer/Issuer.js)

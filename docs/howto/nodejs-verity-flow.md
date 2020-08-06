# Verifier Documentation

## Helpers:
### Object used to register response message handlers
The `handlers` variable is defined as field variable in the controller class like this:
<a name="Handlers"></a>
```
handlers: Handlers = Handlers()
```

### Registers Message Handler
Sets a specific response handler for protocol interactions
<a name="handle"></a>
```
await handlers.handleMessage(context, Buffer.from(req.body, 'utf8'))
``` 
### Loading Context Object
Saved context should be loaded with code like this:
```
async function loadContext (contextFile) {
  return sdk.Context.createWithConfig(fs.readFileSync(CONFIG_PATH))
}
```
Example Context Object: 
```json
{
  "verityPublicVerKey": "ETLgZKeQEKxBW7gXA6FBn7nBwYhXFoogZLCCn5EeRSQV",
  "verityUrl": "https://vas-team1.pdev.evernym.com",
  "verityAgentVerKey": "ZT8HE1t4eF2iMm6x8a2fPjPM1TiY2vuBZ8BMejL3Q9a",
  "walletKey": "examplewallet1",
  "sdkVerKey": "DNZ9Yw2bowKkjCZ1cuX8o4UmTMYNSjSsw5cszuTaPjLz",
  "walletName": "examplewallet1",
  "endpointUrl": "http://4153716fd8e9.ngrok.io",
  "verityPublicDID": "Rgj7LVEonrMzcRC1rhkx76",
  "sdkVerKeyId": "PhXp3RnCSinuCZsqYmy15T",
  "version": "0.2",
  "domainDID": "KWyJwot75jqnGAH1P5jioe"
}
```
## Setup
### Provisioning agent on verity
Provisioning is done only once.

```
const CONFIG_PATH = 'verity-context.json'
cost walletName = 'examplewallet1'  
const wallet_key = 'examplewallet1'
wallet_key = 'examplewallet1'
var verityUrl = ''
// token used for provisioning - Evernym provides this offline for their customers
var token = ...

// create initial Context
var ctx = await sdk.Context.create(walletName, walletKey, verityUrl, '')
console.log('wallet created')
const provision = new sdk.protocols.v0_7.Provision(null, token)
console.log('provision object')

// ask that an agent by provision (setup) and associated with created key pair
const context = provision.provision(ctx)

// Save context
fs.writeFileSync(CONFIG_PATH, JSON.stringify(context.getConfig()))
```

The wallet (usualy created in $HOME/.indy_client/wallet/<wallet-name>) needs to be saved with the context file.

## Handling Asynchronous response messages
### Setting up Webhook
For receiving messages an endpoint is needed. The UpdateEndpoint protocol should be used for setting up the address of this endpoint.
The endpoint dedicated for receiving messages from Verity Server, may look like this:

```
// New endpoint is set
val webhook = context.endpointUrl

context.endpointUrl = webhook

// request that verity application use specified webhook endpoint
await new sdk.protocols.UpdateEndpoint().update(context)

// Save context after updating the context
fs.writeFileSync(CONFIG_PATH, JSON.stringify(context.getConfig()))
```

Example Webhook
```
async function main () {
  await start()
  // Run application 
  await end()
}

async function start () {
  const app = express()
  app.use(bodyParser.text({
    type: function (_) {
      return 'text'
    }
  }))

  app.post('/', async (req, res) => {
    await handlers.handleMessage(context, Buffer.from(req.body, 'utf8'))
    res.send('Success')
  })

  listener = http.createServer(app).listen(LISTENING_PORT)
  console.log(`Listening on port ${LISTENING_PORT}`)
}

async function end () {
  listener.close()
  rl.close()
  process.exit(0)
}
```

### Response message handling 
Most Verity interactions respond to a request asynchronously. Here are some details that will help with the handling of these messages.
1. A response message is delivered via HTTPs. These messages can be processed however the application thinks best. Our example applications use webhooks.
    The http body will contain an encrypted protocol message which needs to be handled by the [Handlers](../howto/nodejs-verity-flow.md#Registers Message Handler) object. Decryption of the message happens here.
    ```
    await handlers.handleMessage(context, Buffer.from(req.body, 'utf8'))
    ``` 
2. Common Fields which show up in a response: 
   - `@type` - \<did info>;spec/\<message family>/\<version of protocol>/\<protocol message>" 
    > **Example:** "did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/public-identifier-created"
   - `@id` - An assigned message identifier
   - `~thread`: `{"thid":"<id>"}`
   - Message specific fields
3. Example handler: 
    - Registering the handler [Registers Message Handler](../howto/nodejs-verity-flow.md#Registers Message Handler)
    - Protocol Message handlers: 
        > **NOTE:** The MessageFamily in this example is an instance of ConnectionsV1_0.
    ```
    // handler for the accept message sent when connection is accepted
    new Promise((resolve) => {
        // Adding the message handler to list of handlers
        handlers.addHandler(connecting.msgFamily, connecting.msgFamilyVersion, async (msgName, message) => {
          switch (msgName) {
            case 'request-receieved':
              // Request receieved
              resolve(null)
              break
            default:
              printMessage(msgName, message)
              nonHandle('Message Name is not handled - ' + msgName)
         }
    })
    ```

## Setting up an Issuer identity
### Check to see if Issuer is already setup
Checks to see if issuer setup has been done. Gets did and verkey from the Verity Application
```
// constructor for the Issuer Setup protocol
const issuerSetup = new sdk.protocols.IssuerSetup()
// 1. query the current identifier
// 2. application's message handler should handle the asynchronous response
await issuerSetup.currentPublicIdentifier(context)

```

Message Response: 
* type: `public-identifier`
    ```json
    {
      "@type": "did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/public-identifier",
      "~thread": {"thid": "51e74fef-16e2-4f52-b54d-bc868418beda"},
      "@id": "e497da01-f907-43b9-8607-68d94f83a22a",
      "verKey": "GoNQz4LV4jUCxUZ9rBoBptc5wtPzyYAg4WDV2GQrT3rA",
      "did": "VzJn8BMiQDhitA7BSmTthe"
    }
    ```
* Save values: \
    These are associated with all future credential definitions and issued credentials.
    - `did`: newly created issuer did
    - `verkey`: newly created issuer verkey

### Setup new Issuer
```
// constructor for the Issuer Setup protocol
const issuerSetup = new sdk.protocols.IssuerSetup()

// 1. request that issuer identifier be created
// 2. application's message handler should handle the asynchronous response
await issuerSetup.create(context)
```

Message Response: 
* type: `public-identifier-created`
    ```json
    {
      "identifier": {
        "verKey": "GoNQz4LV4jUCxUZ9rBoBptc5wtPzyYAg4WDV2GQrT3rA",
        "did": "VzJn8BMiQDhitA7BSmTthe"
      },
      "@type": "did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/public-identifier-created",
      "~thread": {"thid": "1d90666d-a440-4230-b381-cf10f34ded5d"},
      "@id": "2305b750-9eb2-449f-bb82-6393f6b25f65"
    }
    ```
* Save values: \
    These are associated with all future credential definitions and issued credentials.
    - `did`: newly created issuer did
    - `verkey`: newly created issuer verkey

## Updating Config
Update agent configs to set the institution's name and logo url. 
```
const INSTITUTION_NAME = 'Faber College'
const LOGO_URL = 'http://robohash.org/235'
const updateConfigs = new sdk.protocols.UpdateConfigs(INSTITUTION_NAME, LOGO_URL)
await updateConfigs.update(context)
```

## Write Schema to Ledger
```
// input parameters for schema
const schemaName = 'Diploma ' + uuidv4().substring(0, 8)
const schemaVersion = '0.1'
const schemaAttrs = ['name', 'degree']

// constructor for the Write Schema protocol
const schema = new sdk.protocols.WriteSchema(schemaName, schemaVersion, schemaAttrs)
// add handler to the set of handlers

// 1. request schema be written to ledger
// 2. application's message handler should handle the asynchronous response
await schema.write(context) 
```
Message Response: 
* type: `status-report`
    ```json
    {
      "schemaId": "VzJn8BMiQDhitA7BSmTthe:2:Diploma 5361e670:0.1",
      "@type": "did:sov:123456789abcdefghi1234;spec/write-schema/0.6/status-report",
      "~thread": {"thid": "108758fa-b6d1-44a8-b108-fb382eec1823"},
      "@id": "912d82dd-f19e-4223-b07a-bbd344124705"
    }
    ```
* Save the `schemaId`. This will be used to create credential definitions. 

## Write Credential Definition to Ledger
* `schemaId`: received in the write schema response [Write Schema](../howto/nodejs-verity-flow.md#Write Schema to Ledger)

```
// input parameters for cred definition
const credDefName = 'Trinity College Diplomas'
const credDefTag = 'latest'

// constructor for the Write Credential Definition protocol
const def = new sdk.protocols.WriteCredentialDefinition(credDefName, schemaId, credDefTag)

// add handler to the set of handlers

// 1. request the cred def be writen to ledger
// 2. application's message handler should handle the asynchronous response
await def.write(context) // wait for operation to be complete and returns ledger cred def identifier
```

Message Response: 
* type: `status-report`
```json
{
  "credDefId": "VzJn8BMiQDhitA7BSmTthe:3:CL:13094:latest",
  "@type": "did:sov:123456789abcdefghi1234;spec/write-cred-def/0.6/status-report",
  "~thread": {"thid": "cbeb962f-ee08-48b7-91ea-e00539357ec6"},
  "@id": "cb9976ca-e395-4cef-8d11-8b55cc9f3677"
}
```
* Save the `credDefId`. This will be used to specify the type of credential to be issued. 

## Connecting

### Creating an Invitation with Relationship Protocol
We create an api which will return the invitation. That invitation can be converted to QR code which can be scanned by Connect.me.
This code is just an example, it does not handle error cases.

The Relationship protocol has two steps: 
1. create relationship key 
    ```
    // global context
    // global handlers

    // Constructor for the Connecting API
    const relProvisioning = new sdk.protocols.v1_0.Relationship(null, null, 'inviter')


    // add handler to the set of handlers

    // starts the relationship protocol
    await relProvisioning.create(context)
    ```
   Message Response: 
   * type: `created`
       ```json
        {
          "@type": "did:sov:123456789abcdefghi1234;spec/relationship/1.0/created",
          "~thread": {"thid": "caf7d140-966f-4cf4-a8e7-527cb6ff1e19"},
          "@id": "4704e7f9-c3af-4c51-b2b4-1f905c72166b",
          "verKey": "4YC42v4WTrMUNWv6ZFX3WVnyKAxJqHs3WUqgxJUpvQgD",
          "did": "7VUS2GgoXcm7L9bCUDzSLj"
        }
       ```
    * Save values: \
        These are associated with all future interactions for a specific relationship
        - `did`: did created for a specific relationship
        - `verkey`: verkey created for a specific relationship
2. create invitation
    - `relDID` and `threadId` were given in the `created` response
        ```
        const relationship = new sdk.protocols.v1_0.Relationship(relDID, threadId)
        await relationship.connectionInvitation(context)
        ```
    Message Response: 
    * type: `invitation`
       ```json
       {
         "inviteURL": "http://vas-team1.pdev.evernym.com:80/agency/msg?c_i=eyJsYWJlbCI6Imludml0ZXIiLCJzZXJ2aWNlRW5kcG9pbnQiOiJodHRwOi8vdmFzLXRlYW0xLnBkZXYuZXZlcm55bS5jb206ODAvYWdlbmN5L21zZyIsInJlY2lwaWVudEtleXMiOlsiNFlDNDJ2NFdUck1VTld2NlpGWDNXVm55S0F4SnFIczNXVXFneEpVcHZRZ0QiXSwicm91dGluZ0tleXMiOlsiNFlDNDJ2NFdUck1VTld2NlpGWDNXVm55S0F4SnFIczNXVXFneEpVcHZRZ0QiLCJFVExnWktlUUVLeEJXN2dYQTZGQm43bkJ3WWhYRm9vZ1pMQ0NuNUVlUlNRViJdLCJwcm9maWxlVXJsIjoiaHR0cDovL3JvYm9oYXNoLm9yZy8yMzUiLCJAdHlwZSI6ImRpZDpzb3Y6QnpDYnNOWWhNcmpIaXFaRFRVQVNIZztzcGVjL2Nvbm5lY3Rpb25zLzEuMC9pbnZpdGF0aW9uIiwiQGlkIjoiODQ4YTJjZGQtZTI1ZS00YTNlLWE2ZTEtMTJjNzkzZmNmOWQ2In0=",
         "@type": "did:sov:123456789abcdefghi1234;spec/relationship/1.0/invitation",
         "~thread": {"thid": "caf7d140-966f-4cf4-a8e7-527cb6ff1e19"},
         "@id": "148c5c0f-09bb-48f6-b9d0-b1c4fd04615b"
       }
       ```
   - Save value `inviteURL`. This is used to establish a connection by either delivering directly to other party or generated a QR Code.
    
### Connection Listener 
Once the mobile device receives the `inviteURL` or scans the QR Code, the rest is automated in verity-sdk. \
You can setup a handler to view incoming messages.
```
// handler for the response to the request to start the Connecting protocol.
var firstStep = new Promise((resolve) => {
handlers.addHandler(connecting.msgFamily, connecting.msgFamilyVersion, async (msgName, message) => {
  switch (msgName) {
    case connecting.msgNames.REQUEST_RECEIVED:
      printMessage(msgName, message)
      resolve(null)
      break
    default:
      printMessage(msgName, message)
      nonHandle('Message Name is not handled - ' + msgName)
  }
})
```

## Issue Credential
The Issue Credential has two steps: 

1. Send the Credential Offer
    * `defId`: received in the credential definition response [Credential Definition Response](../howto/nodejs-verity-flow.md#Write Credential Definition to Ledger)    
    * `relDID`: received in the create Relationship response [Creating Relationship](../howto/nodejs-verity-flow.md#Creating an Invitation with Relationship Protocol)
    ```
    // input parameters for issue credential
    const credentialData = {
      name: 'Joe Smith',
     degree: 'Bachelors'
    }
   
    // constructor for the Issue Credential protocol
    const issue = new sdk.protocols.v1_0.IssueCredential(relDID, null, defId, credentialData, 'comment', 0, true)
    
   //add handler to the set of handlers
   
   // 1. request that credential is offered
   // 2. application's message handler should handle the asynchronous response
   await issue.offerCredential(context)
   ```
   
2. Send the Credential once the holder sends a `accept-request` - This is automated in the sdk

## Request Proof Presentation
* `issuerDID`: received in the IssuerSetup response [Issuer Setup](../howto/nodejs-verity-flow.md#Setting up an Issuer identity)
* `relDID`: received in the create Relationship response [Creating Relationship](../howto/nodejs-verity-flow.md#Creating an Invitation with Relationship Protocol)
```
// global issuer_did
// input parameters for request proof
const proofName = 'Proof of Degree' + uuidv4().substring(0, 8)
const proofAttrs = [
    {
      name: 'name',
      restrictions: [{ issuer_did: issuerDID }]
    },
    {
      name: 'degree',
      restrictions: [{ issuer_did: issuerDID }]
    }
]

// constructor for the Present Proof protocol
const proof = new sdk.protocols.v1_0.PresentProof(relDID, null, proofName, proofAttrs)

// 1. request proof
// 2. application's message handler should handle the asynchronous response
await proof.request(context)
```

Message Response: 
* type: `presentation-result`
    ```json
    {
      "@type": "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/present-proof/1.0/presentation-result",
      "~thread": {
        "received_orders": {"YaLZ14xqPQGErywdZarBgd": 0},
        "sender_order": 0,
        "thid": "725dab3b-3f87-4e8d-a7f0-11f4ef3a56af"
      },
      "verification_result": "ProofValidated",
      "@id": "8318b788-c6f8-438e-93ed-f00e9e1b5f3c",
      "requested_presentation": {
        "predicates": {},
        "self_attested_attrs": {},
        "identifiers": [{
          "schema_id": "VzJn8BMiQDhitA7BSmTthe:2:Diploma 5361e670:0.1",
          "cred_def_id": "VzJn8BMiQDhitA7BSmTthe:3:CL:13094:latest"
        }],
        "revealed_attrs": {
          "name": {
            "identifier_index": 0,
            "value": "Joe Smith"
          },
          "degree": {
            "identifier_index": 0,
            "value": "Bachelors"
          }
        },
        "unrevealed_attrs": {}
      }
    }
    ```
    * to see if the presentation is valid, evaluate `verification_result`
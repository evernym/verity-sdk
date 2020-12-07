# Verifier Documentation
The getting started guide has more comprehensive explanations for the Verity flow. [Getting Started Guide](../getting-started/getting-started.md)

Here are basic code examples showing how to interface with verity-sdk to: 
1. Create an Agent on Verity - [Provision](../getting-started/dotnet-verity-flow.md#provisioning-agent-on-verity)
2. Handle asynchronous response messages from Verity - [Message Handling](../getting-started/dotnet-verity-flow.md#handling-asynchronous-response-messages)
3. Setting up an Issuer - [Issuer Setup](../getting-started/dotnet-verity-flow.md#setting-up-an-issuer-identity)
4. Writing a schema to the ledger - [Write Schema](../getting-started/dotnet-verity-flow.md#write-schema-to-ledger)
5. Writing a credential definition to the ledger - [Write Credential Definition](../getting-started/dotnet-verity-flow.md#write-credential-definition-to-ledger)
6. Establishing Connections between parties - [Connecting](../getting-started/dotnet-verity-flow.md#connecting)
7. Issuing credentials - [Issue Credential](../getting-started/dotnet-verity-flow.md#issue-credential)
8. Requesting Proof Presentations - [Request Proof Presentation](../getting-started/dotnet-verity-flow.md#request-proof-presentation)
9. Utils for saving verity-sdk context and registering Message Handlers  - [Utils](../getting-started/dotnet-verity-flow.md#utils)

## Setup
### Provisioning agent on Verity
Provisioning is the first step done when interacting with Verity. It creates a dedicated cloud agent on Verity for the user of the sdk. Provisioning is done only once.

```
const string VERITY_CONTEXT_STORAGE = "verity-context.json";
string verityUrl = ...
// token used for provisioning - Evernym provides this offline for their customers
string token = ...
ProvisionV0_7 provisioner;
provisioner = Provision.v0_7(token);

// create initial Context
Context ctx = ContextBuilder.fromScratch("examplewallet1", "examplewallet1", verityUrl);
context = provisioner.provision(ctx);

// Save context
File.WriteAllText(VERITY_CONTEXT_STORAGE, context.toJson().ToString());
```

The wallet (by default created in $HOME/.indy_client/wallet/<wallet-name>) needs to be saved with the context file.

## Handling Asynchronous response messages
### Setting up Webhook
Most Verity response messages are sent asynchronously. For receiving messages, a public endpoint is needed. The UpdateEndpoint protocol should be used for setting up the address of this endpoint.
The endpoint dedicated for receiving messages from Verity Server, may look like this:

```
// New endpoint is set
string webhook = 
context = context.ToContextBuilder().endpointUrl(webhook).build();

// request that verity-application use specified webhook endpoint
UpdateEndpoint.v0_6().update(context);

// Save context after updating the context
File.WriteAllText(VERITY_CONTEXT_STORAGE, context.toJson().ToString());
```

Example Webhook
```
 public class Listener
    {
        public delegate void OnListen(string message);

        int port;
        OnListen handler;
        private HttpListener listener = null;

        public Listener(int port, OnListen handler)
        {
            this.port = port;
            this.handler = handler;
        }

        public void listen()
        {
            var prefix = $"http://*:{port}/";
            listener = new HttpListener();
            listener.Prefixes.Add(prefix);
            try
            {
                listener.Start();
                App.consoleOutput($"Started listening...");
            }
            catch (HttpListenerException ex)
            {
                App.consoleOutput(ex.Message);
                return;
            }

            Task.Run(() => { ProcessRequest(); });
            
        }

        public void stop()
        {
            listener.Stop();
            listener.Close();
        }

        private void ProcessRequest()
        {
            while (listener.IsListening)
            {
                var context = listener.GetContext();

                // Get the data from the HTTP stream
                var data = new StreamReader(context.Request.InputStream).ReadToEnd();

                // Process 
                handler(data);

                //Answer
                byte[] b = Encoding.UTF8.GetBytes("Success");
                context.Response.StatusCode = 200;
                context.Response.KeepAlive = false;
                context.Response.ContentLength64 = b.Length;

                var output = context.Response.OutputStream;
                output.Write(b, 0, b.Length);
                context.Response.Close();
            }
        }
    }
```

### Response message handling 
Most Verity interactions respond to a request asynchronously. Here are some details that will help with the handling of these messages.
1. A response message is delivered via HTTPs. These messages can be processed however the application thinks best. Our example applications use webhooks.
    The http body will contain an encrypted protocol message which needs to be handled by the [Handlers](../getting-started/dotnet-verity-flow.md#registers-message-handler) object. Decryption of the message happens here.
    ```
    listener = new Listener(listenerPort, (encryptedMessageFromVerity) =>
    {
        try
        {
            handlers.handleMessage(context, Encoding.UTF8.GetBytes(encryptedMessageFromVerity));
        }
        catch (Exception ex)
        {
            App.consoleOutput("! Exception: " + ex.Message);
        }
    });

    listener.listen();
    ``` 
2. Common Fields which show up in a response: 
   - `@type` - \<did info>;spec/\<message family>/\<version of protocol>/\<protocol message>" 
    > **Example:** "did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/public-identifier-created"
   - `@id` - An assigned message identifier
   - `~thread`: `{"thid":"<id>"}`
   - Message specific fields
3. Example handler: 
    - Registering the handler [Registers Message Handler](../getting-started/dotnet-verity-flow.md#registers-message-handler)
    - Protocol Message handlers: 
        > **NOTE:** The MessageFamily in this example is an instance of ConnectionsV1_0.
    ```
    // handler for the accept message sent when connection is accepted
    handlers.addHandler(
      handler,
      (msgName, message) =>
      {
          if ("request-received".Equals(msgName))
          {
              requestReceived = true;
          }
          else if ("response-sent".Equals(msgName))
          {
              startResponse = true;
          }
          else
          {
              nonHandled(msgName, message);
          }
      }
    );
    ```

## Setting up an Issuer identity
When an entity issues a credential, they need to have privileged keys on the ledger. This is the step to create \
the issuer keys and register them on the dedicated cloud agent so that writing to the ledger and issuing credentials can be accomplished.
### Check to see if Issuer is already setup
Checks to see if issuer setup has been done. Gets did and verkey from the Verity Application
```
// constructor for the Issuer Setup protocol
IssuerSetupV0_6 issuerSetup = IssuerSetup.v0_6();
// 1. query the current identifier
// 2. application's message handler should handle the asynchronous response
issuerSetup.currentPublicIdentifier(context);
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
    - `did`: issuer did
    - `verkey`: issuer verkey

### Setup new Issuer
```
// constructor for the Issuer Setup protocol
IssuerSetupV0_6 newIssuerSetup = IssuerSetup.v0_6();

// 1. request that issuer identifier be created
// 2. application's message handler should handle the asynchronous response
newIssuerSetup.create(context);
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
string INSTITUTION_NAME = "Faber College";
string LOGO_URL = "https://freeiconshop.com/wp-content/uploads/edd/bank-flat.png";

UpdateConfigsV0_6 updateConfigs = UpdateConfigs.v0_6(INSTITUTION_NAME, LOGO_URL);
updateConfigs.update(context);
updateConfigs.status(context);
```

## Write Schema to Ledger
When data is going to be shared via credential exchange, the data needs to be publicaly defined. 
This is done by writing a schema to the ledger. Different issuers can create credentials that use this defined Schema. [Issuer Setup](../getting-started/dotnet-verity-flow.md#setting-up-an-issuer-identity) must be complete to have the proper permissions.
```
// input parameters for schema
string schemaName = "Diploma " + Guid.NewGuid().ToString().Substring(0, 8);
string schemaVersion = "0.1";

// constructor for the Write Schema protocol
WriteSchemaV0_6 writeSchema = WriteSchema.v0_6(schemaName, schemaVersion, "name", "degree");
// add handler to the set of handlers

// 1. request schema be written to ledger
// 2. application's message handler should handle the asynchronous response
writeSchema.write(context);
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
An issuer will write a credential definition to the ledger which corresponds to a specific Schema. \
This is how an entity can publicaly define the data which will be sent in a credential.
* `schemaId`: received in the write schema response [Write Schema](../getting-started/dotnet-verity-flow.md#write-schema-to-ledger)

```
// input parameters for cred definition
string credDefName = "Trinity Collage Diplomas";
string credDefTag = "latest";

// constructor for the Write Credential Definition protocol
WriteCredentialDefinitionV0_6 def = WriteCredentialDefinition.v0_6(credDefName, _schemaIdRef, credDefTag);

// add handler to the set of handlers

// 1. request the cred def be writen to ledger
// 2. application's message handler should handle the asynchronous response
def.write(context);
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
Connecting creates communication channel to interact on. This process creates keys specifically for this interaction. Data can be requested and delivered over this channel.

### Creating an Invitation with Relationship Protocol
We create an api call which will return the invitation. That invitation can be converted to QR code which can be scanned by Connect.me.

The Relationship protocol has two steps: 
1. create relationship key 
    ```
    RelationshipV1_0 relProvisioning = Relationship.v1_0("Faber College");

    // add handler to the set of handlers

    // starts the relationship protocol
    relProvisioning.create(context);
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
        RelationshipV1_0 relationship = Relationship.v1_0(_relDID, _threadId);
        relationship.connectionInvitation(context);
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
Once the mobile device receives the `inviteURL` or scans the QR Code, the rest of connection establishment is automated in verity-sdk. \
You can setup a handler to view incoming messages.
```
handlers.addHandler(
      handler,
      (msgName, message) =>
      {
          if ("request-received".Equals(msgName))
          {
              requestReceived = true;
          }
          else if ("response-sent".Equals(msgName))
          {
              startResponse = true;
          }
          else
          {
              nonHandled(msgName, message);
          }
      }
    );
```

## Issue Credential
When an entity provides data to another party, the Issue Credential protocol is used. Both the [Issuer Setup](../getting-started/dotnet-verity-flow.md#setting-up-an-issuer-identity) and [Write Credential Definition](../getting-started/dotnet-verity-flow.md#write-credential-definition-to-ledger) protocols need to have been completed.

The Issue Credential has two steps: 

1. Send the Credential Offer
* `defId`: received in the credential definition response [Credential Definition Response](../getting-started/dotnet-verity-flow.md#write-credential-definition-to-ledger)    
* `relDID`: received in the create Relationship response [Creating Relationship](../getting-started/dotnet-verity-flow.md#creating-an-invitation-with-relationship-protocol)
  ```
  // input parameters for issue credential
  string credentialName = "Degree";
  Dictionary<string, string> credentialData = new Dictionary<string, string>();
  credentialData.Add("name", "Alice Smith");
  credentialData.Add("degree", "Bachelors");
   
  // constructor for the Issue Credential protocol
  IssueCredentialV1_0 issue = IssueCredential.v1_0(_relDID, _defIdRef, credentialData, credentialName, "0", true);
    
  //add handler for credential offer to the set of handlers
   
  // 1. request that credential is offered
  // 2. application's message handler should handle the asynchronous response
  issue.offerCredential(context);
   ```
   
2. Send the Credential once the holder sends a `accept-request` - This is automated in the sdk

## Request Proof Presentation
When an entity requests a party prove specific things by providing self attested information or information corresponding to an already issued credential, the Proof Presentation protocol is used. 

* `issuerDID`: received in the IssuerSetup response [Issuer Setup](../getting-started/dotnet-verity-flow.md#setting-up-an-issuer-identity)
* `relDID`: received in the create Relationship response [Creating Relationship](../getting-started/dotnet-verity-flow.md#creating-an-invitation-with-relationship-protocol)
```
// global issuer_did
// input parameters for request proof
string proofName = "Proof of Degree"
Restriction restriction = RestrictionBuilder
        .blank()
        .issuerDid(_issuerDID)
        .build();

Protocols.PresentProof.Attribute nameAttr = PresentProofV1_0.attribute("name", restriction);
Protocols.PresentProof.Attribute degreeAttr = PresentProofV1_0.attribute("degree", restriction);

// constructor for the Present Proof protocol
PresentProofV1_0 proof = PresentProof.v1_0(_relDID, proofName, nameAttr, degreeAttr);

// 1. request proof
// 2. application's message handler should handle the asynchronous response
proof.request(context);
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
    
## Utils
### Object used to register response message handlers
The `handlers` variable is defined as field variable in the controller class like this:
<a name="Handlers"></a>
```
public Handlers handlers { get; set; } = new Handlers();
```

### Registers Message Handler
Sets a specific response handler for protocol interactions
<a name="handle"></a>
```
handlers.handleMessage(context, Encoding.UTF8.GetBytes(encryptedMessageFromVerity));
``` 
### Loading Context Object
Saved context should be loaded with code like this:
```
Context loadContext()
        {
            var data = File.ReadAllText(VERITY_CONTEXT_STORAGE);

            var ctx_b = ContextBuilder.fromJson(data);
            var ctx = ctx_b.build();

            return ctx;
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

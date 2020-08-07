# Verifier Documentation
It is assumed that Java Spring Framework is used.

## Utils:
### Object used to register response message handlers
The `handlers` variable is defined as field variable in the controller class like this:
<a name="Handlers"></a>
```java
Handlers handlers = new Handlers();
```
### Registers Message Handler
Sets a specific response handler for protocol interactions
<a name="handle"></a>
```java
MessageFamily messageFamily = ...; 
MessageHandler.Handler messageHandler = ...;
handlers.addHandler(messageFamily, (String msgName, JSONObject message) -> {
    try {
        messageHandler.handle(msgName, message);
    } catch(Exception ex) {
        ex.printStackTrace();
    }
});
```
### Loading Context Object
Saved context should be loaded with code like this:
```java
Context loadContext(File contextFile) throws IOException, WalletOpenException {
    return ContextBuilder.fromJson(
            new JSONObject(
                    new String(Files.readAllBytes(contextFile.toPath()))
            )
    ).build();
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

```java
File contextFile = new File("verity-context.json");
String verityUrl = "http://verity.url";
// token used for provisioning - Evernym provides this offline for their customers
String token = "..."; 

// First we create an initial context.
Context ctx = ContextBuilder.fromScratch("wallet-name", "wallet-secret-key", verityUrl);

// do provisioning and get the context.
ProvisionV0_7 provisioner = Provision.v0_7(token);
ctx = provisioner.provision(ctx);

// save the context to file.
Files.write(contextFile.toPath(), ctx.toJson().toString(2).getBytes());
```
The wallet (usualy created in $HOME/.indy_client/wallet/<wallet-name>) needs to be saved with the context file.

## Handling Asynchronous response messages
### Setting up Webhook
For receiving messages an endpoint is needed. The UpdateEndpoint protocol should be used for setting up the address of this endpoint.
The endpoint dedicated for receiving messages from Verity Server, may look like this:
//TODO: RTM -> Document UpdateEndpoint

```java
// Needed if an updated endpoint is used

String webhook = "";
try {
    webhook = context.endpointUrl();
} catch (Exception ignored) {}

context = context.toContextBuilder().endpointUrl(webhook).build();

// request that verity-application use specified webhook endpoint
UpdateEndpoint.v0_6().update(context);
```

Example Webhook
```java
@PostMapping("/verity-webhook")
public String webHook(HttpEntity<byte[]> requestEntity) throws VerityException {
    handlers.handleMessage(context, requestEntity.getBody());
    return "OK";
}
```

### Response message handling 
Most Verity interactions respond to a request asynchronously. Here are some details that will help with the handling of these messages.
1. A response message is delivered via HTTPs. These messages can be processed however the application thinks best. Our example applications use webhooks.
    The http body will contain an encrypted protocol message which needs to be handled by the [Handlers](../howto/java-verity-flow.md#Registers Message Handler) object. Decryption of the message happens here.
    ```java
    handlers.handleMessage(context, requestEntity.getBody());
    ``` 
2. Common Fields which show up in a response: 
   - `@type` - \<did info>;spec/\<message family>/\<version of protocol>/\<protocol message>" 
    > **Example:** "did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/public-identifier-created"
   - `@id` - An assigned message identifier
   - `~thread`: `{"thid":"<id>"}`
   - Message specific fields
3. Example handler: 
    - Registering the handler [Registers Message Handler](../howto/java-verity-flow.md#Registers Message Handler)
    - Protocol Message handlers: 
        > **NOTE:** The MessageFamily in this example is an instance of ConnectionsV1_0.
    ```java
    handle(Connecting.v1_0("",""), (String msgName, JSONObject message) -> {
        if("request-received".equals(msgName)) {
            // Received the Request
        } else if("response-sent".equals(msgName)) {
            // Response sent
        } else {
           // Message received but not supported by the Message Family. 
        }
    });
    ```

## Setting up an Issuer identity
### Check to see if Issuer is already setup
Checks to see if issuer setup has been done. Gets did and verkey from the Verity Application
```java
// These values need to be saved during the handling of the response
private String issuerDID;
private String issuerVerkey;

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
    - `did`: newly created issuer did
    - `verkey`: newly created issuer verkey

### Setup new Issuer
```java
// These values need to be saved during the handling of the response
private String issuerDID;
private String issuerVerkey;

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
```java
String INSTITUTION_NAME = "Faber College";
String LOGO_URL = "http://robohash.org/235";

UpdateConfigsV0_6 updateConfigs = UpdateConfigs.v0_6(INSTITUTION_NAME, LOGO_URL);
updateConfigs.update(context);
updateConfigs.status(context);
```

## Write Schema to Ledger
```java
// input parameters for schema
String schemaName = "Diploma "+ UUID.randomUUID().toString().substring(0, 8);
String schemaVersion = "0.1";

// constructor for the Write Schema protocol
WriteSchemaV0_6 writeSchema = WriteSchema.v0_6(schemaName, schemaVersion, "name", "degree");

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
* `schemaId`: received in the write schema response [Write Schema](../howto/java-verity-flow.md#Write Schema to Ledger)

```java
private String writeLedgerCredDef(String schemaId) throws IOException, VerityException {
    // input parameters for cred definition
    String credDefName = "Trinity Collage Diplomas";
    String credDefTag = "latest";

    // constructor for the Write Credential Definition protocol
    WriteCredentialDefinitionV0_6 def = WriteCredentialDefinition.v0_6(credDefName, schemaId, credDefTag);

    // 1. request the cred def be writen to ledger
    // 2. application's message handler should handle the asynchronous response
    def.write(context);
}
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
    ```java
    RelationshipV1_0 relProvisioning = Relationship.v1_0("inviter");

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
    - `relDid` and `threadId` were given in the `created` response
        ```java
        RelationshipV1_0 relationship = Relationship.v1_0(relDID.get(), threadId.get());
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
Once the mobile device receives the `inviteURL` or scans the QR Code, the rest is automated in verity-sdk. \
You can setup a handler to view incoming messages.
```java
handlers.addHandler(Connecting.v1_0("",""), (String msgName, JSONObject message) -> {
    if("request-received".equals(msgName)) {
        // Received the Request
    } else if("response-sent".equals(msgName)) {
        // Response sent
    } 
});
```

## Issue Credential
The Issue Credential has two steps: 
 * `defId`: received in the credential definition response [Credential Definition Response](../howto/java-verity-flow.md#Write Credential Definition to Ledger)    
 * `forDID`: received in the create Relationship response [Creating Relationship](../howto/java-verity-flow.md#Creating an Invitation with Relationship Protocol)
   

1. Send the Credential Offer
    ```java
    // input parameters for issue credential
    String credentialName = "Degree";
    Map<String, String> credentialData = new HashMap<>();
    credentialData.put("name", "Joe Smith");
    credentialData.put("degree", "Bachelors");
    // constructor for the Issue Credential protocol
    IssueCredentialV1_0 issue = IssueCredential.v1_0(forDID, defId, credentialData, "comment", "0", true);

    // request that credential is offered
    issue.offerCredential(context);
    ```
   
2. Send the Credential once the holder sends a `accept-request` - This is automated in the sdk

## Request Proof Presentation
* `issuerDID`: received in the IssuerSetup response [Issuer Setup](../howto/java-verity-flow.md#Setting up an Issuer identity)
* `forDID`: received in the create Relationship response [Creating Relationship](../howto/java-verity-flow.md#Creating an Invitation with Relationship Protocol)
```java
// input parameters for request proof
String proofName = "Proof of Degree - "+UUID.randomUUID().toString().substring(0, 8);

Restriction restriction =  RestrictionBuilder
        .blank()
        .issuerDid(issuerDID)
        .build();

Attribute nameAttr = PresentProofV1_0.attribute("name", restriction);
Attribute degreeAttr = PresentProofV1_0.attribute("degree", restriction);

// constructor for the Present Proof protocol
PresentProofV1_0 proof = PresentProof.v1_0(forDID, proofName, nameAttr, degreeAttr);

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

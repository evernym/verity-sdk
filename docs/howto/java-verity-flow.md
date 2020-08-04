# Verifier Documentation

It is assumed that Java Spring Framework is used.

## Helpers:
The `handlers` variable is defined as field variable in the controller class like this:
```java
Handlers handlers = new Handlers();
```
### Generic Handler
Sets a specific response handler for a protocol interactions
<a name="handle"></a>
```java
void handle(MessageFamily messageFamily, MessageHandler.Handler messageHandler) {
    handlers.addHandler(messageFamily, (String msgName, JSONObject message) -> {
        try {
            messageHandler.handle(msgName, message);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    });
}
```
## Setup
### Provisioning agent on verity
Provisioning is done only once.

```java
void provisionAgent() throws VerityException, IOException {
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
}
```
The wallet (usualy created in $HOME/.indy_client/wallet/<wallet-name>) needs to be saved with the context file.
### Application Preperation
#### Loading Context Object
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

#### Setting up Webhook
For receiving messages an endpoint is needed. The UpdateEndpoint protocol should be used for setting up the address of this endpoint.
The endpoint dedicated for receiving messages from Verity Server, may look like this:

```java
@PostMapping("/verity-webhook")
public String webHook(HttpEntity<byte[]> requestEntity) throws VerityException {
    handlers.handleMessage(context, requestEntity.getBody());
    return "OK";
}
```

### Setting up an Issuer identity
```java
```
Message handler for issuer setup responses from verity should be added (for eg in constructor)
```java
private String issuerDID;
private String issuerVerkey;

void issuerIdentifier() throws IOException, VerityException {
    // constructor for the Issuer Setup protocol
    IssuerSetupV0_6 issuerSetup = IssuerSetup.v0_6();

    AtomicBoolean issuerComplete = new AtomicBoolean(false); // spinlock bool

    issuerIdentifierHandler(issuerSetup, issuerComplete);
    
    // query the current identifier
    issuerSetup.currentPublicIdentifier(context);

    // wait for response from verity-application
    waitFor(issuerComplete, "Waiting for current issuer DID");
}
```
[Uses Generic Handler](../howto/java-verity-flow.md#Generic Handler)
```java
void issuerIdentifierHandler(IssuerSetupV0_6 issuerSetup, AtomicBoolean issuerComplete) {
    // handler for current issuer identifier message
    handle(issuerSetup, (String msgName, JSONObject message) -> {
        if("public-identifier".equals(msgName))
        {
            printlnMessage(msgName, message);
            issuerDID = message.getString("did");
            issuerVerkey = message.getString("verKey");
        }
        issuerComplete.set(true);
    });
}
```
### Updating Config
Update agent configs to set the institution's name and logo url. 
```java
void updateConfigs() throws IOException, VerityException {
    String INSTITUTION_NAME = "Faber College";
    String LOGO_URL = "http://robohash.org/235";

    UpdateConfigsV0_6 updateConfigs = UpdateConfigs.v0_6(INSTITUTION_NAME, LOGO_URL);
    updateConfigs.update(context);
    updateConfigs.status(context);
}
```
## Write Schema to Ledger
```java
private String writeLedgerSchema() throws IOException, VerityException {
    // input parameters for schema
    String schemaName = "Diploma "+ UUID.randomUUID().toString().substring(0, 8);
    String schemaVersion = "0.1";

    // constructor for the Write Schema protocol
    WriteSchemaV0_6 writeSchema = WriteSchema.v0_6(schemaName, schemaVersion, "name", "degree");

    AtomicBoolean schemaComplete = new AtomicBoolean(false); // spinlock bool
    AtomicReference<String> schemaIdRef = new AtomicReference<>();

    writeSchemaHandler(writeSchema, schemaIdRef, schemaComplete);
    
    // request schema be written to ledger
    writeSchema.write(context);

    // wait for operation to be complete
    waitFor(schemaComplete, "Waiting to write schema to ledger");
    // returns ledger schema identifier
    return schemaIdRef.get();
}
```
[Uses Generic Handler](../howto/java-verity-flow.md#Generic Handler)
```java
private void writeSchemaHandler(WriteSchemaV0_6 writeSchema, 
                                AtomicReference<String> schemaIdRef, 
                                AtomicBoolean schemaComplete) {
    // handler for message received when schema is written
    handle(writeSchema, (String msgName, JSONObject message) -> {
        if("status-report".equals(msgName)) {
            printlnMessage(msgName, message);
            schemaIdRef.set(message.getString("schemaId"));
            schemaComplete.set(true);

        }
        else {
            nonHandled("Message Name is not handled - "+msgName);
        }
    });
}
```
## Write Credential Definition to Ledger
```java
private String writeLedgerCredDef(String schemaId) throws IOException, VerityException {

        // input parameters for cred definition
        String credDefName = "Trinity Collage Diplomas";
        String credDefTag = "latest";

        // constructor for the Write Credential Definition protocol
        WriteCredentialDefinitionV0_6 def = WriteCredentialDefinition.v0_6(credDefName, schemaId, credDefTag);

        AtomicBoolean defComplete = new AtomicBoolean(false); // spinlock bool
        AtomicReference<String> defIdRef = new AtomicReference<>();

        writeCredDefHandler(def, defIdRef, defComplete);
        
        // request the cred def be writen to ledger
        def.write(context);
        // wait for operation to be complete
        waitFor(defComplete, "Waiting to write cred def to ledger");
        // returns ledger cred def identifier
        return defIdRef.get();
    }
```
[Uses Generic Handler](../howto/java-verity-flow.md#Generic Handler)
```java   
    private void writeCredDefHandler(WriteCredentialDefinitionV0_6 def,
                                    AtomicReference<String> defIdRef,
                                    AtomicBoolean defComplete) {
        // handler for message received when schema is written
        handle(def, (String msgName, JSONObject message) -> {
            if("status-report".equals(msgName)) {
                printlnMessage(msgName, message);
                defIdRef.set(message.getString("credDefId"));
                defComplete.set(true);

            }
            else {
                nonHandled("Message Name is not handled - "+msgName);
            }
        });
    }
```
## Connecting

### Creating an Invitation (Relationship Protocol)
We create an api which will return the invitation. That invitation can be converted to QR code which can be scanned by Connect.me.
This code is just an example, it does not handle error cases.

```java
private String createRelationship() throws IOException, VerityException {
    // Relationship protocol has two steps
    // 1. create relationship key
    // 2. create invitation

    RelationshipV1_0 relProvisioning = Relationship.v1_0("inviter");

    // handler for the response to the request to start the Connecting protocol.
    AtomicBoolean startRelationshipComplete = new AtomicBoolean(false);
    AtomicBoolean invitationComplete = new AtomicBoolean(false);
    AtomicReference<String> threadId = new AtomicReference<>();
    AtomicReference<String> relDID = new AtomicReference<>();

   relationshipHandler(relProvisioning, threadId, relDID, startRelationshipComplete, invitationComplete); 
   
    relProvisioning.create(context);

    waitFor(startRelationshipComplete, "Waiting to start relationship");

    RelationshipV1_0 relationship = Relationship.v1_0(relDID.get(), threadId.get());
    relationship.connectionInvitation(context);
    waitFor(invitationComplete, "Waiting for invite");

    // return owning DID for the connection
    return relDID.get();
}
```
[Uses Generic Handler](../howto/java-verity-flow.md#Generic Handler)
```java   
private void relationshipHandler(RelationshipV1_0 relProvisioning, 
                                 AtomicReference<String> threadId,
                                 AtomicReference<String> relDID,
                                 AtomicBoolean startRelationshipComplete,
                                 AtomicBoolean invitationComplete) {
    handle(relProvisioning, (String msgName, JSONObject message) -> {
        if("created".equals(msgName))
        {
            printlnMessage(msgName, message);
            threadId.set(message.getJSONObject("~thread").getString("thid"));
            relDID.set(message.getString("did"));

            startRelationshipComplete.set(true);
        }
        else if("invitation".equals(msgName)){
            printlnMessage(msgName, message);
            String inviteURL = message.getString("inviteURL");

            try {
                QRCode.from(inviteURL).withSize(500, 500)
                        .writeTo(new FileOutputStream(new File("qrcode.png")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (!(System.getenv("HTTP_SERVER_URL") == null) ) {
                println("Open the following URL in your browser and scan presented QR code");
                println(ANSII_GREEN + System.getenv("HTTP_SERVER_URL") + "/java-example-app/qrcode.html" + ANSII_RESET);
            }
            else {
                println("QR code generated at: qrcode.png");
                println("Open this file and scan QR code to establish a connection");
            }

            invitationComplete.set(true);
        }
        else {
            nonHandled("Message Name is not handled - " + msgName);
        }
    });
}
```

### Connection Listener 
Most of this is automated in verity-sdk. You can setup a handler to view incoming messages.
```java
handlers.addHandler(Connecting.v1_0("",""), (String msgName, JSONObject message) -> {
    if("request-received".equals(msgName)) {
        // Received the Request
    } else if("response-sent".equals(msgName)) {
        // Response sent
    } else {
        nonHandled("Message Name is not handled - "+msgName);
    }
});
```

## Issue Credential
```java
private void issueCredential(String forDID, String defId) throws IOException, VerityException, InterruptedException {
    // input parameters for issue credential
    String credentialName = "Degree";
    Map<String, String> credentialData = new HashMap<>();
    credentialData.put("name", "Joe Smith");
    credentialData.put("degree", "Bachelors");
    // constructor for the Issue Credential protocol
    IssueCredentialV1_0 issue = IssueCredential.v1_0(forDID, defId, credentialData, "comment", "0", true);

    AtomicBoolean offerSent = new AtomicBoolean(false); // spinlock bool
    AtomicBoolean credSent = new AtomicBoolean(false); // spinlock bool

    issueCredentialHandler(issue, offerSent, credSent);
    
    // request that credential is offered
    issue.offerCredential(context);
    waitFor(offerSent, "Wait for offer to be sent");

    waitFor(credSent, "Wait for Connect.me to request the credential and credential to be sent");

    Thread.sleep(3000); // Give time for Credential to get to mobile device
}
```
[Uses Generic Handler](../howto/java-verity-flow.md#Generic Handler)
```java   
private void issueCredentialHandler(IssueCredentialV1_0 issue, AtomicBoolean offerSent, AtomicBoolean credSent) {
    // handler for signal messages
    handle(issue, (String msgName, JSONObject message) -> {
        if("sent".equals(msgName) && !offerSent.get()) {
            offerSent.set(true);
        }
        else if("sent".equals(msgName)) {
            credSent.set(true);
        }
        else {
            nonHandled("Message Name is not handled - "+msgName);
        }
    });
}
```
## Request Proof Presentation
```java
private void requestProof(String forDID) throws IOException, VerityException {
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

    AtomicBoolean proofComplete = new AtomicBoolean(false); // spinlock bool

    requestProofHandler(proof, proofComplete);
    
    // request proof
    proof.request(context);
    // wait for connect.me user to present the requested proof
    waitFor(proofComplete, "Waiting for proof presentation from Connect.me");
}
```
[Uses Generic Handler](../howto/java-verity-flow.md#Generic Handler)
```java
private void requestProofHandler(PresentProofV1_0 proof, AtomicBoolean proofComplete) {
    // handler for the result of the proof presentation
    handle(proof, (String msgName, JSONObject message) -> {
        if("presentation-result".equals(msgName)) {
            printlnMessage(msgName, message);
            proofComplete.set(true);
        } else {
            nonHandled("Message Name is not handled - "+msgName);
        }
    });
}
```

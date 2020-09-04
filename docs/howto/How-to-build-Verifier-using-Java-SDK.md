# How to build Verifier using Java SDK
​
This is a tutorial for building a Verifier application using Java SDK. This Verifer app requiers Verity Application instance provided in the cloud by Evernym or in local environment in order to have end2end functionality. The Verifier app in this tutorial is built as a web service that establishes DID connections with end users who can either use mobile or cloud wallets and sends presentation requests for verifiable data from them so that Verifier app can make certain decisions based on the received verifiable data. Verifier app serves couple of endpoints, first one to serve Connection invitations and the second one to serve as a webhook for receiving async responses from Verity Application.

This guide will walk you through how to provision the SDK, how to set up different endpoints and how to create create different message handlers. We are assuming that the Java SDK and all the dependencies are installed. In this tutorial, we are using Java Spring Framework.
​
## Provisioning agent on Verity Application
Provisioning is a process that 'registers' the SDK and Verifier app with Verity Application. It creates and agent of Verity Application and set of key pairs that are exchanged during provisioning so that all future communication between Verifier app and Verity Application and encrypted. Provisioning is a process that is done only once. 
​
```java
void provisionAgent() throws VerityException, IOException {
    File contextFile = new File("verity-context.json");
    String verityUrl = "http://verity.url";
    String token = "..."; // token used for provisioning
​
    // First we create an initial context.
    Context ctx = ContextBuilder.fromScratch("wallet-name", "wallet-secret-key", verityUrl);
    
    // do provisioning and get the context.
    ProvisionV0_7 provisioner = Provision.v0_7(token);
    ctx = provisioner.provision(ctx);
​
    // save the context to file.
    Files.write(contextFile.toPath(), ctx.toJson().toString(2).getBytes());
}
```
As a result of provisioning, context file is created and needs to be saved for later use. The wallet that holds the keys for secure communication with Verity Applicaiton (usualy created in $HOME/.indy_client/wallet/<wallet-name>) needs to be saved with the context file.
​
## Verifier app preparation
Saved context should be loaded with a code like this:
```java
Context loadContext(File contextFile) throws IOException, WalletOpenException {
    return ContextBuilder.fromJson(
            new JSONObject(
                    new String(Files.readAllBytes(contextFile.toPath()))
            )
    ).build();
}
```
​
For receiving async responsed from Verity Application, an endpoint is needed. UpdateEndpoint protocol should be used for setting up the address of this endpoint.
The endpoint dedicated for receiving messages from Verity Server, may look like this:
​
```java
@PostMapping("/verity-webhook")
public String webHook(HttpEntity<byte[]> requestEntity) throws VerityException {
    handlers.handleMessage(context, requestEntity.getBody());
    return "OK";
}
```
​
`handlers` variable is defined as field variable in the controller class like this:
```java
Handlers handlers = new Handlers();
```
​
For establishing DID connections with end users, the idea is to create a endpoint `/invitation` which returns invitation string to end user's mobile or cloud wallet.
Once end user's mobile or cloud wallet establishes connection with Verity Application, we will proceed with requesting the end user to present proof.
​
## Creating an invitation
​
Endpoint `\invitation` which returns the invitation is created. The invitation that is returned can be converted to QR code and presented on a web page or it can be returned directly to end user's mobile or cloud wallet.
NOTE Code below is an example and it does not handle error cases.
​
```java
static class RelationshipState {
    public String did;
    public CompletableFuture<String> invitation = new CompletableFuture<String>();
}
​
// maps relationship threadId to RelationshipState
ConcurrentHashMap<String, RelationshipState> relationships = new ConcurrentHashMap<String, RelationshipState>();
​
@GetMapping("/invitation")
public String invitation() throws IOException, VerityException, ExecutionException {
    RelationshipV1_0 rel = Relationship.v1_0("Company Name", new URL("http://company/logo.png"));
    rel.create(context);
    String threadId = rel.getThreadId();
​
    RelationshipState relState = new RelationshipState();
    relationships.put(threadId, relState);
​
    // wait for invitation
    String invitation = relState.invitation.get();
​
    // did of this relationship could be saved, if needed.
    String did = relState.did;
​
    return invitation;
}
```
​
Message handler for relationship responses from Verity Applicaiton should be added (for eg in constructor)
```java
handlers.addHandler(Relationship.v1_0(null), (String msgName, JSONObject message) -> {
    if("created".equals(msgName))
    {
        String threadId = message.getJSONObject("~thread").getString("thid");
        String did = message.getString("did");
        RelationshipState relState = relationships.get("threadId");
        if (relState != null) {
            relState.did = did;
        } else {
            // log some warning?
        }
​
        try {
            Relationship.v1_0(did, threadId).connectionInvitation(context);
        } catch (IOException | VerityException e) {
            // do something sane
            e.printStackTrace();
        }
    }
    else if("invitation".equals(msgName)) {
        String threadId = message.getJSONObject("~thread").getString("thid");
        String inviteURL = message.getString("inviteURL");
​
        RelationshipState relState = relationships.get("threadId");
        if (relState != null) {
            relState.invitation.complete(inviteURL);
        }
        else {
            // log some warning?
        }
    }
    else {
        // it should not get here, log some warning?
    }
});
​
```
​
## Sending Presentation Request
Once the connection is accepted Verifer app sends presentation request to end users
​
Java handlers for signals for connecting protocol from Verity Application:
```java
// maps connections to boolean representing connected state
ConcurrentHashMap<String, Boolean> connections = new ConcurrentHashMap<String, Boolean>();
​
Message handler for Connecting responses from Verity Application should be added (for eg in constructor)
```java
handlers.addHandler(Connecting.v1_0(null, null), (String msgName, JSONObject message) -> {
    if("request-received".equals(msgName)) {
        // do nothing, connecting request is received.
    } else if("response-sent".equals(msgName)) {
        String did = message.getString("myDID");
    
        // request the presentation request
        Restriction restriction =  RestrictionBuilder
                .blank()
                .issuerDid("some issuer did")
                .build();
        Attribute nameAttr = PresentProofV1_0.attribute("name", restriction);
        Attribute degreeAttr = PresentProofV1_0.attribute("degree", restriction);
​
        PresentProofV1_0 proof = PresentProof.v1_0(did, "DemoPresentation", nameAttr, degreeAttr);
        proof.request(context);
​
        // if needed we can save threadId to match it with response. In our case it is not needed.
        // String threadId = proof.getThreadId();
​
    } else {
        // it should not get here, log some warning?
    }
});
```
​
And for responses for presentation request protocol:
​
```java
handlers.addHandler(PresentProof.v1_0(null, null), (String msgName, JSONObject message) -> {
    if("presentation-result".equals(msgName)) {
        // if needed this is the code which can retrieve threadId from response
        // String threadId = message.getJSONObject("~thread").getString("thid");
        
        // check for verification_result attribute (message.getString("verification_result") == "ProofValidated")?
​
        JSONObject proof = message.getJSONObject("requested_presentation");
        // TODO: use the proof (save in database or similar).
    } else {
        // it should not get here, log some warning?
    }
});
```
/*
 * COPYRIGHT 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.
 */
package com.evernym.sdk.example;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.exceptions.WalletOpenException;
import com.evernym.verity.sdk.protocols.provision.v0_7.ProvisionV0_7;
import com.evernym.verity.sdk.protocols.relationship.Relationship;
import com.evernym.verity.sdk.protocols.relationship.v1_0.RelationshipV1_0;
import com.evernym.verity.sdk.protocols.connecting.Connecting;
import com.evernym.verity.sdk.protocols.connecting.v1_0.ConnectionsV1_0;
import com.evernym.verity.sdk.protocols.issuecredential.IssueCredential;
import com.evernym.verity.sdk.protocols.issuecredential.v1_0.IssueCredentialV1_0;
import com.evernym.verity.sdk.protocols.issuersetup.IssuerSetup;
import com.evernym.verity.sdk.protocols.issuersetup.v0_6.IssuerSetupV0_6;
import com.evernym.verity.sdk.protocols.presentproof.common.Attribute;
import com.evernym.verity.sdk.protocols.presentproof.PresentProof;
import com.evernym.verity.sdk.protocols.presentproof.v1_0.PresentProofV1_0;
import com.evernym.verity.sdk.protocols.presentproof.common.Restriction;
import com.evernym.verity.sdk.protocols.presentproof.common.RestrictionBuilder;
import com.evernym.verity.sdk.protocols.provision.Provision;
import com.evernym.verity.sdk.protocols.questionanswer.CommittedAnswer;
import com.evernym.verity.sdk.protocols.questionanswer.v1_0.CommittedAnswerV1_0;
import com.evernym.verity.sdk.protocols.updateconfigs.v0_6.UpdateConfigsV0_6;
import com.evernym.verity.sdk.protocols.updateendpoint.UpdateEndpoint;
import com.evernym.verity.sdk.protocols.updateconfigs.UpdateConfigs;
import com.evernym.verity.sdk.protocols.writecreddef.WriteCredentialDefinition;
import com.evernym.verity.sdk.protocols.writecreddef.v0_6.WriteCredentialDefinitionV0_6;
import com.evernym.verity.sdk.protocols.writeschema.WriteSchema;
import com.evernym.verity.sdk.protocols.writeschema.v0_6.WriteSchemaV0_6;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.ContextBuilder;
import com.evernym.verity.sdk.utils.Util;
import net.glxn.qrgen.QRCode;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class App extends Helper {
    Integer port = 4000;
    @Override int listenerPort() {return port;}

    private String issuerDID;
    private String issuerVerkey;

    public static void main( String[] args ) {
        new App().execute();
    }

    public void example() throws IOException, VerityException, InterruptedException {
        setup();

        String relDID = createRelationship();
        createConnection(relDID);

//        askQuestion(forDID);

        String schemaId = writeLedgerSchema();
        String defId = writeLedgerCredDef(schemaId);

        issueCredential(relDID, defId);

        requestProof(relDID);
    }


    Context provisionAgent() throws IOException, VerityException {
        ProvisionV0_7 provisioner;
        if (consoleYesNo("Provide Provision Token", true)) {
            String token = consoleInput("Token").trim();
            println("using provision token: " + token);
            provisioner = Provision.v0_7(token);
        } else {
            provisioner = Provision.v0_7();
        }

        String verityUrl = consoleInput("Verity Application Endpoint").trim();

        if ("".equals(verityUrl)) {
            verityUrl = "http://localhost:9000";
        }

        println("Using Url: "+ verityUrl);

        // create initial Context
        Context ctx = ContextBuilder.fromScratch("examplewallet1", "examplewallet1", verityUrl);

        // ask that an agent by provision (setup) and associated with created key pair
        return provisioner.provision(ctx);
    }

    Context loadContext(File contextFile) throws IOException, WalletOpenException {
        return ContextBuilder.fromJson(
                new JSONObject(
                        new String(Files.readAllBytes(contextFile.toPath()))
                )
        ).build();
    }

    void updateWebhookEndpoint() throws IOException, VerityException {
        String webhookFromCtx = "";

        try {
            webhookFromCtx = context.endpointUrl();
        } catch (UndefinedContextException ignored) {}

        String webhook = consoleInput(String.format("Ngrok endpoint for port(%d)[%s]", port, webhookFromCtx)).trim();

        if("".equals(webhook)) {
            webhook = webhookFromCtx;
        }

        println("Using Webhook: "+ webhook);
        context = context.toContextBuilder().endpointUrl(webhook).build();

        // request that verity application use specified webhook endpoint
        UpdateEndpoint.v0_6().update(context);
    }

    void updateConfigs() throws IOException, VerityException {
        String INSTITUTION_NAME = "Faber College";
        String LOGO_URL = "http://robohash.org/235";

        UpdateConfigsV0_6 updateConfigs = UpdateConfigs.v0_6(INSTITUTION_NAME, LOGO_URL);
        updateConfigs.update(context);
        updateConfigs.status(context);
    }

    void setupIssuer() throws IOException, VerityException {
        // constructor for the Issuer Setup protocol
        IssuerSetupV0_6 newIssuerSetup = IssuerSetup.v0_6();

        AtomicBoolean setupComplete = new AtomicBoolean(false); // spinlock bool

        // handler for created issuer identifier message
        handle(newIssuerSetup, (String msgName, JSONObject message) -> {
            if("public-identifier-created".equals(msgName))
            {
                printlnMessage(msgName, message);
                issuerDID = message.getJSONObject("identifier").getString("did");
                issuerVerkey = message.getJSONObject("identifier").getString("verKey");
                setupComplete.set(true);
            }
            else {
                nonHandled("Message Name is not handled - "+msgName);
            }
        });

        // request that issuer identifier be created
        newIssuerSetup.create(context);

        // wait for request to complete
        waitFor(setupComplete, "Waiting for setup to complete");

        println("The issuer DID and Verkey must be on the ledger.");
        println(String.format("Please add DID (%s) and Verkey (%s) to ledger.", issuerDID, issuerVerkey));
        waitFor("Press ENTER when DID is on ledger");
    }

    void issuerIdentifier() throws IOException, VerityException {
        // constructor for the Issuer Setup protocol
        IssuerSetupV0_6 issuerSetup = IssuerSetup.v0_6();

        AtomicBoolean issuerComplete = new AtomicBoolean(false); // spinlock bool

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

        // query the current identifier
        issuerSetup.currentPublicIdentifier(context);

        // wait for response from verity application
        waitFor(issuerComplete, "Waiting for current issuer DID");
    }

    void setup() throws IOException, VerityException {
        File contextFile = new File("verity-context.json");
        if (contextFile.exists()) {
            if (consoleYesNo("Reuse Verity Context (in verity-context.json)", true)) {
                context = loadContext(contextFile);
            } else {
                context = provisionAgent();
            }
        }
        else {
            context = provisionAgent();
        }

        printlnObject(context.toJson(), ">>>", "Context Used:");

        Files.write(contextFile.toPath(), context.toJson().toString(2).getBytes());

        updateWebhookEndpoint();

        updateConfigs();

        issuerIdentifier();

        if (issuerDID == null) {
            setupIssuer();
        }
    }

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

                println("QR code at: qrcode.png");

                invitationComplete.set(true);
            }
            else {
                nonHandled("Message Name is not handled - " + msgName);
            }
        });
        
        relProvisioning.create(context);

        waitFor(startRelationshipComplete, "Waiting to start relationship");

        RelationshipV1_0 relationship = Relationship.v1_0(relDID.get(), threadId.get());
        relationship.connectionInvitation(context);
        waitFor(invitationComplete, "Waiting for invite");

        // return owning DID for the connection
        return relDID.get();
    }

    private String createConnection(String relDID) throws IOException, VerityException {

        // Constructor for the Connecting API
        ConnectionsV1_0 listener = Connecting.v1_0("", "");
        AtomicBoolean requestReceived = new AtomicBoolean(false);
        AtomicBoolean startResponse = new AtomicBoolean(false);

        handle(listener, (String msgName, JSONObject message) -> {
            if("request-received".equals(msgName)) {
                requestReceived.set(true);
            } else if("response-sent".equals(msgName)) {
                startResponse.set(true);
            } else {
                nonHandled("Message Name is not handled - "+msgName);
            }
        });

        waitFor(requestReceived, "Waiting to receive Request");
        waitFor(startResponse, "Responding to connection request");
        return relDID;
    }

//    private void askQuestion(String forDID) throws IOException, VerityException {
//        String questionText = "Hi Alice, how are you today?";
//        String questionDetail = "Checking up on you today.";
//        String[] validResponses = {"Great!", "Not so good."};
//
//        CommittedAnswerV1_0 committedAnswer = CommittedAnswer.v1_0(
//                forDID,
//                questionText,
//                questionDetail,
//                validResponses,
//                true);
//
//
//        AtomicBoolean questionComplete = new AtomicBoolean(false); // spinlock bool
//        handle(committedAnswer, (String msgName, JSONObject message) -> {
//            if("answer-given".equals(msgName))
//            {
//                printlnMessage(msgName, message);
//                questionComplete.set(true);
//
//            }
//            else {
//                nonHandled("Message Name is not handled - "+msgName);
//            }
//        });
//
//        committedAnswer.ask(context);
//        waitFor(questionComplete, "Waiting for Connect.Me to answer the question");
//    }

    private String writeLedgerSchema() throws IOException, VerityException {
        // input parameters for schema
        String schemaName = "Diploma "+ UUID.randomUUID().toString().substring(0, 8);
        String schemaVersion = "0.1";

        // constructor for the Write Schema protocol
        WriteSchemaV0_6 writeSchema = WriteSchema.v0_6(schemaName, schemaVersion, "name", "degree");

        AtomicBoolean schemaComplete = new AtomicBoolean(false); // spinlock bool
        AtomicReference<String> schemaIdRef = new AtomicReference<>();

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

        // request schema be written to ledger
        writeSchema.write(context);

        // wait for operation to be complete
        waitFor(schemaComplete, "Waiting to write schema to ledger");
        // returns ledger schema identifier
        return schemaIdRef.get();
    }

    private String writeLedgerCredDef(String schemaId) throws IOException, VerityException {

        // input parameters for cred definition
        String credDefName = "Trinity Collage Diplomas";
        String credDefTag = "latest";

        // constructor for the Write Credential Definition protocol
        WriteCredentialDefinitionV0_6 def = WriteCredentialDefinition.v0_6(credDefName, schemaId, credDefTag);

        AtomicBoolean defComplete = new AtomicBoolean(false); // spinlock bool
        AtomicReference<String> defIdRef = new AtomicReference<>();

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

        // request the cred def be writen to ledger
        def.write(context);
        // wait for operation to be complete
        waitFor(defComplete, "Waiting to write cred def to ledger");
        // returns ledger cred def identifier
        return defIdRef.get();
    }

    private void issueCredential(String forDID, String defId) throws IOException, VerityException, InterruptedException {
        // input parameters for issue credential
        String credentialName = "Degree";
        Map<String, String> credentialData = new HashMap<>();
        credentialData.put("name", "Joe Smith");
        credentialData.put("degree", "Bachelors");
        // constructor for the Issue Credential protocol
        IssueCredentialV1_0 issue = IssueCredential.v1_0(forDID, defId, credentialData, "comment", "0");

        AtomicBoolean offerSent = new AtomicBoolean(false); // spinlock bool
        AtomicBoolean credRequested = new AtomicBoolean(false); // spinlock bool
        AtomicBoolean credSent = new AtomicBoolean(false); // spinlock bool

        // handler for 'ask_accept` message when the offer for credential is accepted
        handle(issue, (String msgName, JSONObject message) -> {
            if("sent".equals(msgName) && !offerSent.get()) {
                offerSent.set(true);
            }
            else if("accept-request".equals(msgName)) {
                printlnMessage(msgName, message);
                credRequested.set(true);
            }
            else if("sent".equals(msgName)) {
                credSent.set(true);
            }
            else {
                nonHandled("Message Name is not handled - "+msgName);
            }
        });

        // request that credential is offered
        issue.offerCredential(context);
        waitFor(offerSent, "Wait for offer to be sent");

        // wait for connect.me user to accept offer
        waitFor(credRequested, "Wait for Connect.me to request the credential");

        // request that credential be issued
        issue.issueCredential(context);
        waitFor(credSent, "Wait for Credential to be sent");

        Thread.sleep(3000); // Give time for Credential to get to mobile device
    }

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

        // handler for the result of the proof presentation
        handle(proof, (String msgName, JSONObject message) -> {
            if("presentation-result".equals(msgName)) {
                printlnMessage(msgName, message);
                proofComplete.set(true);
            } else {
                nonHandled("Message Name is not handled - "+msgName);
            }
        });

        // request proof
        proof.request(context);
        // wait for connect.me user to present the requested proof
        waitFor(proofComplete, "Waiting for proof presentation from Connect.me");
    }
}

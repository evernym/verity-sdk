/*
 * COPYRIGHT 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.
 */
package com.evernym.sdk.example;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.exceptions.WalletOpenException;
import com.evernym.verity.sdk.protocols.connecting.Connecting;
import com.evernym.verity.sdk.protocols.connecting.v0_6.ConnectingV0_6;
import com.evernym.verity.sdk.protocols.issuecredential.IssueCredential;
import com.evernym.verity.sdk.protocols.issuecredential.v0_6.IssueCredentialV0_6;
import com.evernym.verity.sdk.protocols.issuersetup.IssuerSetup;
import com.evernym.verity.sdk.protocols.issuersetup.v0_6.IssuerSetupV0_6;
import com.evernym.verity.sdk.protocols.presentproof.common.Attribute;
import com.evernym.verity.sdk.protocols.presentproof.PresentProof;
import com.evernym.verity.sdk.protocols.presentproof.v0_6.PresentProofV0_6;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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

        String forDID = createConnection();

//        askQuestion(forDID);

        String schemaId = writeLedgerSchema();
        String defId = writeLedgerCredDef(schemaId);

        issueCredential(forDID, defId);

        requestProof(forDID);
    }



    Context provisionAgent() throws WalletException, IOException, UndefinedContextException {
        String verityUrl = consoleInput("Verity Application Endpoint").trim();

        if ("".equals(verityUrl)) {
            verityUrl = "http://localhost:9000";
        }

        println("Using Url: "+ verityUrl);

        // create initial Context
        Context ctx = ContextBuilder.fromScratch("examplewallet1", "examplewallet1", verityUrl);

        // ask that an agent by provision (setup) and associated with created key pair
        return Provision.v0_6().provisionSdk(ctx);
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

        updateWebhookEndpoint();

        updateConfigs();

        issuerIdentifier();

        if (issuerDID == null) {
            setupIssuer();
        }

        printlnObject(context.toJson(), ">>>", "Context Used:");

        Files.write(contextFile.toPath(), context.toJson().toString(2).getBytes());
    }

    private String createConnection() throws IOException, VerityException {
        // Connecting protocol has to steps
        // 1. Start the protocol and receive the invite
        //  2. Wait for the other participant to accept the invite

        // Step 1

        // Constructor for the Connecting API
        ConnectingV0_6 connecting = Connecting.v0_6(UUID.randomUUID().toString(), true);

        // handler for the response to the request to start the Connecting protocol.
        AtomicBoolean startConnectionComplete = new AtomicBoolean(false); // spinlock bool
        AtomicBoolean connectionComplete = new AtomicBoolean(false); // spinlock bool
        AtomicReference<String> relDID = new AtomicReference<>();
        handle(connecting, (String msgName, JSONObject message) -> {
            if("CONN_REQUEST_RESP".equals(msgName))
            {
                printlnMessage(msgName, message);
                JSONObject invite = message.getJSONObject("inviteDetail");
                relDID.set(invite.getJSONObject("senderDetail").getString("DID"));
                String truncatedInvite = Util.truncateInviteDetails(invite).toString();

                try {
                    QRCode.from(truncatedInvite).withSize(500, 500)
                            .writeTo(new FileOutputStream(new File("qrcode.png")));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                println("QR code at: qrcode.png");

                startConnectionComplete.set(true);
            }
            else if("CONN_REQ_ACCEPTED".equals(msgName)){
                printlnMessage(msgName, message);
                connectionComplete.set(true);
            }
            else {
                nonHandled("Message Name is not handled - "+msgName);
            }
        });

        // starts the connecting protocol
        connecting.connect(context); // Send the connection create message to Verity
        // wait for response from verity application
        waitFor(startConnectionComplete, "Waiting to start connection");

        // Step 2

        // wait for acceptance from connect.me user
        waitFor(connectionComplete, "Waiting for Connect.Me to accept connection");
        // return owning DID for the connection
        return relDID.get();
    }

    private void askQuestion(String forDID) throws IOException, VerityException {
        String questionText = "Hi Alice, how are you today?";
        String questionDetail = "Checking up on you today.";
        String[] validResponses = {"Great!", "Not so good."};

        CommittedAnswerV1_0 committedAnswer = CommittedAnswer.v1_0(
                forDID,
                questionText,
                questionDetail,
                validResponses,
                true);


        AtomicBoolean questionComplete = new AtomicBoolean(false); // spinlock bool
        handle(committedAnswer, (String msgName, JSONObject message) -> {
            if("answer-given".equals(msgName))
            {
                printlnMessage(msgName, message);
                questionComplete.set(true);

            }
            else {
                nonHandled("Message Name is not handled - "+msgName);
            }
        });

        committedAnswer.ask(context);
        waitFor(questionComplete, "Waiting for Connect.Me to answer the question");
    }

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
        IssueCredentialV0_6 issue = IssueCredential.v0_6(forDID, credentialName, credentialData, defId);

        AtomicBoolean offerComplete = new AtomicBoolean(false); // spinlock bool

        // handler for 'ask_accept` message when the offer for credential is accepted
        handle(issue, (String msgName, JSONObject message) -> {
            if("ask-accept".equals(msgName)) {
                printlnMessage(msgName, message);
                offerComplete.set(true);
            }
            else {
                nonHandled("Message Name is not handled - "+msgName);
            }
        });

        // request that credential is offered
        issue.offerCredential(context);
        // wait for connect.me user to accept offer
        waitFor(offerComplete, "Wait for Connect.me to accept the Credential Offer");

        // request that credential be issued
        issue.issueCredential(context);

        Thread.sleep(3000); // Give time for Credential to get to mobile device
    }

    private void requestProof(String forDID) throws IOException, VerityException {
        // input parameters for request proof
        String proofName = "Proof of Degree - "+UUID.randomUUID().toString().substring(0, 8);

        Restriction restriction =  RestrictionBuilder
                .blank()
                .issuerDid(issuerDID)
                .build();

        Attribute nameAttr = PresentProofV0_6.attribute("name", restriction);
        Attribute degreeAttr = PresentProofV0_6.attribute("degree", restriction);

        // constructor for the Present Proof protocol
        PresentProofV0_6 proof = PresentProof.v0_6(forDID, proofName, nameAttr, degreeAttr);

        AtomicBoolean proofComplete = new AtomicBoolean(false); // spinlock bool

        // handler for the result of the proof presentation
        handle(proof, (String msgName, JSONObject message) -> {
            if("proof-result".equals(msgName)) {
                printlnMessage(msgName, message);
                proofComplete.set(true);
            }
            else {
                nonHandled("Message Name is not handled - "+msgName);
            }
        });

        // request proof
        proof.request(context);
        // wait for connect.me user to present the requested proof
        waitFor(proofComplete, "Waiting for proof presentation from Connect.me");
    }
}

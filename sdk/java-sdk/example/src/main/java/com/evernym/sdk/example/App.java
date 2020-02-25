/*
 * COPYRIGHT 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.
 */
package com.evernym.sdk.example;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.exceptions.WalletOpenException;
import com.evernym.verity.sdk.protocols.connecting.Connecting;
import com.evernym.verity.sdk.protocols.issuecredential.IssueCredential;
import com.evernym.verity.sdk.protocols.issuersetup.IssuerSetup;
import com.evernym.verity.sdk.protocols.presentproof.Attribute;
import com.evernym.verity.sdk.protocols.presentproof.PresentProof;
import com.evernym.verity.sdk.protocols.presentproof.Restriction;
import com.evernym.verity.sdk.protocols.presentproof.RestrictionBuilder;
import com.evernym.verity.sdk.protocols.provision.Provision;
import com.evernym.verity.sdk.protocols.questionanswer.CommittedAnswer;
import com.evernym.verity.sdk.protocols.updateendpoint.UpdateEndpoint;
import com.evernym.verity.sdk.protocols.writecreddef.WriteCredentialDefinition;
import com.evernym.verity.sdk.protocols.writeschema.WriteSchema;
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
        Context ctx = ContextBuilder.fromScratch("examplewallet1", "examplewallet1", verityUrl);
        ctx = Provision.v0_6().provisionSdk(ctx);
        return ctx;
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
        UpdateEndpoint.v0_6().update(context); // The SDK lets Verity know what its endpoint is
    }

    void setupIssuer() throws IOException, VerityException {
        IssuerSetup newIssuerSetup = IssuerSetup.v0_6();
        AtomicBoolean setupComplete = new AtomicBoolean(false);
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

        newIssuerSetup.create(context);
        waitFor(setupComplete, "Waiting for setup to complete");
        println("The issuer DID and Verkey must be on the ledger.");
        println(String.format("Please add DID (%s) and Verkey (%s) to ledger.", issuerDID, issuerVerkey));
        waitFor("Press ENTER when DID is on ledger");
    }

    void issuerIdentifier() throws IOException, VerityException {
        IssuerSetup issuerSetup = IssuerSetup.v0_6();
        AtomicBoolean issuerComplete = new AtomicBoolean(false);
        handle(issuerSetup, (String msgName, JSONObject message) -> {
            if("public-identifier".equals(msgName))
            {
                printlnMessage(msgName, message);
                issuerDID = message.getString("did");
                issuerVerkey = message.getString("verKey");
            }
            issuerComplete.set(true);
        });

        issuerSetup.currentPublicIdentifier(context);
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

        issuerIdentifier();

        if (issuerDID == null) {
            setupIssuer();
        }

        printlnObject(context.toJson(), ">>>", "Context Used:");

        Files.write(contextFile.toPath(), context.toJson().toString(2).getBytes());
    }

    private String createConnection() throws IOException, VerityException {
        // Create a new connection
        Connecting connecting = Connecting.v0_6(UUID.randomUUID().toString(), true);

        // Handler for getting invite details (connection awaiting response)
        AtomicBoolean startConnectionComplete = new AtomicBoolean(false);
        AtomicBoolean connectionComplete = new AtomicBoolean(false);
        AtomicReference<String> relDID = new AtomicReference<>();
        handle(connecting, (String msgName, JSONObject message) -> {
            if("CONN_REQUEST_RESP".equals(msgName))
            {
                printlnMessage(msgName, message);
                JSONObject invite = message.getJSONObject("inviteDetail");
                relDID.set(invite.getJSONObject("senderDetail").getString("DID"));
                String inviteDetails = Util.truncateInviteDetails(invite).toString();

                try {
                    QRCode.from(inviteDetails).withSize(500, 500)
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

        connecting.connect(context); // Send the connection create message to Verity
        waitFor(startConnectionComplete, "Waiting to start connection");
        waitFor(connectionComplete, "Waiting for Connect.Me to accept connection");
        return relDID.get();
    }

    private void askQuestion(String forDID) throws IOException, VerityException {
        String questionText = "Hi Alice, how are you today?";
        String questionDetail = " ";
        String[] validResponses = {"Great!", "Not so good."};

        CommittedAnswer committedAnswer = CommittedAnswer.v1_0(
                forDID,
                questionText,
                questionDetail,
                validResponses,
                true);


        AtomicBoolean questionComplete = new AtomicBoolean(false);
        handle(committedAnswer, (String msgName, JSONObject message) -> {
            if("answer".equals(msgName))
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
        String schemaName = "Diploma "+ UUID.randomUUID().toString().substring(0, 8);
        String schemaVersion = "0.1";
        WriteSchema writeSchema = WriteSchema.v0_6(schemaName, schemaVersion, "name", "degree");

        AtomicBoolean schemaComplete = new AtomicBoolean(false);
        AtomicReference<String> schemaIdRef = new AtomicReference<>();
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

        writeSchema.write(context);
        waitFor(schemaComplete, "Waiting to write schema to ledger");

        return schemaIdRef.get();
    }

    private String writeLedgerCredDef(String schemaId) throws IOException, VerityException {
        String credDefName = "Trinity Collage Diplomas";
        String credDefTag = "latest";

        WriteCredentialDefinition def = WriteCredentialDefinition.v0_6(credDefName, schemaId, credDefTag);

        AtomicBoolean defComplete = new AtomicBoolean(false);
        AtomicReference<String> defIdRef = new AtomicReference<>();
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

        def.write(context);
        waitFor(defComplete, "Waiting to write cred def to ledger");
        return defIdRef.get();
    }

    private void issueCredential(String forDID, String defId) throws IOException, VerityException, InterruptedException {
        String credentialName = "Degree";
        Map<String, String> credentialData = new HashMap<>();
        credentialData.put("name", "JoeSmith");
        credentialData.put("degree", "Bachelors");
        IssueCredential issue = IssueCredential.v0_6(forDID, credentialName, credentialData, defId);

        AtomicBoolean offerComplete = new AtomicBoolean(false);
        AtomicBoolean issueComplete = new AtomicBoolean(false);
        handle(issue, (String msgName, JSONObject message) -> {
            if("ask-accept".equals(msgName)) {
                printlnMessage(msgName, message);
                offerComplete.set(true);
            }
            else if ("status-report".equals(msgName)){
                printlnMessage(msgName, message);
                issueComplete.set(true);
            }
            else {
                nonHandled("Message Name is not handled - "+msgName);
            }

        });

        issue.offerCredential(context);
        waitFor(offerComplete, "Wait for Connect.me to accept the Credential Offer");
        issue.issueCredential(context);
        waitFor(offerComplete, "Wait for issuance to complete");

        Thread.sleep(3000); // Give time for Credential to get to mobile device

    }

    private void requestProof(String forDID) throws IOException, VerityException {
        String proofName = UUID.randomUUID().toString();
        Restriction restriction =  RestrictionBuilder
                .blank()
                .issuerDid(issuerDID)
                .build();

        Attribute nameAttr = PresentProof.attribute("name", restriction);
        Attribute degreeAttr = PresentProof.attribute("degree", restriction);

        PresentProof proof = PresentProof.v0_6(forDID, proofName, nameAttr, degreeAttr);

        AtomicBoolean proofComplete = new AtomicBoolean(false);
        handle(proof, (String msgName, JSONObject message) -> {
            if("proof-result".equals(msgName)) {
                printlnMessage(msgName, message);
                proofComplete.set(true);
            }
            else {
                nonHandled("Message Name is not handled - "+msgName);
            }
        });

        proof.request(context);
        waitFor(proofComplete, "Waiting for proof presentation from Connect.me");
    }
}

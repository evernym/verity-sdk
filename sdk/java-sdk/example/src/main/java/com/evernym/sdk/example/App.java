/*
 * COPYRIGHT 2013-2019, ALL RIGHTS RESERVED, EVERNYM INC.
 */
package com.evernym.sdk.example;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.evernym.verity.sdk.handlers.MessageHandler;
import com.evernym.verity.sdk.protocols.*;
import com.evernym.verity.sdk.handlers.Handlers;
import com.evernym.verity.sdk.protocols.connecting.Connecting;
import com.evernym.verity.sdk.protocols.issuecredential.IssueCredential;
import com.evernym.verity.sdk.protocols.issuersetup.IssuerSetup;
import com.evernym.verity.sdk.protocols.presentproof.Attribute;
import com.evernym.verity.sdk.protocols.presentproof.PresentProof;
import com.evernym.verity.sdk.protocols.presentproof.Restriction;
import com.evernym.verity.sdk.protocols.presentproof.RestrictionBuilder;
import com.evernym.verity.sdk.protocols.provision.Provision;
import com.evernym.verity.sdk.protocols.updateendpoint.UpdateEndpoint;
import com.evernym.verity.sdk.protocols.writecreddef.WriteCredentialDefinition;
import com.evernym.verity.sdk.protocols.writeschema.WriteSchema;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.ContextBuilder;
import com.evernym.verity.sdk.utils.Util;

import net.glxn.qrgen.QRCode;
import org.json.JSONArray;
import org.json.JSONObject;

public class App {
    private static Integer port = 4000;
    private static Context context;
    private static Listener listener;
    private static String issuerDID;
    private static Handlers handlers;

    private static ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();

    private static String verityUrl = "http://localhost:9000";

    public static void main( String[] args ) throws Exception {
        try {
            System.setErr(new PrintStream(errBuffer));

            // NOTE: You must provision against Verity using the provision-sdk.py script before running this example.
                // The output of that script should be stored in verityConfig.json

            startListening(); // The example app stands up an endpoint to listen for messages from Verity
            handlers = new Handlers();

            File contextFile = new File("verity-context.json");
            if (contextFile.exists()) {
                context = ContextBuilder.fromJson(
                    new JSONObject(
                        new String(Files.readAllBytes(contextFile.toPath()))
                    )
                ).build();

                IssuerSetup issuerSetup = IssuerSetup.v0_6();
                AtomicBoolean issuerComplete = new AtomicBoolean(false);
                handle(issuerSetup, (String msgName, JSONObject message) -> {
                    if("public-identifier".equals(msgName))
                    {
                        System.out.println(message.toString(2));
                        issuerDID = message.getString("did");
                        issuerComplete.set(true);
                    }
                    else {
                        nonHandled("Message Name is not handled - "+msgName);
                    }
                });

                issuerSetup.currentPublicIdentifier(context);
                waitFor(issuerComplete, "Waiting for current issuer DID");
            }
            else {
                context = ContextBuilder.fromScratch("examplewallet1", "examplewallet1", verityUrl);
                context = Provision.v0_6().provisionSdk(context);
                System.out.println(context.toJson().toString(2));

                context = context.toContextBuilder().endpointUrl("http://127.0.0.1:"+port).build();

                UpdateEndpoint.v0_6().update(context); // The SDK lets Verity know what its endpoint is

                IssuerSetup issuerSetup = IssuerSetup.v0_6();

                AtomicBoolean setupComplete = new AtomicBoolean(false);
                handle(issuerSetup, (String msgName, JSONObject message) -> {
                    if("public-identifier-created".equals(msgName))
                    {
                        System.out.println(message.toString(2));
                        issuerDID = message.getJSONObject("identifier").getString("did");
                        setupComplete.set(true);
                    }
                    else {
                        nonHandled("Message Name is not handled - "+msgName);
                    }
                });

                issuerSetup.create(context);
                waitFor(setupComplete, "Waiting for setup to complete");
                waitFor("Press ENTER when DID is on ledger");

                Files.write(contextFile.toPath(), context.toJson().toString(2).getBytes());
            }

            // Create a new connection
            Connecting connecting = Connecting.v0_6(UUID.randomUUID().toString(), true);

            // Handler for getting invite details (connection awaiting response)
            AtomicBoolean startConnectionComplete = new AtomicBoolean(false);
            AtomicBoolean connectionComplete = new AtomicBoolean(false);
            AtomicReference<String> relDID = new AtomicReference<>();
            handle(connecting, (String msgName, JSONObject message) -> {
                if("CONN_REQUEST_RESP".equals(msgName))
                {
                    System.out.println(message.toString(2));
                    JSONObject invite = message.getJSONObject("inviteDetail");
                    relDID.set(invite.getJSONObject("senderDetail").getString("DID"));
                    String inviteDetails = Util.truncateInviteDetails(invite).toString();

                    try {
                        QRCode.from(inviteDetails).withSize(500, 500)
                                .writeTo(new FileOutputStream(new File("qrcode.png")));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    System.out.print("QR code at: qrcode.png");

                    startConnectionComplete.set(true);
                }
                else if("CONN_REQ_ACCEPTED".equals(msgName)){
                    connectionComplete.set(true);
                }
                else {
                    nonHandled("Message Name is not handled - "+msgName);
                }
            });

            connecting.connect(context); // Send the connection create message to Verity
            waitFor(startConnectionComplete, "Waiting to start connection");
            waitFor(connectionComplete, "Waiting for Connect.Me to accept connection");
            String forDID = relDID.get();

//            String questionText = "Hi Alice, how are you today?";
//            String questionDetail = " ";
//            String[] validResponses = {"Great!", "Not so good."};
//
//            CommittedAnswer committedAnswer = CommittedAnswer.v1_0(
//                    forDID,
//                    questionText,
//                    questionDetail,
//                    validResponses,
//                    true);
//
//
//            AtomicBoolean questionComplete = new AtomicBoolean(false);
//            handle(committedAnswer, (String msgName, JSONObject message) -> {
//                if("answer".equals(msgName))
//                {
//                    questionComplete.set(true);
//                    System.out.println(message.toString(2));
//                }
//                else {
//                    nonHandled("Message Name is not handled - "+msgName);
//                }
//            });
//
//            committedAnswer.ask(context);
//            waitFor(questionComplete, "Waiting for Connect.Me to answer the question");


            String schemaName = "Diploma "+ UUID.randomUUID().toString().substring(0, 8);
            String schemaVersion = "0.1";
            WriteSchema writeSchema = WriteSchema.v0_6(schemaName, schemaVersion, "name", "degree");

            AtomicBoolean schemaComplete = new AtomicBoolean(false);
            AtomicReference<String> schemaIdRef = new AtomicReference<>();
            handle(writeSchema, (String msgName, JSONObject message) -> {
                if("status-report".equals(msgName)) {
                    schemaIdRef.set(message.getString("schemaId"));
                    schemaComplete.set(true);
                    System.out.println(message.toString(2));
                }
                else {
                    nonHandled("Message Name is not handled - "+msgName);
                }
            });

            writeSchema.write(context);
            waitFor(schemaComplete, "Waiting to write schema to ledger");
            String schemaId = schemaIdRef.get();


            String credDefName = "Trinity Collage Diplomas";
            String credDefTag = "latest";

            WriteCredentialDefinition def = WriteCredentialDefinition.v0_6(credDefName, schemaId, credDefTag);

            AtomicBoolean defComplete = new AtomicBoolean(false);
            AtomicReference<String> defIdRef = new AtomicReference<>();
            handle(def, (String msgName, JSONObject message) -> {
                if("status-report".equals(msgName)) {
                    defIdRef.set(message.getString("credDefId"));
                    defComplete.set(true);
                    System.out.println(message.toString(2));
                }
                else {
                    nonHandled("Message Name is not handled - "+msgName);
                }
            });

            def.write(context);
            waitFor(defComplete, "Waiting to write cred def to ledger");
            String defId = defIdRef.get();

            String credentialName = "Degree";
            Map<String, String> credentialData = new HashMap<>();
            credentialData.put("name", "JoeSmith");
            credentialData.put("degree", "Bachelors");
            IssueCredential issue = IssueCredential.v0_6(forDID, credentialName, credentialData, defId);

            AtomicBoolean offerComplete = new AtomicBoolean(false);
            AtomicBoolean issueComplete = new AtomicBoolean(false);
            handle(issue, (String msgName, JSONObject message) -> {
                if("ask-accept".equals(msgName)) {
                    System.out.println(message.toString(2));
                    offerComplete.set(true);
                }
                else if ("status-report".equals(msgName)){
                    System.out.println(message.toString(2));
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
                    System.out.println(message.toString(2));
                    proofComplete.set(true);
                }
                else {
                    nonHandled("Message Name is not handled - "+msgName);
                }
            });

            proof.request(context);
            waitFor(proofComplete, "Waiting for proof presentation from Connect.me");

        } catch(Exception ex) {
            ex.printStackTrace();
        }
        finally {
            listener.stop();
        }
    }

    // Basic http server listening for messages from Verity
    private static void startListening() throws IOException, InterruptedException {
        listener = new Listener(App.port, (String encryptedMessageFromVerity) -> {
            try {
                handlers.handleMessage(context, encryptedMessageFromVerity.getBytes());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
        listener.listen();
        System.out.println("Listening on port " + port);
    }

    private static void writeInviteDetailsFile(JSONObject data) throws IOException {
        Files.write(FileSystems.getDefault().getPath("inviteDetails.json"), data.toString().getBytes());
    }

    private static void handle(MessageFamily messageFamily, MessageHandler.Handler messageHandler) {
        handlers.addHandler(messageFamily, (String msgName, JSONObject message) -> {
            try {
                messageHandler.handle(msgName, message);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private static void nonHandled(String msg) {
        System.err.println(msg);
        System.err.flush();
        listener.stop();
        try { Thread.sleep(250); } catch (InterruptedException ignored) {}
        System.exit(-1);
    }

    private static void waitFor(String waitMsg) {
        ByteArrayOutputStream recordedOut = new ByteArrayOutputStream();
        PrintStream out = System.out;
        System.setOut(new PrintStream(recordedOut));

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            out.println();
            out.print(waitMsg+" ... ");
            out.flush();
            try {br.readLine();} catch (IOException ignored) {}

            out.print("Done\n");
            out.flush();
        }
        finally {
            out.print(new String(recordedOut.toByteArray()));
            System.setOut(out);
        }

    }

    private static void waitFor(AtomicBoolean canContinue, String waitMsg) {
        ByteArrayOutputStream recordedOut = new ByteArrayOutputStream();
        PrintStream out = System.out;
        System.setOut(new PrintStream(recordedOut));

        try {
            String[] spinner = new String[] {"\u0008/", "\u0008-", "\u0008\\", "\u0008|" };
            out.println();
            out.print(waitMsg + " ... ");
            out.print("|");
            int pos = 0;
            while(!canContinue.get()) {
                try { Thread.sleep(450); } catch (InterruptedException ignored) {}
                out.printf("%s", spinner[pos % spinner.length]);
                out.flush();
                pos++;
            }
            out.print("\u0008");
            out.print("Done\n");
            out.flush();
        }
        finally {
            out.print(new String(recordedOut.toByteArray()));
            System.setOut(out);
        }
    }

    // Builds JSON object for proof requested attributes
    private static JSONObject getProofAttr(String name, String issuerDid) {
        JSONObject proofAttr = new JSONObject();
        proofAttr.put("name", name);
        JSONArray restrictions = new JSONArray();
        JSONObject restriction = new JSONObject();
        restriction.put("issuer_did", issuerDid);
        restrictions.put(restriction);
        proofAttr.put("restrictions", restrictions);
        return proofAttr;
    }
}

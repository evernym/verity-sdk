/*
 * COPYRIGHT 2013-2019, ALL RIGHTS RESERVED, EVERNYM INC.
 */
package com.evernym.sdk.example;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import com.evernym.verity.sdk.protocols.Connection;
import com.evernym.verity.sdk.protocols.CredDef;
import com.evernym.verity.sdk.protocols.Credential;
import com.evernym.verity.sdk.handlers.Handlers;
import com.evernym.verity.sdk.protocols.ProofRequest;
import com.evernym.verity.sdk.protocols.Question;
import com.evernym.verity.sdk.protocols.Schema;
import com.evernym.verity.sdk.utils.Context;

import org.json.JSONArray;
import org.json.JSONObject;

public class App {
    static Integer port = 4000;
    static String connectionId;
    static Context context;
    static String credDefId;
    static Credential credential;
    static Handlers handlers;

    public static void main( String[] args ) {
        try {
            // NOTE: You must provision against Verity using the provision-sdk.py script before running this example. 
                // The output of that script should be stored in verityConfig.json

            startListening(); // The example app stands up an endpoint to listen for messages from Verity
            context = new Context(readConfigFile("verityConfig.json"));
            context.sendUpdateWebhookMessage(context); // The SDK lets Verity know what its endpoint is

            // Create a new connection (initiates the daisy-chained flow of Connection, Question, Credential, Proof)
            Connection connection = new Connection("my institution id", true); // Note that Connection also supports a phone number in the constructor. See javadocs.
            connection.create(context); // Send the connection create message to Verity

            handlers = new Handlers();

            // Handler for getting invite details (connection awaiting response)
            handlers.addHandler(Connection.STATUS_MESSAGE_TYPE, Connection.AWAITING_RESPONSE_STATUS, (JSONObject message) -> {
                try {
                    JSONObject inviteDetails = new JSONObject(message.getString("content"));
                    System.out.print("Invite Details: ");
                    System.out.println(inviteDetails.toString());
                    writeInviteDetailsFile(inviteDetails); // For integration tests
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });

            // Handler for Connection Accepted message
            handlers.addHandler(Connection.STATUS_MESSAGE_TYPE, Connection.ACCEPTED_BY_USER_STATUS, (JSONObject message) -> {
                try {
                    System.out.println("Connection Accepted!!!");

                    // Now that the connection has been accepted, let's send Alice a Question.
                    App.connectionId = message.getString("content");
                    String notificationTitle = "Challenge Question";
                    String questionText = "Hi Alice, how are you today?";
                    String questionDetail = " ";
                    String[] validResponses = {"Great!", "Not so good"};
                    Question provableQuestion = new Question(App.connectionId, notificationTitle, questionText, questionDetail, validResponses);
                    provableQuestion.ask(context);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });

            // Handler for Question Answered message
            handlers.addHandler(Question.STATUS_MESSAGE_TYPE, Question.QUESTION_ANSWERED_STATUS, (JSONObject message) -> {
                try {
                    System.out.println("Question Answered: \"" + message.getString("content") + "\"");

                    // Write a Schema and Cred Def to the ledger in preparation for issuing
                    // This step will likely be done manually by the institution, and not on a regular basis
                    String schemaName = "My test schema";
                    String schemaVersion = getRandomInt(0, 1000).toString() + "." + getRandomInt(0, 1000).toString() + "." + getRandomInt(0, 1000).toString();
                    Schema schema = new Schema(schemaName, schemaVersion, "name", "degree");
                    schema.write(context);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });

            // Handler for Schema write successful status
            handlers.addHandler(Schema.STATUS_MESSAGE_TYPE, Schema.WRITE_SUCCESSFUL_STATUS, (JSONObject message) -> {
                try {
                    String credDefName = "My test credential definition";
                    String schemaId = message.getString("content");
                    String credDefTag = "latest";
                    JSONObject revocationDetails = new JSONObject();
                    revocationDetails.put("support_revocation", false);
                    CredDef credDef = new CredDef(credDefName, schemaId, credDefTag, revocationDetails);
                    credDef.write(context);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });

            // Handler for Cred Def write successful status
            handlers.addHandler(CredDef.STATUS_MESSAGE_TYPE, CredDef.WRITE_SUCCESSFUL_STATUS, (JSONObject message) -> {
                try {
                    // Issue a credential to Alice
                    credDefId = message.getString("content");
                    JSONObject credentialValues = new JSONObject();
                    credentialValues.put("name", "Joe Smith");
                    credentialValues.put("degree", "Bachelors");
                    credential = new Credential(connectionId, credDefId, credentialValues, 0);
                    credential.send(context);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });

            // Handler for Credential Offer Accepted message
            handlers.addHandler(Credential.STATUS_MESSAGE_TYPE, Credential.OFFER_ACCEPTED_BY_USER_STATUS, (JSONObject message) -> {
                try {
                    System.out.println("User accepted the credential offer. Verity should now be sending the Credential");
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });

            // Handler for Credential Accepted message
            handlers.addHandler(Credential.STATUS_MESSAGE_TYPE, Credential.CREDENTIAL_ACCEPTED_BY_USER_STATUS, (JSONObject message) -> {
                try {
                    System.out.println("User accepted the credential");

                    // Ask Alice to prove she owns the credential we sent her
                    String proofRequestName = "Who are you?";
                    JSONArray proofAttrs = new JSONArray();
                    proofAttrs.put(getProofAttr("name", credDefId.split(":")[0]));
                    JSONObject revocationInterval = new JSONObject();
                    ProofRequest proofRequest = new ProofRequest(connectionId, proofRequestName, proofAttrs, revocationInterval);
                    proofRequest.send(context);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });

            // Handler for Proof Received message
            handlers.addHandler(ProofRequest.STATUS_MESSAGE_TYPE, ProofRequest.PROOF_RECEIVED_STATUS, (JSONObject message) -> {
                System.out.println("Proof Accepted!");
                System.out.println(message.toString());
                System.exit(0);
            });

            // Handle all messages not handled by other handlers
            handlers.addDefaultHandler((JSONObject message) -> {
                System.out.println("New message from verity: " + message.toString());
            });

            // Handle all problem report messages not handled directly by other handlers
            handlers.addProblemReportHandler((JSONObject message) -> {
                System.out.println("New problem report from verity: " + message.getJSONObject("comment").getString("en"));
                System.exit(1);
            });
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    // Basic http server listening for messages from Verity
    private static void startListening() throws IOException, InterruptedException {
        Listener listener = new Listener(App.port, (String encryptedMessageFromVerity) -> {
            try {
                handlers.handleMessage(context, encryptedMessageFromVerity.getBytes());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
        listener.listen();
        System.out.println("Listening on port " + port);
    }

    private static String readConfigFile(String configPath) throws IOException {
        return new String(Files.readAllBytes(FileSystems.getDefault().getPath(configPath)));
    }

    private static void writeInviteDetailsFile(JSONObject data) throws IOException {
        Files.write(FileSystems.getDefault().getPath("inviteDetails.json"), data.toString().getBytes());
    }

    private static Integer getRandomInt(int min, int max) {
        return new Integer((int)(Math.random() * ((max - min) + 1)) + min);
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

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
            // NOTE: Wallet must already exist. You can create it with the tools/provisioner/provision-sdk.py script
            startListening();
            context = new Context(readConfigFile());
            context.sendUpdateWebhookMessage(context);

            handlers = new Handlers();

            // Handle all messages not handled by other handlers
            handlers.addDefaultHandler((JSONObject message) -> {
                System.out.println("New message from verity: " + message.toString());
            });

            // Handle all problem report messages not handled directly by other handlers
            handlers.addProblemReportHandler((JSONObject message) -> {
                System.out.println("New problem report from verity: " + message.getJSONObject("comment").getString("en"));
            });

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
                    String schemaName = "My test schema";
                    String schemaVersion = getRandomInt(0, 100).toString() + "." + getRandomInt(0, 100).toString() + "." + getRandomInt(0, 100).toString();
                    Schema schema = new Schema(schemaName, schemaVersion, "name", "degree");
                    schema.write(context);

                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });

            // Handler for Schema write successful status
            handlers.addHandler(Schema.STATUS_MESSAGE_TYPE, Schema.WRITE_SUCCESSFUL_STATUS, (JSONObject message) -> {
                try {
                    String schemaId = message.getString("content");
                    CredDef credDef = new CredDef(schemaId);
                    credDef.write(context);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });

            // Handler for Cred Def write successful status
            handlers.addHandler(CredDef.STATUS_MESSAGE_TYPE, CredDef.WRITE_SUCCESSFUL_STATUS, (JSONObject message) -> {
                try {
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

                    String proofRequestName = "Who are you?";
                    JSONArray proofAttrs = new JSONArray();
                    proofAttrs.put(getProofAttr("name", credDefId.split(":")[0]));
                    ProofRequest proofRequest = new ProofRequest(proofRequestName, proofAttrs, connectionId);
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

            // Create a new connection (initiates the entire flow)
            Connection connection = new Connection("my institution id");
            connection.create(context);
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

    private static String readConfigFile() throws IOException {
        return new String(Files.readAllBytes(FileSystems.getDefault().getPath("verityConfig.json")));
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

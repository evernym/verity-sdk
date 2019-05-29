package com.evernym.sdk.example;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.UUID;

import com.evernym.verity.sdk.protocols.Connection;
import com.evernym.verity.sdk.protocols.CredDef;
import com.evernym.verity.sdk.protocols.Credential;
import com.evernym.verity.sdk.handlers.Handlers;
import com.evernym.verity.sdk.protocols.ProofRequest;
import com.evernym.verity.sdk.protocols.Question;
import com.evernym.verity.sdk.protocols.Schema;
import com.evernym.verity.sdk.utils.VerityConfig;

import org.json.JSONArray;
import org.json.JSONObject;

public class App {
    static Integer port = 4000;
    static String connectionId;
    static VerityConfig verityConfig;
    static String credDefId;
    static Credential credential;

    public static void main( String[] args ) {
        try {
            // NOTE: Wallet must already exist. You can create it with the tools/provisioner/provision-sdk.py script
            startListening();
            verityConfig = new VerityConfig(readConfigFile());
            verityConfig.sendUpdateWebhookMessage(verityConfig);

            Handlers.addDefaultHandler((JSONObject message) -> {
                System.out.println("New message from verity: " + message.toString());
            });
            Handlers.addProblemReportHandler((JSONObject message) -> {
                System.out.println("New problem report from verity: " + message.getJSONObject("comment").getString("en"));
            });
            Handlers.addHandler(Connection.STATUS_MESSAGE_TYPE, Connection.AWAITING_RESPONSE_STATUS, (JSONObject message) -> {
                JSONObject inviteDetails = new JSONObject(message.getString("content"));
                System.out.print("Invite Details: ");
                System.out.println(inviteDetails.toString());
            });
            Handlers.addHandler(Connection.STATUS_MESSAGE_TYPE, Connection.ACCEPTED_BY_USER_STATUS, (JSONObject message) -> {
                try {
                    System.out.println("Connection Accepted!!!");
                    App.connectionId = message.getString("content");
                    String notificationTitle = "Challenge Question";
                    String questionText = "Hi Alice, how are you today?";
                    String questionDetail = " ";
                    String[] validResponses = {"Great!", "Not so good"};
                    Question provableQuestion = new Question(App.connectionId, notificationTitle, questionText, questionDetail, validResponses);
                    provableQuestion.ask(verityConfig);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });
            Handlers.addHandler(Question.STATUS_MESSAGE_TYPE, Question.QUESTION_ANSWERED_STATUS, (JSONObject message) -> {
                try {
                    System.out.println("Question Answered: \"" + message.getString("content") + "\"");
                    String schemaName = "My test schema";
                    String schemaVersion = getRandomInt(0, 100).toString() + "." + getRandomInt(0, 100).toString() + "." + getRandomInt(0, 100).toString();
                    Schema schema = new Schema(schemaName, schemaVersion, "name", "degree");
                    schema.write(verityConfig);

                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });
            Handlers.addHandler(Schema.STATUS_MESSAGE_TYPE, Schema.WRITE_SUCCESSFUL_STATUS, (JSONObject message) -> {
                try {
                    String schemaId = message.getString("content");
                    CredDef credDef = new CredDef(schemaId);
                    credDef.write(verityConfig);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });
            Handlers.addHandler(CredDef.STATUS_MESSAGE_TYPE, CredDef.WRITE_SUCCESSFUL_STATUS, (JSONObject message) -> {
                try {
                    credDefId = message.getString("content");
                    JSONObject credentialValues = new JSONObject();
                    credentialValues.put("name", "Joe Smith");
                    credentialValues.put("degree", "Bachelors");
                    credential = new Credential(connectionId, credDefId, credentialValues, 0);
                    credential.send(verityConfig);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });
            Handlers.addHandler(Credential.STATUS_MESSAGE_TYPE, Credential.OFFER_ACCEPTED_BY_USER_STATUS, (JSONObject message) -> {
                try {
                    System.out.println("User accepted the credential offer. Verity should now be sending the Credential");
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });
            Handlers.addHandler(Credential.STATUS_MESSAGE_TYPE, Credential.CREDENTIAL_ACCEPTED_BY_USER_STATUS, (JSONObject message) -> {
                try {
                    System.out.println("User accepted the credential");

                    String proofRequestName = "Who are you?";
                    JSONArray proofAttrs = new JSONArray();
                    proofAttrs.put(getProofAttr("name", "V4SGRU86Z58d6TV7PBUe6f"));
                    ProofRequest proofRequest = new ProofRequest(proofRequestName, proofAttrs, connectionId);
                    proofRequest.send(verityConfig);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });
            Handlers.addHandler(ProofRequest.STATUS_MESSAGE_TYPE, ProofRequest.PROOF_RECEIVED_STATUS, (JSONObject message) -> {
                System.out.println("Proof Accepted!");
                System.out.println(message.toString());
            });

            Connection connection = new Connection("my institution id");
            connection.create(verityConfig);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void startListening() throws IOException, InterruptedException {
        Listener listener = new Listener(App.port, (String encryptedMessageFromVerity) -> {
            try {
                Handlers.handleMessage(verityConfig, encryptedMessageFromVerity.getBytes());
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

    private static Integer getRandomInt(int min, int max) {
        return new Integer((int)(Math.random() * ((max - min) + 1)) + min);
    }

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

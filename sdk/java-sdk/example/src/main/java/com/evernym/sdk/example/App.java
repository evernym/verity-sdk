package com.evernym.sdk.example;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import com.evernym.verity.sdk.protocols.Connection;
import com.evernym.verity.sdk.protocols.Protocols;
import com.evernym.verity.sdk.protocols.Question;
import com.evernym.verity.sdk.utils.VerityConfig;

import org.json.JSONObject;

public class App {
    static Integer port = 4000;
    static String connectionId;
    static VerityConfig verityConfig;

    public static void main( String[] args ) {
        try {
            // NOTE: Wallet must already exist. You can create it with the tools/provisioner/provision-sdk.py script
            startListening(); 
            verityConfig = new VerityConfig(readConfigFile());
            verityConfig.sendUpdateWebhookMessage(verityConfig);

            Protocols.addHandler(Connection.STATUS_MESSAGE_TYPE, Connection.AWAITING_RESPONSE_STATUS, (JSONObject message) -> {
                JSONObject inviteDetails = new JSONObject(message.getString("message"));
                System.out.println(inviteDetails.toString());
            });
            Protocols.addHandler(Connection.STATUS_MESSAGE_TYPE, Connection.ACCEPTED_BY_USER_STATUS, (JSONObject message) -> {
                try {
                    System.out.println("Connection Accepted!!!");
                    App.connectionId = message.getString("message");
                    String notificationTitle = "Challenge Question";
                    String questionText = "Hi Alice";
                    String questionDetail = "How are you today";
                    String[] validResponses = {"Great!", "Not so good"};
                    Question provableQuestion = new Question(App.connectionId, notificationTitle, questionText, questionDetail, validResponses);
                    provableQuestion.sendMessage(verityConfig);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
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
                Protocols.handleMessage(verityConfig, encryptedMessageFromVerity.getBytes());
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
}

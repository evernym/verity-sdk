package com.evernym.sdk.example;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.protocols.Connection;
import com.evernym.verity.sdk.protocols.ProvableQuestion;
import com.evernym.verity.sdk.utils.VerityConfig;
import com.evernym.verity.sdk.utils.MessagePackaging;

import org.hyperledger.indy.sdk.IndyException;
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

            Connection connection = new Connection("my institution id");
            connection.create(verityConfig);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void handleMessage(JSONObject message) throws IOException, InterruptedException, ExecutionException, IndyException {
        if(message.getString("@type").equals(Connection.STATUS_MESSAGE_TYPE) && message.getInt("status") == Connection.AWAITING_RESPONSE_STATUS) {
            JSONObject inviteDetails = new JSONObject(message.getString("message"));
            System.out.println(inviteDetails.toString());
        }
        else if(message.getString("@type").equals(Connection.STATUS_MESSAGE_TYPE) && message.getInt("status") == Connection.ACCEPTED_BY_USER_STATUS) {
            System.out.println("Connection Accepted!!!");
            App.connectionId = message.getString("message");
            String questionText = "Hi Alice";
            String questionDetail = "How are you today";
            String[] validResponses = {"Great!", "Not so good"};
            ProvableQuestion provableQuestion = new ProvableQuestion(App.connectionId, questionText, questionDetail, validResponses);
            provableQuestion.sendMessage(verityConfig);
        }
    }

    private static void startListening() throws IOException, InterruptedException {
        Listener listener = new Listener(App.port, (String encryptedMessageFromVerity) -> {
            try {
                JSONObject message = MessagePackaging.unpackMessageFromVerity(verityConfig, encryptedMessageFromVerity.getBytes());
                System.out.println("New message from verity: " + message.toString());
                handleMessage(message);
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

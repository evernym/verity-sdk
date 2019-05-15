package com.evernym.sdk.example;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import com.evernym.verity.sdk.protocols.Connection;
import com.evernym.verity.sdk.protocols.ProvableQuestion;
import com.evernym.verity.sdk.utils.VerityConfig;
import com.evernym.verity.sdk.utils.MessagePackaging;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

public class App {
    static String agencyUrl = "http://localhost:3000";
    static Integer port = 4000;
    static String connectionId;

    public static void main( String[] args ) {
        try {
             // NOTE: Wallet must already exist. You can create it with the tools/provisioner/provision-sdk.py script
            String config = new String(Files.readAllBytes(FileSystems.getDefault().getPath("verityConfig.json")));
            VerityConfig verityConfig = new VerityConfig(config);
            verityConfig.sendUpdateWebhookMessage(verityConfig);

            String sourceId = "my institution id";
            String phoneNumber = "123-456-7891";
            Connection connection = new Connection(sourceId, phoneNumber);
            connection.sendMessage(verityConfig);

            Listener listener = new Listener(App.port, 10, (String encryptedMessageFromAgency) -> {
                try {
                    String messageFromAgency = MessagePackaging.unpackMessageFromAgency(verityConfig, encryptedMessageFromAgency.getBytes());
                    System.out.println("New message from agency: " + messageFromAgency);
                    JSONObject message = new JSONObject(messageFromAgency);
                    if(message.getString("@type") == Connection.getType() && Integer.parseInt(message.getString("status")) == 1) { // FIXME: Magic number should live somewhere. Add status consts to Connection class.
                        App.connectionId = message.getString("message");
                    }
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });
            listener.listen();
            System.out.println("Listening on port " + port);

            while(App.connectionId == null);

            String questionText = "Hi Alice";
            String questionDetail = "How are you today";
            String[] validResponses = {"Great!", "Not so good"};
            ProvableQuestion provableQuestion = new ProvableQuestion(App.connectionId, questionText, questionDetail, validResponses);
            provableQuestion.sendMessage(verityConfig);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}

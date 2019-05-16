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
    static Integer port = 4000;
    static String connectionId;

    public static void main( String[] args ) {
        try {
             // NOTE: Wallet must already exist. You can create it with the tools/provisioner/provision-sdk.py script
            String config = new String(Files.readAllBytes(FileSystems.getDefault().getPath("verityConfig.json")));
            VerityConfig verityConfig = new VerityConfig(config);
            verityConfig.sendUpdateWebhookMessage(verityConfig);

            Listener listener = new Listener(App.port, 10, (String encryptedMessageFromAgency) -> {
                try {
                    System.out.println("Unpacking new message from agency");
                    String messageFromAgency = MessagePackaging.unpackMessageFromAgency(verityConfig, encryptedMessageFromAgency.getBytes());
                    System.out.println("New message from agency: " + messageFromAgency);
                    JSONObject message = new JSONObject(messageFromAgency);
                    if(message.getString("@type").equals("vs.service/connection/0.1/status") && message.getInt("status") == 0) { // FIXME: Magic number should live somewhere. Add status consts to Connection class.
                        JSONObject inviteDetails = new JSONObject(message.getString("message"));
                        System.out.println(inviteDetails.toString());
                    }
                    else if(message.getString("@type").equals("vs.service/connection/0.1/status") && message.getInt("status") == 1) { // FIXME: Magic number should live somewhere. Add status consts to Connection class.
                        System.out.println("Connection Accepted!!!");
                        App.connectionId = message.getString("message");
                    }

                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });
            listener.listen();
            System.out.println("Listening on port " + port);

            String sourceId = "my institution id";
            //String phoneNumber = "8015551234";
            Connection connection = new Connection(sourceId);
            connection.sendMessage(verityConfig);

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

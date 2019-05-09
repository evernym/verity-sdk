package com.evernym.verity.sdk;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.wallet.*;
import org.json.JSONObject;

public class VeritySdk {
    String agencyUrl;

    public VeritySdk(String agencyUrl) {
        this.agencyUrl = agencyUrl;
    }

    public String handleInboundMessage(String message) {
        System.out.println("Got message: " + message);
        return message;
    }

    public void newConnection() throws IOException {
        JSONObject json = new JSONObject().put("key", "value");
        sendToAgency(this.agencyUrl, json);
    }

    public void provision() throws IndyException, Exception {
        String walletConfig = new JSONObject().put("id", "java_wallet").toString();
        String walletCredentials = new JSONObject().put("key", "12345").toString();
        Wallet.createWallet(walletConfig, walletCredentials).get();
        Wallet myWallet = Wallet.openWallet(walletConfig, walletCredentials).get();
        myWallet.closeWallet().get();
        Wallet.deleteWallet(walletConfig, walletCredentials).get();
    }

    private void sendToAgency(String url, JSONObject json) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response;
        try {
            HttpPost request = new HttpPost(url);
            StringEntity params = new StringEntity(json.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            response = httpClient.execute(request);
            // handle response here...
            if (response != null) {
                InputStream in = response.getEntity().getContent(); //Get the data in the entity
            }
        } catch (Exception ex) {
            // handle exception here
        } finally {
            httpClient.close(); // TODO: How to handle this IOException
        }
    }
}

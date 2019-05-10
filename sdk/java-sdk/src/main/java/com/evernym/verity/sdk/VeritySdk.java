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
}

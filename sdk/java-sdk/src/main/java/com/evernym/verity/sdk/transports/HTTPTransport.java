package com.evernym.verity.sdk.transports;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class HTTPTransport extends Transport {
    CloseableHttpClient httpClient;

    public HTTPTransport() {
        this.httpClient = HttpClientBuilder.create().build();
    }

    public void sendMessage(String url, byte[] message) throws IOException {
        HttpPost request = new HttpPost(url);
        request.setEntity(new ByteArrayEntity(message));
        request.setHeader("Content-Type", "application/octet-stream");
        HttpResponse response = httpClient.execute(request);
        Integer statusCode = response.getStatusLine().getStatusCode();
        if(statusCode > 399) {
            System.out.println("statusCode: " + statusCode);
            throw new IOException("Request failed!");
        }
    }
}
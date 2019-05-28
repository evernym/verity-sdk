package com.evernym.verity.sdk.transports;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Support for sending messages using the HTTP transport
 */
public class HTTPTransport extends Transport {
    CloseableHttpClient httpClient;

    public HTTPTransport() {
        this.httpClient = HttpClientBuilder.create().build();
    }

    /**
     * Send an encrypted agent message to a specified endpoint
     * @param url the url where the message will be POSTed to
     * @param message the encrypted agent message
     * @throws IOException when the HTTP library fails to post to the url
     */
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
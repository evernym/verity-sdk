package com.evernym.verity.sdk.transports;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Support for sending messages using the HTTP transport
 */
public class HTTPTransport extends Transport {
    private CloseableHttpClient httpClient;

    public HTTPTransport() {
        this.httpClient = HttpClientBuilder.create().build();
    }

    private CloseableHttpClient client() {
        return httpClient;
    }

    private HttpPost buildRequest(String url, byte[] message) {
        HttpPost request = new HttpPost(url);
        request.setEntity(new ByteArrayEntity(message));
        request.setHeader("Content-Type", "application/octet-stream");
        return request;
    }

    private HttpResponse transportMessage(HttpPost request) throws IOException {
        HttpResponse response = client().execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode > 399) {
            throw new IOException("Request failed! - " + statusCode + " - " + EntityUtils.toString(response.getEntity()));
        }
        else {
            return response;
        }
    }

    private String msgEndpointUrl(String verityUrl) {
        return String.format("%s/agency/msg", verityUrl);
    }

    /**
     * Send an encrypted agent message to a specified endpoint
     * @param verityUrl the url where the message will be POSTed to
     * @param message the encrypted agent message
     * @throws IOException when the HTTP library fails to post to the url
     */
    public void sendMessage(String verityUrl, byte[] message) throws IOException {
        HttpPost request = buildRequest(msgEndpointUrl(verityUrl), message);

        transportMessage(request);
    }
    /**
     * Send an encrypted agent message to a specified endpoint
     * @param verityUrl the url where the message will be POSTed to
     * @param message the encrypted agent message
     * @throws IOException when the HTTP library fails to post to the url
     */
    public byte[] sendSyncMessage(String verityUrl, byte[] message) throws IOException {
        HttpPost request = buildRequest(msgEndpointUrl(verityUrl), message);

        HttpResponse resp = transportMessage(request);

        return EntityUtils.toByteArray(resp.getEntity());
    }
}
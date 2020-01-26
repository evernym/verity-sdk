package com.evernym.verity.sdk.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

class VerityUtil {
    private VerityUtil(){}

    static Did retrieveVerityPublicDid(HttpGet request, HttpClient httpClient) throws IOException {
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode > 399) {
            throw new IOException("Request failed! - " + statusCode + " - " + EntityUtils.toString(response.getEntity()));
        }
        else {
            String msg = EntityUtils.toString(response.getEntity());
            try {
                JSONObject msgObj = new JSONObject(msg);
                String did = msgObj.getString("DID");
                String verkey = msgObj.getString("verKey");
                return new Did(did, verkey);
            }
            catch (JSONException e){
                throw new IOException("Invalid and unexpected data from Verity -- response -- " + msg);
            }
        }
    }

    static Did retrieveVerityPublicDid(String verityUrl) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();

        String fullUrl = verityUrl+"/agency";
        HttpGet request = new HttpGet(fullUrl);

        return retrieveVerityPublicDid(request, httpClient);
    }
}

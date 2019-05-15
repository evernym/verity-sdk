package com.evernym.verity.sdk.protocols;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.utils.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hyperledger.indy.sdk.IndyException;

public abstract class Protocol {
    protected String type;
    protected String id;
    CloseableHttpClient httpClient;

    public Protocol() {
        this.id = UUID.randomUUID().toString();
        this.httpClient = HttpClientBuilder.create().build();
    }
    
    /**
     * Packs the connection message for the agency
     * @param verityConfig an instance of VerityConfig that has been initialized with your wallet and key details
     * @return Encrypted connection message ready to be sent to the agency
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IndyException
     */
    public byte[] getMessage(VerityConfig verityConfig) throws InterruptedException, ExecutionException, IndyException {
        return MessagePackaging.packMessageForAgency(verityConfig, toString());
    }

    public void sendMessage(VerityConfig verityConfig) throws IOException, InterruptedException, ExecutionException, IndyException {
        HttpPost request = new HttpPost(verityConfig.getAgencyUrl());
        request.setEntity(new ByteArrayEntity(getMessage(verityConfig)));
        HttpResponse response = httpClient.execute(request);
        
        if (response != null) {
            InputStream in = response.getEntity().getContent(); //Get the data in the entity
            // TODO: Check the status code, etc.
        }
    }

    public abstract String toString();
}
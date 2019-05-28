package com.evernym.verity.sdk.protocols;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.transports.HTTPTransport;
import com.evernym.verity.sdk.transports.Transport;
import com.evernym.verity.sdk.utils.*;

import org.hyperledger.indy.sdk.IndyException;
import org.json.JSONObject;

/**
 * The base class for all protocols
 */
public abstract class Protocol {
    protected String id;

    public Protocol() {
        this.id = UUID.randomUUID().toString();
    }
    
    /**
     * Packs the connection message for the verity
     * @param verityConfig an instance of VerityConfig that has been initialized with your wallet and key details
     * @return Encrypted connection message ready to be sent to the verity
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public byte[] getMessage(VerityConfig verityConfig) throws InterruptedException, ExecutionException, IndyException, MethodNotSupportedException {
        return MessagePackaging.packMessageForVerity(verityConfig, toString());
    }

    /**
     * Packs the connection message for the verity
     * @param verityConfig an instance of VerityConfig that has been initialized with your wallet and key details
     * @param message the message to pack for Verity
     * @return Encrypted connection message ready to be sent to the verity
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public byte[] getMessage(VerityConfig verityConfig, String message) throws InterruptedException, ExecutionException, IndyException {
        return MessagePackaging.packMessageForVerity(verityConfig, message);
    }

    /**
     * Encrypts and sends the default message to Verity
     * @param verityConfig an instance of VerityConfig configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     * @throws MethodNotSupportedException when this method has been disabled by a child class
     */
    public void sendMessage(VerityConfig verityConfig) throws IOException, InterruptedException, ExecutionException, IndyException, MethodNotSupportedException {
        // Later we can switch on verityConfig.getVerityProtocol
        Transport transport = new HTTPTransport();
        transport.sendMessage(verityConfig.getVerityUrl(), getMessage(verityConfig));
    }

    /**
     * Encrypts and sends a specified message to Verity
     * @param verityConfig an instance of VerityConfig configured with the results of the provision_sdk.py script
     * @param message the message to send to Verity
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public void sendMessage(VerityConfig verityConfig, String message) throws IOException, InterruptedException, ExecutionException, IndyException {
        // Later we can switch on verityConfig.getVerityProtocol
        Transport transport = new HTTPTransport();
        transport.sendMessage(verityConfig.getVerityUrl(), getMessage(verityConfig, message));
    }

    public abstract String toString();
}
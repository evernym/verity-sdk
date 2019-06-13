package com.evernym.verity.sdk.protocols;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.transports.HTTPTransport;
import com.evernym.verity.sdk.transports.Transport;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.MessagePackaging;

import org.hyperledger.indy.sdk.IndyException;

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
     * @param context an instance of Context that has been initialized with your wallet and key details
     * @return Encrypted connection message ready to be sent to the verity
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public byte[] getMessage(Context context) throws InterruptedException, ExecutionException, IndyException {
        return MessagePackaging.packMessageForVerity(context, toString());
    }

    /**
     * Encrypts and sends the default message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public void sendMessage(Context context) throws IOException, InterruptedException, ExecutionException, IndyException {
        // Later we can switch on context.getVerityProtocol
        Transport transport = new HTTPTransport();
        transport.sendMessage(context.getVerityUrl(), getMessage(context));
    }

    /**
     * Encrypts and sends a specified message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @param message the message to send to Verity
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public void sendMessage(Context context, String message) throws IOException, InterruptedException, ExecutionException, IndyException {
        // Later we can switch on context.getVerityProtocol
        Transport transport = new HTTPTransport();
        transport.sendMessage(context.getVerityUrl(), MessagePackaging.packMessageForVerity(context, message));
    }

    public abstract String toString();
}
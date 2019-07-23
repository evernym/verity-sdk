package com.evernym.verity.sdk.protocols;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.transports.HTTPTransport;
import com.evernym.verity.sdk.transports.Transport;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;

import org.hyperledger.indy.sdk.IndyException;
import org.json.JSONObject;

/**
 * The base class for all protocols
 */
public abstract class Protocol {
    private boolean sendDisabled = false;

    JSONObject messages;

    @SuppressWarnings("WeakerAccess")
    public Protocol() {
        messages = new JSONObject();
    }
    
    /**
     * Packs the connection message for the verity
     * @param context an instance of Context that has been initialized with your wallet and key details
     * @return Encrypted connection message ready to be sent to the verity
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    @SuppressWarnings("WeakerAccess")
    public static byte[] getMessage(Context context, JSONObject message) throws InterruptedException, ExecutionException, IndyException {
        return Util.packMessageForVerity(context, message);
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
    byte[] send(Context context, JSONObject message) throws IOException, InterruptedException, ExecutionException, IndyException {
        byte[] messageToSend = Util.packMessageForVerity(context, message);
        if(! sendDisabled) {
            Transport transport = new HTTPTransport();
            transport.sendMessage(context.getVerityUrl(), messageToSend);
        }
        return messageToSend;
    }

    void disableHTTPSend() {
        sendDisabled = true;
    }

    static String getNewId() {
        return UUID.randomUUID().toString();
    }

    protected abstract void defineMessages();
}
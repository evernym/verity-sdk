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
    private String MSG_FAMILY;
    private String MSG_FAMILY_VERSION;
    static private String MESSAGE_TYPE_DID = "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw";
    private boolean sendDisabled = false;

    JSONObject messages;

    @SuppressWarnings("WeakerAccess")
    public Protocol(String msgFamily, String msgFamilyVersion) {
        this.MSG_FAMILY = msgFamily;
        this.MSG_FAMILY_VERSION = msgFamilyVersion;
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

    static String getMessageTypeComplete(String msgFamily, String msgFamilyVersion, String msgName) {
        return Protocol.MESSAGE_TYPE_DID + ";spec/" + msgFamily + "/" + msgFamilyVersion + "/" + msgName;
    }

    String getMessageType(String msgName) {
        return Protocol.getMessageTypeComplete(this.MSG_FAMILY, this.MSG_FAMILY_VERSION, msgName);
    }

    protected String getProblemReportMessageType() {
        return getMessageType("problem-report");
    }

    protected String getStatusMessageType() {
        return getMessageType("status");
    }

    protected abstract void defineMessages();
}
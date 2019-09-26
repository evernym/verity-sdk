package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.transports.HTTPTransport;
import com.evernym.verity.sdk.transports.Transport;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

/**
 * The base class for all protocols
 */
public abstract class Protocol {
    private boolean sendDisabled = false;

    // Currently a static threadId but that don't allow for re-entrant use-cases
    private UUID threadId = UUID.randomUUID();

    protected JSONObject addThread(JSONObject msg) {
        JSONObject threadBlock = new JSONObject();
        threadBlock.put("thid", threadId.toString());
        msg.put("~thread", threadBlock);
        return msg;
    }

    JSONObject messages;

    @SuppressWarnings("WeakerAccess")
    public Protocol() {
        messages = new JSONObject();
    }
    
    /**
     * Packs the connection message for the verity
     * @param context an instance of Context that has been initialized with your wallet and key details
     * @return Encrypted connection message ready to be sent to the verity
     * @throws WalletException when there are issues with encryption and decryption
     * @throws UndefinedContextException when the context don't have enough information for this operation
     */
    @SuppressWarnings("WeakerAccess")
    public static byte[] getMessage(Context context, JSONObject message) throws UndefinedContextException, WalletException {
        return Util.packMessageForVerity(context, message);
    }

    /**
     * Encrypts and sends a specified message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @param message the message to send to Verity
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws WalletException when there are issues with encryption and decryption
     * @throws UndefinedContextException when the context don't have enough information for this operation
     */
    byte[] send(Context context, JSONObject message) throws IOException, UndefinedContextException, WalletException {
        byte[] messageToSend = Util.packMessageForVerity(context, message);
        if(! sendDisabled) {
            Transport transport = new HTTPTransport();
            transport.sendMessage(context.verityUrl(), messageToSend);
        }
        return messageToSend;
    }

    void disableHTTPSend() {
        sendDisabled = true;
    }

    static String getNewId() {
        return UUID.randomUUID().toString();
    }

    public void sendMessage(Context context, JSONObject message) throws IOException, UndefinedContextException, WalletException {
        // Later we can switch on context.getVerityProtocol
        Transport transport = new HTTPTransport();
        transport.sendMessage(context.verityUrl(), Util.packMessageForVerity(context, message));
    }

    protected abstract void defineMessages();
}
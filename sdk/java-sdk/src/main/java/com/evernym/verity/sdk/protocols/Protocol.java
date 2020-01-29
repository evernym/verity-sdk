package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.transports.HTTPTransport;
import com.evernym.verity.sdk.transports.Transport;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.DbcUtil;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

/**
 * The base class for all protocols
 */
public abstract class Protocol {
    public Protocol(String threadId) {
        DbcUtil.requireNotNull(threadId);

        this.threadId = threadId;
    }

    public Protocol() {
        this(UUID.randomUUID().toString());
    }

    private final String threadId;

    protected JSONObject addThread(JSONObject msg) {
        JSONObject threadBlock = new JSONObject();
        threadBlock.put("thid", threadId);
        msg.put("~thread", threadBlock);
        return msg;
    }

    /**
     * Encrypts and sends a specified message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @param message the message to send to Verity
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws WalletException when there are issues with encryption and decryption
     * @throws UndefinedContextException when the context don't have enough information for this operation
     */
    protected byte[] send(Context context, JSONObject message) throws IOException, VerityException {
        byte[] messageToSend = packMsg(context, message);
        Transport transport = new HTTPTransport();
        transport.sendMessage(context.verityUrl(), messageToSend);
        return messageToSend;
    }

    /**
     * Packs the connection message for the verity
     * @param context an instance of Context that has been initialized with your wallet and key details
     * @return Encrypted connection message ready to be sent to the verity
     * @throws WalletException when there are issues with encryption and decryption
     * @throws UndefinedContextException when the context don't have enough information for this operation
     */
    protected static byte[] packMsg(Context context, JSONObject message) throws VerityException {
        return Util.packMessageForVerity(context, message);
    }

    public static String getNewId() {
        return UUID.randomUUID().toString();
    }

    public void sendMessage(Context context, JSONObject message) throws IOException, UndefinedContextException, WalletException {
        Transport transport = new HTTPTransport();
        transport.sendMessage(context.verityUrl(), Util.packMessageForVerity(context, message));
    }
}
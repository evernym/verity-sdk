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

import static org.hyperledger.indy.sdk.StringUtils.isNullOrWhiteSpace;

/**
 * The base class for all protocols
 */
public abstract class AbstractProtocol implements Protocol {
    /**
     * Constructs a Protocol with a given threadId. A threadId is NOT generated with this constructor.
     *
     * @param threadId given ID used for the thread. MUST not be null.
     */
    public AbstractProtocol(String threadId) {
        if (!isNullOrWhiteSpace(threadId)) {
            DbcUtil.requireNotNull(threadId);

            this.threadId = threadId;
        }
        else {
            this.threadId = UUID.randomUUID().toString();
        }
    }

    /**
     * Constructs a Protocol. The threadId is generated (randomly).
     */
    public AbstractProtocol() {
        this(UUID.randomUUID().toString());
    }

    /**
     * The thread identifier
     * @return the final threadId
     */
    public String getThreadId() {
        return threadId;
    }

    private final String threadId;

    /**
     * Attaches the thread block (including the thid) for a protocol to the given message object (JSON)
     *
     * @param msg with the thread block attached
     */
    protected void addThread(JSONObject msg) {
        JSONObject threadBlock = new JSONObject();
        threadBlock.put("thid", threadId);
        msg.put("~thread", threadBlock);
    }

    /**
     * Encrypts and sends a specified message to Verity
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param message the message to send to Verity
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws WalletException when there are issues with encryption and decryption
     * @throws UndefinedContextException when the context don't have enough information for this operation
     */
    protected void send(Context context, JSONObject message) throws IOException, VerityException {
        if(context == null) {
            throw new UndefinedContextException("Context cannot be NULL");
        }

        byte[] messageToSend = packMsg(context, message);
        Transport transport = new HTTPTransport();
        transport.sendMessage(context.verityUrl(), messageToSend);
    }

    /**
     * Packs the connection message for the verity-application
     * @param context an instance of Context that has been initialized with your wallet and key details
     * @param message the message to be packed for the verity-application
     * @return Encrypted connection message ready to be sent to the verity
     * @throws WalletException when there are issues with encryption and decryption
     * @throws UndefinedContextException when the context don't have enough information for this operation
     */
    protected static byte[] packMsg(Context context, JSONObject message) throws VerityException {
        return Util.packMessageForVerity(context, message);
    }

    /**
     * Generates a new and unique id for a message.
     * @return new message id
     */
    public static String getNewId() {
        return UUID.randomUUID().toString();
    }
}
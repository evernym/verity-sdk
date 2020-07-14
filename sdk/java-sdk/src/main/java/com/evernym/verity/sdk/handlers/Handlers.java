package com.evernym.verity.sdk.handlers;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Stores an array of message handlers that are used when receiving an inbound message
 */
public class Handlers {
    private final ArrayList<MessageHandler> messageHandlers = new ArrayList<>();
    private DefaultMessageHandler defaultHandler;

    /**
     * Adds a MessageHandler for a message type to the list if current message handlers
     * @param messageFamily the family of the message to be handled
     * @param messageHandler the handler function itself
     */
    public void addHandler(MessageFamily messageFamily, MessageHandler.Handler messageHandler) {
        messageHandlers.add(0, new MessageHandler(messageFamily, messageHandler));
    }

    /**
     * Adds a handler for all message types not handled by other message handlers
     * @param messageHandler the function that will be called
     */
    public void addDefaultHandler(DefaultMessageHandler.Handler messageHandler) {
        defaultHandler = new DefaultMessageHandler(messageHandler);
    }

    /**
     * Calls all of the handlers that support handling of this particular message type and message status
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param rawMessage the raw bytes received from Verity
     * @throws WalletException when there are issues with encryption and decryption
     */
    public void handleMessage(Context context, byte[] rawMessage) throws VerityException {
        JSONObject message = Util.unpackMessage(context, rawMessage);
        for(MessageHandler messageHandler: messageHandlers) {
            if(messageHandler.handles(message)) {
                messageHandler.handle(message);
                return;
            }
        }

        // call default if another handler is not called
        if(defaultHandler != null) {
            defaultHandler.handle(message);
        }
    }
}
package com.evernym.verity.sdk.protocols;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.utils.MessagePackaging;
import com.evernym.verity.sdk.utils.VerityConfig;

import org.hyperledger.indy.sdk.IndyException;
import org.json.JSONObject;

/**
 * Stores an array of message handlers that are used when receiving an inbound message
 */
public class Handlers {
    static ArrayList<MessageHandler> messageHandlers = new ArrayList<MessageHandler>();

    /**
     * Adds a MessageHandler for a message type to the list if current message handlers
     * @param messageType the type of message to be handled
     * @param messageHandler the handler function itself
     */
    public static void addHandler(String messageType, MessageHandler.Handler messageHandler) {
        messageHandlers.add(new MessageHandler(messageType, messageHandler));
    }

    /**
     * Adds a MessageHandler for a message type and status to the list if current message handlers
     * @param messageType the type of message to be handled
     * @param messageStatus the status of the message to be handled
     * @param messageHandler the handler function itself
     */
    public static void addHandler(String messageType, Integer messageStatus, MessageHandler.Handler messageHandler) {
        messageHandlers.add(new MessageHandler(messageType, messageStatus, messageHandler));
    }

    /**
     * Calls all of the handlers that support handling of this particular message type and message status
     * @param verityConfig an instance of VerityConfig configured with the results of the provision_sdk.py script
     * @param rawMessage the raw bytes received from Verity
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public static void handleMessage(VerityConfig verityConfig, byte[] rawMessage) throws InterruptedException, ExecutionException, IndyException {
        JSONObject message = MessagePackaging.unpackMessageFromVerity(verityConfig, rawMessage);
        System.out.println("New message from verity: " + message.toString());
        for(MessageHandler messageHandler: messageHandlers) {
            if(messageHandler.handles(message)) {
                messageHandler.handle(message);
            }
        }
    }
    
}
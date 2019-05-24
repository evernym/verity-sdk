package com.evernym.verity.sdk.protocols;

import org.json.JSONObject;

/**
 * Defines how to handle a message of a certain type and optionally with a particular status
 */
public class MessageHandler {
    private String messageType;
    private Integer messageStatus;
    private Handler messageHandler;

    public interface Handler {
        public void handle(JSONObject message);
    }

    /**
     * Associate a handler with a particular message type
     * @param messageType the type of message to be handled
     * @param messageHandler the handler function itself
     */
    MessageHandler(String messageType, Handler messageHandler) {
        this.messageType = messageType;
        this.messageHandler = messageHandler;
    }

    /**
     * Associate a handle with a particular message type and status
     * @param messageType the type of message to be handled
     * @param messageStatus the status of the message to be handled
     * @param messageHandler the handler function itself
     */
    MessageHandler(String messageType, Integer messageStatus, Handler messageHandler) {
        this.messageType = messageType;
        this.messageStatus = messageStatus;
        this.messageHandler = messageHandler;
    }

    /**
     * Checks to see if this MessageHandler handles a particular agent message
     * @param message the JSON structure of the agent message
     * @return whether or not this MessageHandler handles the given message
     */
    public boolean handles(JSONObject message) {
        return message.getString("@type").equals(this.messageType) && (messageStatus == null || message.getInt("status") == this.messageStatus);
    }

    /**
     * Calls the handler function on the agent message
     * @param message the JSON structure of the agent message
     */
    public void handle(JSONObject message) {
        messageHandler.handle(message);
    }
}
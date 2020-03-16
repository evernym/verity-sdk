package com.evernym.verity.sdk.handlers;

import org.json.JSONObject;

/**
 * Defines how to handle a message of a certain type and optionally with a particular status
 */
public class DefaultMessageHandler {
    private Handler messageHandler;

    public interface Handler {
        void handle(JSONObject message);
    }

    /**
     * Associate a handler for the default handler
     * @param messageHandler the handler function itself
     */
    DefaultMessageHandler(Handler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * Calls the handler function on the agent message
     * @param message the JSON structure of the agent message
     */
    public void handle(JSONObject message) {
        this.messageHandler.handle(message);
    }
}
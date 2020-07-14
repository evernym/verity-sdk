package com.evernym.verity.sdk.handlers;

import org.json.JSONObject;

/**
 * Defines how to handle a message of a certain type and optionally with a particular status
 */
public class DefaultMessageHandler {
    private final Handler messageHandler;

    /**
     * A simple default handler interface for JSON object messages
     */
    public interface Handler {
        /**
         * Handles the given JSON object message
         * @param message the message to be handled
         */
        void handle(JSONObject message);
    }

    /**
     * Associates a handler callback function with the default handler action
     * @param messageHandler the handler function itself
     */
    DefaultMessageHandler(Handler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * Calls the handler callback function for the given message
     * @param message the JSON structure of the agent message
     */
    public void handle(JSONObject message) {
        this.messageHandler.handle(message);
    }
}
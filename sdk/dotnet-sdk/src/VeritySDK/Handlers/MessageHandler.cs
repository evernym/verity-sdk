using System;
using System.Json;

namespace VeritySDK
{
    /**
     * Defines how to handle a message of a certain type and optionally with a particular status
     */
    public class MessageHandler
    {
        public delegate void Handler(string msgName, JsonObject message);

        private MessageFamily messageFamily;
        private Handler messageHandler;

        ///**
        // * A simple handler interface for JSON object messages
        // */
        //public interface Handler
        //{
        //    /**
        //     * Handles the given JSON object message
        //     *
        //     * @param msgName the name for the message to be handled
        //     * @param message the message to be handled
        //     */
        //    void handle(string msgName, JsonObject message);
        //}

        /**
         * Associate a handler with a particular message type
         * @param family the type of message to be handled
         * @param messageHandler the handler function itself
         */
        public MessageHandler(MessageFamily family, Handler messageHandler)
        {
            DbcUtil.requireNotNull(family, "family");
            DbcUtil.requireNotNull(messageHandler, "messageHandler");

            this.messageFamily = family;
            this.messageHandler = messageHandler;
        }

        /**
         * Checks to see if this MessageHandler handles a particular agent message
         * @param message the JSON structure of the agent message
         * @return whether or not this MessageHandler handles the given message
         */
        public bool handles(JsonObject message)
        {
            if (this.messageFamily == null) return false;

            return this.messageFamily.matches(message["@type"]);
        }

        /**
         * Calls the handler function on the agent message
         * @param message the JSON structure of the agent message
         */
        public void handle(JsonObject message)
        {
            String msgType = message["@type"];
            String msgName = this.messageFamily.messageName(msgType);

            this.messageHandler(msgName, message);
        }
    }
}

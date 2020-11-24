using System;
using System.Json;
using VeritySDK.Protocols;
using VeritySDK.Utils;

namespace VeritySDK.Handler
{
    /// <summary>
    /// Defines how to handle a message of a certain type and optionally with a particular status
    /// </summary>
    public class MessageHandler
    {
        /// <summary>
        /// Delegate for handles the given named JSON object message 
        /// </summary>
        /// <param name="msgName">the name for the message to be handled</param>
        /// <param name="message">the message to be handled</param>
        public delegate void Handler(string msgName, JsonObject message);


        private MessageFamily messageFamily;
        private Handler messageHandler;

        /// <summary>
        /// Associate a handler with a particular message type 
        /// </summary>
        /// <param name="family">the type of message to be handled</param>
        /// <param name="messageHandler">the handler function itself</param>
        public MessageHandler(MessageFamily family, Handler messageHandler)
        {
            DbcUtil.requireNotNull(family, "family");
            DbcUtil.requireNotNull(messageHandler, "messageHandler");

            this.messageFamily = family;
            this.messageHandler = messageHandler;
        }

        /// <summary>
        /// Checks to see if this MessageHandler handles a particular agent message 
        /// </summary>
        /// <param name="message">the JSON structure of the agent message</param>
        /// <returns>whether or not this MessageHandler handles the given message</returns>
        public bool handles(JsonObject message)
        {
            if (this.messageFamily == null) return false;

            return this.messageFamily.matches(message["@type"]);
        }

        /// <summary>
        /// Calls the handler function on the agent message 
        /// </summary>
        /// <param name="message">the JSON structure of the agent message</param>
        public void handle(JsonObject message)
        {
            string msgType = message["@type"];
            string msgName = this.messageFamily.messageName(msgType);

            this.messageHandler(msgName, message);
        }
    }
}

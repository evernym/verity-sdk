using System.Collections.Generic;
using System.Json;
using VeritySDK.Protocols;
using VeritySDK.Utils;

namespace VeritySDK.Handler
{
    /// <summary>
    /// Stores an array of message handlers that are used when receiving an inbound message
    /// </summary>
    public class Handlers
    {
        /// <summary>
        /// Delegate for handles the given JSON object message
        /// </summary>
        /// <param name="message">the message to be handled</param>
        public delegate void DefaultHandler(JsonObject message);


        private DefaultHandler defaultHandler;
        private List<MessageHandler> messageHandlers = new List<MessageHandler>();

        /// <summary>
        /// Adds a handler for all message types not handled by other message handlers
        /// </summary>
        /// <param name="messageHandler">the function that will be called</param>
        public void addDefaultHandler(DefaultHandler messageHandler)
        {
            defaultHandler = messageHandler;
        }

        /// <summary>
        /// Adds a MessageHandler for a message type to the list if current message handlers
        /// </summary>
        /// <param name="messageFamily">the family of the message to be handled</param>
        /// <param name="messageHandler">the handler function itself</param>
        public void addHandler(MessageFamily messageFamily, MessageHandler.Handler messageHandler)
        {
            messageHandlers.Insert(0, new MessageHandler(messageFamily, messageHandler));
        }

        /// <summary>
        /// Calls all of the handlers that support handling of this particular message type and message status
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="rawMessage">the raw bytes received from Verity</param>
        public void handleMessage(Context context, byte[] rawMessage)
        {
            JsonObject message = Util.unpackMessage(context, rawMessage);
            foreach (MessageHandler messageHandler in messageHandlers)
            {
                if (messageHandler.handles(message))
                {
                    messageHandler.handle(message);
                    return;
                }
            }

            // call default if another handler is not called
            if (defaultHandler != null)
                defaultHandler(message);
        }
    }
}

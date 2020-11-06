using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// Defines how to handle a message of a certain type and optionally with a particular status
    /// </summary>
    public class DefaultMessageHandler
    {
        /// <summary>
        /// A simple default delegate handler interface for JSON object messages
        /// </summary>
        /// <param name="message">the JSON structure of the agent message</param>
        public delegate void Handler(JsonObject message);

        private Handler messageHandler;

        /// <summary>
        /// Associates a handler callback function with the default handler action
        /// </summary>
        /// <param name="messageHandler">the handler function itself</param>
        public DefaultMessageHandler(Handler messageHandler)
        {
            this.messageHandler = messageHandler;
        }

        /// <summary>
        /// Calls the handler callback function for the given message         
        /// </summary>
        /// <param name="message">the JSON structure of the agent message</param>
        public void handle(JsonObject message)
        {
            this.messageHandler(message);
        }
    }
}

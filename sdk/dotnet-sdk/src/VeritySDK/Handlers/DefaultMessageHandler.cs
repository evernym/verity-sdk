using System.Json;

namespace VeritySDK
{

    /**
     * Defines how to handle a message of a certain type and optionally with a particular status
     */
    public class DefaultMessageHandler
    {
        public delegate void Handler(JsonObject message);

        private Handler messageHandler;

    /**
     * A simple default handler interface for JSON object messages
     */
    //public interface Handler
    //    {
    //        /**
    //         * Handles the given JSON object message
    //         * @param message the message to be handled
    //         */
    //        void handle(JsonObject message);
    //    }

        /**
         * Associates a handler callback function with the default handler action
         * @param messageHandler the handler function itself
         */
        public DefaultMessageHandler(Handler messageHandler)
        {
            this.messageHandler = messageHandler;
        }

        /**
         * Calls the handler callback function for the given message
         * @param message the JSON structure of the agent message
         */
        public void handle(JsonObject message)
        {
            this.messageHandler(message);
        }
    }
}

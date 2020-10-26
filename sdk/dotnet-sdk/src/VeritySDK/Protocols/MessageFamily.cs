using System;

namespace VeritySDK
{
    /**
     * Interface for message families. Message families are the set of messages used by a protocol. They include
     * three types of messages: protocol messages, control messages and signal messages.
     * <p/>
     * Protocol messages are messages exchange between parties of the protocol. Each party is an independent self-sovereign
     * domain.
     * <p/>
     * Control messages are messages sent by a controller (applications that use verity-sdk are controllers) to the verity
     * application. These messages control the protocol and make decisions for the protocol.
     * <p/>
     * Signal messages are messages sent from the verity-application agent to a controller
     * <p/>
     * Message family messages always have a type. This type has 4 parts: qualifier, family, version and name. Three parts
     * are static and defined by this interface.
     * <p/>
     * Example: did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/problem_report
     * <p/>
     * qualifier: did:sov:BzCbsNYhMrjHiqZDTUASHg
     * <br/>
     * family: connections
     * <br/>
     * version: 1.0
     * <br/>
     * name: problem_report
     *
     */
    public abstract class MessageFamily
    {
        /**
         * @return the qualifier for the message family
         */
        public abstract string qualifier();

        /**
         * @return the family name for the message family
         */
        public abstract string family();

        /**
         * @return the version for the message family
         */
        public abstract string version();

        /**
         * Tests if a message type matches the message family defined by the interface
         * @param qualifiedMessageType a message type that tested
         * @return true if the given message type matches otherwise returns false
         */
        public bool matches(string qualifiedMessageType)
        {
            if (qualifiedMessageType == null)
                return false;

            return qualifiedMessageType.StartsWith(getMessageFamily());
        }

        /**
         * Parse out the message name from the message type
         * @param qualifiedMessageType a message type that is parsed
         * @return the parsed message name
         * @throws InvalidMessageTypeException thrown the given message type is un-parseable
         */
        public string messageName(string qualifiedMessageType)
        {
            if (!matches(qualifiedMessageType))
            {
                throw new InvalidMessageTypeException("Given qualified message type does not match this MessageFamily");
            }

            string messageFamily = getMessageFamily();

            if (qualifiedMessageType[messageFamily.Length] != '/' || !(qualifiedMessageType.Length > messageFamily.Length + 1))
            {
                throw new InvalidMessageTypeException("Given qualified message type does not have a message name");
            }

            return qualifiedMessageType.Substring(messageFamily.Length + 1);
        }

        /**
         * Combines the element of this message family into a prefix of the message type, without the message name
         * @return a prefix portion of the message type
         */
        public string messageFamily()
        {
            return qualifier() + ";spec/" + family() + "/" + version();
        }


        [Obsolete("replaced by messageFamily()")]
        protected string getMessageFamily()
        {
            return messageFamily();
        }

        /**
         * Combines the element of this message family with the given message name to build a fully qualified message type
         * @param msgName the message name used in the built message type
         * @return fully qualified message type
         */
        protected string messageType(string msgName)
        {
            return getMessageFamily() + "/" + msgName;
        }


        [Obsolete("replaced by messageType(string)")]
        protected string getMessageType(string msgName)
        {
            return messageType(msgName);
        }
    }
}

using System;

namespace VeritySDK
{
    /// <summary>
    /// Interface for message families. Message families are the set of messages used by a protocol. They include
    /// three types of messages: protocol messages, control messages and signal messages.
    /// 
    /// Protocol messages are messages exchange between parties of the protocol. Each party is an independent self-sovereign
    /// domain.
    /// 
    /// Control messages are messages sent by a controller (applications that use verity-sdk are controllers) to the verity
    /// application. These messages control the protocol and make decisions for the protocol.
    /// 
    /// Signal messages are messages sent from the verity-application agent to a controller
    /// 
    /// Message family messages always have a type. This type has 4 parts: qualifier, family, version and name. Three parts
    /// are static and defined by this interface.
    /// 
    /// Example: did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/problem_report
    /// 
    /// qualifier: did:sov:BzCbsNYhMrjHiqZDTUASHg
    /// 
    /// family: connections
    /// 
    /// version: 1.0
    /// 
    /// name: problem_report
    /// </summary>
    public abstract class MessageFamily
    {
        /// <summary>
        /// Qualifier for the message family
        /// </summary>
        public abstract string qualifier();

        /// <summary>
        /// Famity for the message family
        /// </summary>
        public abstract string family();

        /// <summary>
        /// Version for the message family
        /// </summary>
        public abstract string version();

        /// <summary>
        /// * Tests if a message type matches the message family defined by the interface
        /// </summary>
        /// <param name="qualifiedMessageType">Type a message type that tested</param>
        /// <returns>true if the given message type matches otherwise returns false</returns>
        public bool matches(string qualifiedMessageType)
        {
            if (qualifiedMessageType == null)
                return false;

            return qualifiedMessageType.StartsWith(getMessageFamily());
        }

        /// <summary>
        /// Parse out the message name from the message type 
        /// </summary>
        /// <param name="qualifiedMessageType">Type a message type that is parsed</param>
        /// <returns>the parsed message name</returns>
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

        /// <summary>
        /// Combines the element of this message family into a prefix of the message type, without the message name
        /// </summary>
        /// <returns>a prefix portion of the message type</returns>
        public string messageFamily()
        {
            return qualifier() + ";spec/" + family() + "/" + version();
        }


        /// <summary>
        /// Combines the element of this message family with the given message name to build a fully qualified message type 
        /// </summary>
        /// <param name="msgName">the message name used in the built message type</param>
        /// <returns>fully qualified message type</returns>
        protected string messageType(string msgName)
        {
            return messageFamily() + "/" + msgName;
        }


        [Obsolete("replaced by messageFamily()")]
        protected string getMessageFamily()
        {
            return messageFamily();
        }


        [Obsolete("replaced by messageType(string)")]
        protected string getMessageType(string msgName)
        {
            return messageType(msgName);
        }
    }
}

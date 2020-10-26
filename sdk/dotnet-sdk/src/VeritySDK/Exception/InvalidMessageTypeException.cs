using System;

namespace VeritySDK
{
    /**
     * Exception class used express invalid message types when parsing incoming messages
     */
    public class InvalidMessageTypeException : VerityException
    {
        /// <summary>
        /// Constructs a new exception with the given message.
        /// </summary>
        /// <param name="message">message the given message for the cause of the exception.</param>
        public InvalidMessageTypeException(string message) : base(message) { }

        /// <summary>
        /// Constructs a new exception with the given message and cause.
        /// </summary>
        /// <param name="message">message the given message for the cause of the exception.</param>
        /// <param name="cause">cause the given cause for the exception.</param>
        public InvalidMessageTypeException(String message, System.Exception cause) : base(message, cause) { }
    }
}

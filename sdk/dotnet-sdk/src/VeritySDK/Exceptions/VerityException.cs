using System;

namespace VeritySDK.Exceptions
{
    /// <summary>
    /// Generic and abstract exception class for exceptions throw by the Verity-SDK
    /// </summary>
    public abstract class VerityException : System.Exception
    {
        /// <summary>
        /// Constructs a new with the given message.
        /// </summary>
        /// <param name="message">message the given message for the cause of the exception.</param>
        public VerityException(string message) : base(message) { }

        /// <summary>
        /// Constructs a new with the given message and cause.
        /// </summary>
        /// <param name="message">the given message for the cause of the exception.</param>
        /// <param name="cause">the given cause for the exception.</param>
        public VerityException(String message, System.Exception cause) : base(message, cause) { }
    }
}


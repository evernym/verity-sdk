using System;

namespace VeritySDK
{
    /// <summary>
    /// Exception class used express an invalid provision token.
    /// </summary>
    public class ProvisionTokenException : VerityException
    {
        /// <summary>
        /// Constructs a new ProvisionTokenException with the given message.
        /// </summary>
        /// <param name="message">the given message for the cause of the exception</param>
        public ProvisionTokenException(String message) : base(message) { }

        /// <summary>
        /// Constructs a new ProvisionTokenException with the given message and cause.
        /// </summary>
        /// <param name="message">the given message for the cause of the exception.</param>
        /// <param name="cause">the given cause(Throwable) for the exception.</param>
        public ProvisionTokenException(String message, System.Exception cause) : base(message, cause) { }
    }

}

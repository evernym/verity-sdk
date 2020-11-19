using System;

namespace VeritySDK.Exceptions
{
    ///// <summary>
    ///// Exception class that is thrown when the wallet is un-expectantly closed
    ///// </summary>
    public class WalletException : VerityException
    {
        /// <summary>
        /// Constructs a new exception with the given message.
        /// </summary>
        /// <param name="message">the given message for the cause of the exception.</param>
        public WalletException(string message) : base(message) { }

        /// <summary>
        /// Constructs a new exception with the given message and cause.
        /// </summary>
        /// <param name="message">the given message for the cause of the exception.</param>
        /// <param name="cause">the given cause for the exception.</param>
        public WalletException(String message, System.Exception cause) : base(message, cause) { }
    }
}

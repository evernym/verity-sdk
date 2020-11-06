using System;

namespace VeritySDK
{
    ///// <summary>
    ///// Exception class that is thrown when the wallet fails to open
    ///// </summary>
    public class WalletOpenException : WalletException
    {
        /// <summary>
        /// Constructs a new exception with the given message.
        /// </summary>
        /// <param name="message">the given message for the cause of the exception.</param>
        public WalletOpenException(string message) : base(message) { }

        /// <summary>
        /// Constructs a new exception with the given message and cause.
        /// </summary>
        /// <param name="message">the given message for the cause of the exception.</param>
        /// <param name="cause">the given cause for the exception.</param>
        public WalletOpenException(String message, System.Exception cause) : base(message, cause) { }
    }

}

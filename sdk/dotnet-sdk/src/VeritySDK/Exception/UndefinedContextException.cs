using System;

namespace VeritySDK
{
    /// <summary>
    /// Exception class used when required data is not contained in given Context object
    /// </summary>
    public class UndefinedContextException : VerityException
    {
        /// <summary>
        /// Constructs a new exception with the given message.
        /// </summary>
        /// <param name="message">message the given message for the cause of the exception.</param>
        public UndefinedContextException(String message) : base(message) { }
    }
}

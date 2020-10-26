using System;

namespace VeritySDK
{
    /**
     *  Exception class used express an invalid provision token.
     */
    public class ProvisionTokenException : VerityException
    {
    /**
     * Constructs a new ProvisionTokenException with the given message.
     *
     * @param message the given message for the cause of the exception.
     *
     * @see VerityException#VerityException(String)
     */
    public ProvisionTokenException(String message): base(message) {  }

    /**
     * Constructs a new ProvisionTokenException with the given message and cause.
     *
     * @param message the given message for the cause of the exception.
     * @param cause the given cause (Throwable) for the exception.
     *
     * @see VerityException#VerityException(String, Throwable)
     */
    public ProvisionTokenException(String message, System.Exception cause) : base (message, cause) { }
}

}

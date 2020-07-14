package com.evernym.verity.sdk.exceptions;

/**
 *  Exception class used express an invalid provision token.
 */
public class ProvisionTokenException extends VerityException {
    /**
     * Constructs a new ProvisionTokenException with the given message.
     *
     * @param message the given message for the cause of the exception.
     *
     * @see VerityException#VerityException(String)
     */
    public ProvisionTokenException(String message) {super(message);}

    /**
     * Constructs a new ProvisionTokenException with the given message and cause.
     *
     * @param message the given message for the cause of the exception.
     * @param cause the given cause (Throwable) for the exception.
     *
     * @see VerityException#VerityException(String, Throwable)
     */
    public ProvisionTokenException(String message, Throwable cause) {super(message, cause);}
}

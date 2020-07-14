package com.evernym.verity.sdk.exceptions;

/**
 * Exception class that is thrown when wallet operations fail
 */
public class WalletException extends VerityException {
    /**
     * Constructs a new WalletException with the given message.
     *
     * @param message the given message for the cause of the exception.
     *
     * @see VerityException#VerityException(String)
     */
    public WalletException(String message) {super(message);}

    /**
     * Constructs a new InvalidMessageTypeException with the given message and cause.
     *
     * @param message the given message for the cause of the exception.
     * @param cause the given cause (Throwable) for the exception.
     *
     * @see VerityException#VerityException(String, Throwable)
     */
    public WalletException(String message, Throwable cause) {super(message, cause);}
}

package com.evernym.verity.sdk.exceptions;

/**
 * Exception class that is thrown when the wallet fails to open
 */
public class WalletOpenException extends WalletException {
    /**
     * Constructs a new InvalidMessageTypeException with the given message.
     *
     * @param message the given message for the cause of the exception.
     *
     * @see VerityException#VerityException(String)
     */
    public WalletOpenException(String message) {super(message);}

    /**
     * Constructs a new InvalidMessageTypeException with the given message and cause.
     *
     * @param message the given message for the cause of the exception.
     * @param cause the given cause (Throwable) for the exception.
     *
     * @see VerityException#VerityException(String, Throwable)
     */
    public WalletOpenException(String message, Throwable cause) {super(message, cause);}
}

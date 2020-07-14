package com.evernym.verity.sdk.exceptions;

/**
 * Generic and abstract exception class for exceptions throw by the Verity-SDK
 */
public abstract class VerityException extends Exception {
    /**
     * Constructs a new VerityException with the given message.
     *
     * @param message the given message for the cause of the exception.
     */
    public VerityException(String message) {super(message);}

    /**
     * Constructs a new VerityException with the given message and cause.
     *
     * @param message the given message for the cause of the exception.
     * @param cause the given cause (Throwable) for the exception.
     */
    public VerityException(String message, Throwable cause) {super(message, cause);}
}

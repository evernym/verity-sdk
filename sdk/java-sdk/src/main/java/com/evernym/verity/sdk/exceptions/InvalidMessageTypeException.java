package com.evernym.verity.sdk.exceptions;

/**
 * Exception class used express invalid message types when parsing incoming messages
 */
public class InvalidMessageTypeException extends VerityException {
    /**
     * Constructs a new InvalidMessageTypeException with the given message.
     *
     * @param message the given message for the cause of the exception.
     *
     * @see VerityException#VerityException(String)
     */
    public InvalidMessageTypeException(String message) {super(message);}

    /**
     * Constructs a new InvalidMessageTypeException with the given message and cause.
     *
     * @param message the given message for the cause of the exception.
     * @param cause the given cause (Throwable) for the exception.
     * 
     * @see VerityException#VerityException(String, Throwable)
     */
    public InvalidMessageTypeException(String message, Throwable cause) {super(message, cause);}
}

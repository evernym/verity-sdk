package com.evernym.verity.sdk.exceptions;

/**
 * Exception class used when required data is not contained in given Context object
 */
public class UndefinedContextException extends VerityException {
    /**
     * Constructs a new UndefinedContextException with the given message.
     *
     * @param message the given message for the cause of the exception.
     *
     * @see VerityException#VerityException(String)
     */
    public UndefinedContextException(String message) {
        super(message);
    }
}

package com.evernym.verity.sdk.exceptions;

/**
 * Exception class that is thrown when the wallet is un-expectantly closed
 */
public class WalletClosedException extends WalletException {
    /**
     * Constructs a new WalletClosedException
     *
     * @see VerityException#VerityException(String)
     */
    public WalletClosedException() {
        super("A context wallet handle accessed after being closed");
    }
}

package com.evernym.verity.sdk.exceptions;

public class WalletOpenException extends WalletException {
    public WalletOpenException(String message) {super(message);}

    public WalletOpenException(Throwable cause) {
        super("Wallet failed to open", cause);
    }
}

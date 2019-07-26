package com.evernym.verity.sdk.exceptions;

public class WalletCloseException extends WalletException {
    public WalletCloseException(Throwable cause) {
        super("Wallet failed to close", cause);
    }
}

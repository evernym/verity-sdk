package com.evernym.verity.sdk.exceptions;

public class WalletClosedException extends WalletException {
    public WalletClosedException() {
        super("A context wallet handle accessed after being closed");
    }
}

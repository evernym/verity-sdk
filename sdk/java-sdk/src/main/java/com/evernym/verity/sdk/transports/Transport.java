package com.evernym.verity.sdk.transports;

import java.io.IOException;

/**
 * The base class for all Transports used for sending messages to Verity
 */
public abstract class Transport {
    public abstract void sendMessage(String url, byte[] message) throws IOException;
}
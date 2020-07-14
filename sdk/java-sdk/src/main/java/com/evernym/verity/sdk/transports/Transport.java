package com.evernym.verity.sdk.transports;

import java.io.IOException;

/**
 * The base class for all Transports used for sending messages to Verity
 */
public abstract class Transport {
    /**
     * Sends the given byte array message to the given url via transport defined by the implementing object
     * @param url the given url destination for the message
     * @param message the given message to be transported
     * @throws IOException when the given message fails to be send to the intended destination
     */
    public abstract void sendMessage(String url, byte[] message) throws IOException;
}
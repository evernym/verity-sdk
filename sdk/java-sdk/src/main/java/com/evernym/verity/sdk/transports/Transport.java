package com.evernym.verity.sdk.transports;

import java.io.IOException;

public abstract class Transport {
    public abstract void sendMessage(String url, byte[] message) throws IOException;
}
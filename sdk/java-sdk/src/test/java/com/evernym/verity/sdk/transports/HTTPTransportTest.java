package com.evernym.verity.sdk.transports;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HTTPTransportTest {
    @Test
    public void testBasicHttpPost() throws Exception {
        Transport transport = new HTTPTransport();
        transport.sendMessage("https://jsonplaceholder.typicode.com/posts", "Hello, World!".getBytes());
        assertTrue(true);
    }
}
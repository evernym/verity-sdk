package com.evernym.verity.sdk.transports;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HTTPTransportTest {
    @Test
    public void testBasicHttpPost() throws Exception {
        Transport transport = new HTTPTransport();
        transport.sendMessage("http://dummy.restapiexample.com/api/v1/create", "{\"Hello\": \"World!\"}".getBytes());
    }
}
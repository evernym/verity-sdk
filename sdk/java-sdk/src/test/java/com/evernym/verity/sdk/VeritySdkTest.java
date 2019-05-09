package com.evernym.verity.sdk;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VeritySdkTest {

    @Test
    public void initialTesting() throws Exception {
        VeritySdk sdk = new VeritySdk("https://eas-team1.pdev.evernym.com");
        Listener listener = new Listener(4000, 5, (String message) -> {
            sdk.handleInboundMessage(message);
        });
        listener.listen();
        
        try {
            sdk.provision();
            System.out.println("Provisioned");
            sdk.newConnection();
            System.out.println("Connected");
        } catch(Exception e) { // Terrible practice but I'm just tinkering.
            System.out.println(e.getMessage());
            assertTrue( false );
        }
        assertTrue( true );
    }
}

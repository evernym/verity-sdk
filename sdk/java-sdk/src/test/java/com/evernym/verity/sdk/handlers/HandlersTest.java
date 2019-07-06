package com.evernym.verity.sdk.handlers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HandlersTest {

    @Test
    public void testIsProblemReport() {
        assertTrue(Handlers.isProblemReport("did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/enroll/0.1/problem-report"));
        assertFalse(Handlers.isProblemReport("did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/enroll/0.1/other-msg"));
    }
}
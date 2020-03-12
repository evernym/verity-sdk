package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.protocols.MessageFamily;
import org.junit.Test;
import static org.junit.Assert.*;

public class MsgFamilyBuilderTest {

    @Test(expected = IllegalStateException.class)
    public void testInvalidMsgType() {
        MsgFamilyBuilder.fromQualifiedMsgType("");
    }

    @Test
    public void testValidMsgType() {
        MessageFamily msgFamily = MsgFamilyBuilder.fromQualifiedMsgType("did:sov:123456789abcdefghi1234;spec/connecting/0.6/CONN_REQUEST_RESP");
        assertEquals("did:sov:123456789abcdefghi1234", msgFamily.qualifier());
        assertEquals("connecting", msgFamily.family());
        assertEquals("0.6", msgFamily.version());
    }
}


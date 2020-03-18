package com.evernym.verity.sdk.protocols.connecting.v_1_0;

import com.evernym.verity.sdk.protocols.connecting.Connecting;
import com.evernym.verity.sdk.utils.Util;
import org.junit.Test;


import static org.junit.Assert.*;

public class ConnectionsTest {

    String did = "did1";
    String label = "Alice";
    String parentThreadId = "thread123";
    String base64Url = "<TBD>";

    @Test
    public void testGetMessageType() {
        Connecting connecting = Connecting.v1_0(parentThreadId, label, base64Url);
        String msgName = "msg name";
        assertEquals(
            Util.getMessageType(
                    Util.COMMUNITY_MSG_QUALIFIER,
                    "connections",
                    "1.0",
                    msgName
            ),
            Util.getMessageType(connecting, msgName)
        );
    }

}
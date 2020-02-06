package com.evernym.verity.sdk.protocols.connecting.v_1_0;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.connecting.Connecting;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.Assert.*;

public class ConnectionsTest {

    @Test
    public void testGetMessageType() {
        Connecting connecting = Connecting.v_10("did1", "Alice");
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

    @Test
    public void testInvitationWithDIDMsg() throws VerityException, IOException {
        Connecting connecting = Connecting.v_10("did1", "Alice");
        JSONObject msg = connecting.invitationMsg(TestHelpers.getContext());
        testInvitationWithDIDMsg(msg);
    }

    private void testInvitationWithDIDMsg(JSONObject msg) {
        assertEquals("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/invitation", msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertNotNull(msg.getJSONObject("~thread").getString("thid"));
        assertEquals("did1", msg.getString("did"));
        assertEquals("Alice", msg.getString("label"));
    }

}
package com.evernym.verity.sdk.protocols.connecting.v1_0;

import com.evernym.verity.sdk.TestBase;
import com.evernym.verity.sdk.protocols.connecting.Connecting;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConnectionsTest extends TestBase {

    String label = "Alice";
    String base64Url = "<TBD>";

    @Test
    public void testGetMessageType() {
        ConnectionsV1_0 connecting = Connecting.v1_0(label, base64Url);
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
    public void testAcceptInvitation() throws Exception {
        withContext ( context -> {
            ConnectionsV1_0 testProtocol = Connecting.v1_0(label, base64Url);
            byte [] msg = testProtocol.acceptMsgPacked(context);
            JSONObject unpackedMessage = Util.unpackForwardMessage(context, msg);
            testAcceptInviteMessage(unpackedMessage);
        });
    }

    @Test
    public void testStatus() throws Exception {
        withContext ( context -> {
            ConnectionsV1_0 testProtocol = Connecting.v1_0(label, base64Url);
            byte [] msg = testProtocol.statusMsgPacked(context);
            JSONObject unpackedMessage = Util.unpackForwardMessage(context, msg);
            testStatusInviteMessage(unpackedMessage);
        });
    }

    private void testAcceptInviteMessage(JSONObject msg) {
        assertEquals("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/accept", msg.getString("@type"));
    }

    private void testStatusInviteMessage(JSONObject msg) {
        assertEquals("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/status", msg.getString("@type"));
    }

    private void testBaseMessage(JSONObject msg) {
        assertNotNull(msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertNotNull(msg.getJSONObject("~thread").getString("thid"));
    }

}
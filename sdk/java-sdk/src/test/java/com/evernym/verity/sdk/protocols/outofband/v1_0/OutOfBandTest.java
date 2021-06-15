package com.evernym.verity.sdk.protocols.outofband.v1_0;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.outofband.OutOfBand;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OutOfBandTest {

    @Test
    public void testGetMessageType() {
        OutOfBandV1_0 outofbandProvisioning =
                OutOfBand.v1_0("testForRelationship", "testInviteUrl");
        String msgName = "msg name";
        assertEquals(
                Util.getMessageType(
                        Util.COMMUNITY_MSG_QUALIFIER,
                        "out-of-band",
                        "1.0",
                        msgName
                ),
                Util.getMessageType(outofbandProvisioning, msgName)
        );
    }

    @Test
    public void testGetThreadId() {
        OutOfBandV1_0 testProtocol = OutOfBand.v1_0("testForRelationship", "testInviteUrl");
        assertNotNull(testProtocol.getThreadId());
    }

    @Test
    public void testHandshakeReuseMsg() throws VerityException, IOException {
        OutOfBandV1_0 outofband =
                OutOfBand.v1_0("testForRelationship", "testInviteUrl");
        JSONObject msg = outofband.handshakeReuseMsg(TestHelpers.getContext());
        testHandshakeReuseMsg(msg);
    }

    private void testHandshakeReuseMsg(JSONObject msg) {
        testBaseMessage(msg);
        assertEquals(Util.COMMUNITY_MSG_QUALIFIER + "/out-of-band/1.0/reuse", msg.getString("@type"));
        assertEquals("testForRelationship", msg.getString("~for_relationship"));
        assertEquals("testInviteUrl", msg.getString("inviteUrl"));
    }

    private void testBaseMessage(JSONObject msg) {
        assertNotNull(msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertNotNull(msg.getJSONObject("~thread").getString("thid"));
    }
}
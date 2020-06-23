package com.evernym.verity.sdk.protocols.outofband.v1_0;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.outofband.OutOfBand;
import com.evernym.verity.sdk.protocols.outofband.v1_0.OutOfBandV1_0;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OutOfBandTest {

    final String label = "Alice";
    final String goalCode = "goalCode";
    final String goal = "goal";

    @Test
    public void testGetMessageType() {
        OutOfBandV1_0 outofbandProvisioning = OutOfBand.v1_0("threadId");
        String msgName = "msg name";
        assertEquals(
            Util.getMessageType(
                    Util.EVERNYM_MSG_QUALIFIER,
                    "out-of-band",
                    "1.0",
                    msgName
            ),
            Util.getMessageType(outofbandProvisioning, msgName)
        );
    }

    @Test
    public void testCreateMsg() throws VerityException, IOException {
        OutOfBandV1_0 outofband = OutOfBand.v1_0(label, goalCode, goal, null, null);
        JSONObject msg = outofband.createMsg(TestHelpers.getContext());
        testCreateMsg(msg);
    }

    private void testCreateMsg(JSONObject msg) {
        assertEquals("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/out-of-band/1.0/create", msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertNotNull(msg.getString("label"));
        assertNotNull(msg.getString("goal_code"));
        assertNotNull(msg.getString("goal"));
    }

    @Test
    public void testConnectionInvitationMsg() throws VerityException, IOException {
        OutOfBandV1_0 outofband = OutOfBand.v1_0("thread-id");
        JSONObject msg = outofband.outOfBandInvitationMsg(TestHelpers.getContext());
        testConnectionInvitationMsg(msg);
    }

    private void testConnectionInvitationMsg(JSONObject msg) {
        assertEquals("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/out-of-band/1.0/connection-invitation", msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
    }
}
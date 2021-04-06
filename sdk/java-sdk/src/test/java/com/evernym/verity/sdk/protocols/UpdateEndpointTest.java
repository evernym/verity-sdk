package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.protocols.updateconfigs.UpdateConfigs;
import com.evernym.verity.sdk.protocols.updateconfigs.v0_6.UpdateConfigsV0_6;
import com.evernym.verity.sdk.protocols.updateendpoint.UpdateEndpoint;
import com.evernym.verity.sdk.protocols.updateendpoint.v0_6.UpdateEndpointV0_6;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;

import static com.evernym.verity.sdk.TestHelpers.unpackForwardMessage;
import static org.junit.Assert.*;

public class UpdateEndpointTest {

    @Test
    public void testGetMessageType() {
        UpdateEndpointV0_6 testProtocol = UpdateEndpoint.v0_6();
        String msgName = "msg name";
        assertEquals(
                Util.getMessageType(Util.EVERNYM_MSG_QUALIFIER, testProtocol.family(), testProtocol.version(), msgName),
                testProtocol.messageType(msgName)
        );
    }

    @Test
    public void testGetThreadId() {
        UpdateEndpointV0_6 testProtocol = UpdateEndpoint.v0_6();
        assertNotNull(testProtocol.getThreadId());
    }

    @Test
    public void testUpdateMsg() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            UpdateEndpointV0_6 testProtocol = UpdateEndpoint.v0_6();
            JSONObject msg = testProtocol.updateMsg(context);

            assertEquals(
                    msg.getJSONObject("comMethod").getString("value"),
                    context.endpointUrl()
            );
            assertEquals(
                    Util.EVERNYM_MSG_QUALIFIER + "/configs/0.6/UPDATE_COM_METHOD",
                    msg.getString("@type")
            );
            assertNotNull(msg.getString("@id"));
            assertEquals("webhook", msg.getJSONObject("comMethod").getString("id"));
            assertEquals(2, msg.getJSONObject("comMethod").getInt("type"));
            assertEquals(
                    "1.0",
                    msg.getJSONObject("comMethod").getJSONObject("packaging").getString("pkgType")
            );
            ArrayList<String> expectedReceipientKeys = new ArrayList<String>();
            expectedReceipientKeys.add(context.sdkVerKey());
            assertEquals(
                    expectedReceipientKeys,
                    msg.getJSONObject("comMethod").getJSONObject("packaging").getJSONArray("recipientKeys").toList()
            );
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }

    @Test
    public void testUpdate() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            UpdateEndpointV0_6 testProtocol = UpdateEndpoint.v0_6();
            byte [] message = testProtocol.updateMsgPacked(context);
            JSONObject unpackedMessage = unpackForwardMessage(context, message);
            assertEquals(
                    Util.EVERNYM_MSG_QUALIFIER + "/configs/0.6/UPDATE_COM_METHOD",
                    unpackedMessage.getString("@type")
            );

        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }
}
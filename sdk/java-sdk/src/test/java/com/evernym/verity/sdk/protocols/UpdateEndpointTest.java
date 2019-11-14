package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class UpdateEndpointTest {

    @Test
    public void testGetMessageType() {
        String msgName = "msg name";
        assertEquals(Util.getMessageType(Util.EVERNYM_MSG_QUALIFIER, "configs", "0.6", msgName), UpdateEndpoint.getMessageType(msgName));
    }

    @Test
    public void testConstructor() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            UpdateEndpoint updateEndpoint = new UpdateEndpoint(context);
            assertEquals(updateEndpoint.endpointUrl, context.endpointUrl());
            testMessages(updateEndpoint);
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }

    private void testMessages(UpdateEndpoint updateEndpoint) {
        JSONObject msg = updateEndpoint.messages.getJSONObject(UpdateEndpoint.UPDATE_ENDPOINT);
        assertEquals(UpdateEndpoint.getMessageType("UPDATE_COM_METHOD"), msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertEquals("webhook", msg.getJSONObject("comMethod").getString("id"));
        assertEquals(2, msg.getJSONObject("comMethod").getInt("type"));
        assertEquals(updateEndpoint.endpointUrl, msg.getJSONObject("comMethod").getString("value"));
    }

    @Test
    public void testUpdate() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            UpdateEndpoint updateEndpoint = new UpdateEndpoint(context);
            updateEndpoint.disableHTTPSend();
            byte [] message = updateEndpoint.update();
            JSONObject unpackedMessage = Util.unpackForwardMessage(context, message);
            assertEquals(UpdateEndpoint.getMessageType(UpdateEndpoint.UPDATE_ENDPOINT), unpackedMessage.getString("@type"));

        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }
}
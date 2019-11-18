package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConnectingTest {
    private String sourceId = "source_id";
    private String phoneNumber = "123-456-7891";
    private boolean includePublicDID = true;

    @Test
    public void testGetMessageType() {
        Connecting connecting = new Connecting("none");
        String msgName = "msg name";
        assertEquals(Util.getMessageType(Util.EVERNYM_MSG_QUALIFIER,"connecting", "0.6", msgName), Connecting.getMessageType(msgName));
    }

    @Test
    public void testConstructorWithSourceId() {
        Connecting connecting = new Connecting(sourceId);
        assertEquals(sourceId, connecting.sourceId);
        assertNull(connecting.phoneNumber);
        assertFalse(connecting.includePublicDID);
        testMessages(connecting);
    }

    @Test
    public void testConstructorWithSourceIdAndUsePublicDid() {
        Connecting connecting = new Connecting(sourceId, includePublicDID);
        assertEquals(sourceId, connecting.sourceId);
        assertNull(connecting.phoneNumber);
        assertEquals(includePublicDID, connecting.includePublicDID);
        testMessages(connecting);
    }

    @Test
    public void testConstructorWithSourceIdAndPhoneNumber() {
        Connecting connecting = new Connecting(sourceId, phoneNumber);
        assertEquals(sourceId, connecting.sourceId);
        assertEquals(phoneNumber, connecting.phoneNumber);
        assertFalse(connecting.includePublicDID);
        testMessages(connecting);
    }

    @Test
    public void testFullConstructor() {
        Connecting connecting = new Connecting(sourceId, phoneNumber, includePublicDID);
        assertEquals(sourceId, connecting.sourceId);
        assertEquals(phoneNumber, connecting.phoneNumber);
        assertEquals(includePublicDID, connecting.includePublicDID);
        testMessages(connecting);
    }

    private void testMessages(Connecting connecting) {
        JSONObject msg = connecting.messages.getJSONObject(Connecting.CREATE_CONNECTION);
        assertEquals(msg.getString("@type"), Connecting.getMessageType(Connecting.CREATE_CONNECTION));
        assertNotNull(msg.getString("@id"));
        assertEquals(msg.getString("sourceId"), connecting.sourceId);
        if(connecting.phoneNumber != null)
            assertEquals(msg.getString("phoneNo"), connecting.phoneNumber);
        assertEquals(msg.getBoolean("includePublicDID"), connecting.includePublicDID);

        JSONObject statusMsg = connecting.messages.getJSONObject(Connecting.GET_STATUS);
        assertEquals(Connecting.getMessageType(Connecting.GET_STATUS), statusMsg.getString("@type"));
        assertNotNull(statusMsg.getString("@id"));
    }

    @Test
    public void testConnect() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            Connecting connecting = new Connecting(sourceId, phoneNumber, includePublicDID);
            connecting.disableHTTPSend();
            byte[] message = connecting.connect(context);
            JSONObject unpackedMessage = Util.unpackForwardMessage(context, message);
            assertEquals(Connecting.getMessageType(Connecting.CREATE_CONNECTION), unpackedMessage.getString("@type"));

            byte [] statusMsg = connecting.status(context);
            JSONObject unpackedStatusMessage = Util.unpackForwardMessage(context, statusMsg);
            assertEquals(Connecting.getMessageType(Connecting.GET_STATUS), unpackedStatusMessage.getString("@type"));
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }
}
package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.TestHelpers;

import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConnectingTest {
    private String sourceId = "source_id";
    private String phoneNumber = "123-456-7891";
    private boolean usePublicDid = true;

    @Test
    public void testGetMessageType() {
        Connecting connecting = new Connecting("none");
        String msgName = "msg name";
        assertEquals(Util.getMessageType("connecting", "0.1", msgName), Connecting.getMessageType(msgName));
    }

    @Test
    public void testConstructorWithSourceId() {
        Connecting connecting = new Connecting(sourceId);
        assertEquals(sourceId, connecting.sourceId);
        assertNull(connecting.phoneNumber);
        assertFalse(connecting.usePublicDid);
        testMessages(connecting);
    }

    @Test
    public void testConstructorWithSourceIdAndUsePublicDid() {
        Connecting connecting = new Connecting(sourceId, usePublicDid);
        assertEquals(sourceId, connecting.sourceId);
        assertNull(connecting.phoneNumber);
        assertEquals(usePublicDid, connecting.usePublicDid);
        testMessages(connecting);
    }

    @Test
    public void testConstructorWithSourceIdAndPhoneNumber() {
        Connecting connecting = new Connecting(sourceId, phoneNumber);
        assertEquals(sourceId, connecting.sourceId);
        assertEquals(phoneNumber, connecting.phoneNumber);
        assertFalse(connecting.usePublicDid);
        testMessages(connecting);
    }

    @Test
    public void testFullConstructor() {
        Connecting connecting = new Connecting(sourceId, phoneNumber, usePublicDid);
        assertEquals(sourceId, connecting.sourceId);
        assertEquals(phoneNumber, connecting.phoneNumber);
        assertEquals(usePublicDid, connecting.usePublicDid);
        testMessages(connecting);
    }

    private void testMessages(Connecting connecting) {
        JSONObject msg = connecting.messages.getJSONObject(Connecting.CREATE_CONNECTION);
        assertEquals(msg.getString("@type"), Connecting.getMessageType(Connecting.CREATE_CONNECTION));
        assertNotNull(msg.getString("@id"));
        assertEquals(msg.getJSONObject("connectionDetail").getString("sourceId"), connecting.sourceId);
        if(connecting.phoneNumber != null)
            assertEquals(msg.getJSONObject("connectionDetail").getString("phoneNo"), connecting.phoneNumber);
        assertEquals(msg.getJSONObject("connectionDetail").getBoolean("usePublicDid"), connecting.usePublicDid);
    }

    @Test
    public void testConnect() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            Connecting connecting = new Connecting(sourceId, phoneNumber, usePublicDid);
            connecting.disableHTTPSend();
            byte[] message = connecting.connect(context);
            JSONObject unpackedMessage = Util.unpackForwardMessage(context, message);
            assertEquals(Connecting.getMessageType(Connecting.CREATE_CONNECTION), unpackedMessage.getString("@type"));
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }
}
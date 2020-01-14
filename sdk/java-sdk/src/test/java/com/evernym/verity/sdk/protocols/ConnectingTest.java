package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.connecting.Connecting;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ConnectingTest {
    private String sourceId = "source_id";
    private String phoneNumber = "123-456-7891";
    private boolean includePublicDID = true;

    @Test
    public void testGetMessageType() {
        Connecting connecting = Connecting.newConnection("none");
        String msgName = "msg name";
        assertEquals(
            Util.getMessageType(
                    Util.EVERNYM_MSG_QUALIFIER,
                    "connecting",
                    "0.6",
                    msgName
            ),
            Util.getMessageType(connecting, msgName)
        );
    }

    @Test
    public void testConstructorWithSourceId() throws VerityException, IOException {
        Connecting connecting = Connecting.newConnection(sourceId);
        assertEquals(sourceId, connecting.sourceId());
        assertNull(connecting.phoneNumber());
        assertFalse(connecting.includePublicDID());
        testMessages(connecting);
    }

    @Test
    public void testConstructorWithSourceIdAndUsePublicDid() throws VerityException, IOException {
        Connecting connecting = Connecting.newConnection(sourceId, includePublicDID);
        assertEquals(sourceId, connecting.sourceId());
        assertNull(connecting.phoneNumber());
        assertEquals(includePublicDID, connecting.includePublicDID());
        testMessages(connecting);
    }

    @Test
    public void testConstructorWithSourceIdAndPhoneNumber() throws VerityException, IOException {
        Connecting connecting = Connecting.newConnection(sourceId, phoneNumber);
        assertEquals(sourceId, connecting.sourceId());
        assertEquals(phoneNumber, connecting.phoneNumber());
        assertFalse(connecting.includePublicDID());
        testMessages(connecting);
    }

    @Test
    public void testFullConstructor() throws VerityException, IOException {
        Connecting connecting = Connecting.newConnection(sourceId, phoneNumber, includePublicDID);
        assertEquals(phoneNumber, connecting.phoneNumber());
        assertEquals(includePublicDID, connecting.includePublicDID());
        testMessages(connecting);
    }

    @Test
    public void testConnectMsg() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            Connecting connecting = Connecting.newConnection(sourceId, phoneNumber, includePublicDID);
            byte[] message = connecting.connectMsgPacked(context);
            JSONObject expectedMessage = connecting.connectMsg(context);
            JSONObject unpackedMessage = Util.unpackForwardMessage(context, message);

            // Not ideal for comparing JSON objects but JSONObject don't provide a better way
            assertEquals(expectedMessage.toString(), unpackedMessage.toString());
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }

    @Test
    public void testStatusMsg() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            Connecting connecting = Connecting.newConnection(sourceId, phoneNumber, includePublicDID);

            byte [] statusMsg = connecting.statusMsgPacked(context);
            JSONObject expectedMessage = connecting.statusMsg(context);
            JSONObject unpackedStatusMessage = Util.unpackForwardMessage(context, statusMsg);

            // Not ideal for comparing JSON objects but JSONObject don't provide a better way
            assertEquals(expectedMessage.toString(), unpackedStatusMessage.toString());
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }

    private void testMessages(Connecting connecting) throws VerityException, IOException {
        Context context = TestHelpers.getContext();
        JSONObject msg = connecting.connectMsg(context);
        assertEquals(Util.getMessageType(connecting, Connecting.CREATE_CONNECTION), msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertEquals(msg.getString("sourceId"), connecting.sourceId());
        if(connecting.phoneNumber() != null)
            assertEquals(msg.getString("phoneNo"), connecting.phoneNumber());
        assertEquals(msg.getBoolean("includePublicDID"), connecting.includePublicDID());

        JSONObject statusMsg = connecting.statusMsg(context);
        assertEquals(Util.getMessageType(connecting, Connecting.GET_STATUS), statusMsg.getString("@type"));
        assertNotNull(statusMsg.getString("@id"));
        assertEquals(statusMsg.getString("sourceId"), connecting.sourceId());
    }
}
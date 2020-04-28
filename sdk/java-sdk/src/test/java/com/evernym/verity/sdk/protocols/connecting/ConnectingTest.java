package com.evernym.verity.sdk.protocols.connecting;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.TestUtil;
import com.evernym.verity.sdk.protocols.connecting.v0_6.ConnectingV0_6;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConnectingTest {
    private final String sourceId = "source_id";
    private final String phoneNumber = "123-456-7891";
    private final boolean includePublicDID = true;

    @Test
    public void testGetMessageType() {
        ConnectingV0_6 connecting = Connecting.v0_6("none");
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
    public void testConstructorWithSourceId() throws VerityException {
        ConnectingImplV0_6 connecting = (ConnectingImplV0_6) Connecting.v0_6(sourceId);
        assertEquals(sourceId, connecting.sourceId());
        assertNull(phoneNumber, connecting.phoneNumber());
        assertFalse(connecting.includePublicDID());
        testConnectMsg(connecting, null, false);
    }

    @Test
    public void testConstructorWithSourceIdAndUsePublicDid() throws VerityException {
        ConnectingImplV0_6 connecting = (ConnectingImplV0_6) Connecting.v0_6(sourceId, includePublicDID);
        assertEquals(sourceId, connecting.sourceId());
        assertNull(connecting.phoneNumber());
        assertEquals(includePublicDID, connecting.includePublicDID());
        testConnectMsg(connecting, null, includePublicDID);
    }

    @Test
    public void testConstructorWithSourceIdAndPhoneNumber() throws VerityException {
        ConnectingImplV0_6 connecting = (ConnectingImplV0_6) Connecting.v0_6(sourceId, phoneNumber);
        assertEquals(sourceId, connecting.sourceId());
        assertEquals(phoneNumber, connecting.phoneNumber());
        assertFalse(connecting.includePublicDID());
        testConnectMsg(connecting, phoneNumber,false);
    }

    @Test
    public void testFullConstructor() throws VerityException {
        ConnectingImplV0_6 connecting = (ConnectingImplV0_6) Connecting.v0_6(sourceId, phoneNumber, includePublicDID);
        assertEquals(phoneNumber, connecting.phoneNumber());
        assertEquals(includePublicDID, connecting.includePublicDID());
        testConnectMsg(connecting, phoneNumber, includePublicDID);
    }

    @Test
    public void testConnectMsgPacked() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            ConnectingV0_6 connecting = Connecting.v0_6(sourceId, phoneNumber, includePublicDID);
            byte[] message = connecting.connectMsgPacked(context);
            JSONObject expectedMessage = connecting.connectMsg(context);
            JSONObject unpackedMessage = Util.unpackForwardMessage(context, message);

            TestUtil.assertJsonObjectEqual(expectedMessage, unpackedMessage);
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }

    @Test
    public void testStatusMsgPacked() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            ConnectingV0_6 connecting = Connecting.v0_6(sourceId, phoneNumber, includePublicDID);

            byte [] statusMsg = connecting.statusMsgPacked(context);
            JSONObject expectedMessage = connecting.statusMsg(context);
            JSONObject unpackedMessage = Util.unpackForwardMessage(context, statusMsg);

            TestUtil.assertJsonObjectEqual(expectedMessage, unpackedMessage);
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }

    private void testConnectMsg(ConnectingV0_6 connecting, String phoneNumber, boolean includePublicDID) throws VerityException {
        Context context = TestHelpers.getContext();
        JSONObject msg = connecting.connectMsg(context);
        assertEquals(Util.getMessageType(connecting, ConnectingV0_6.CREATE_CONNECTION), msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertEquals(msg.getString("sourceId"), sourceId);
        if(phoneNumber != null)
            assertEquals(msg.getString("phoneNo"), phoneNumber);
        assertEquals(msg.getBoolean("includePublicDID"), includePublicDID);

        JSONObject statusMsg = connecting.statusMsg(context);
        assertEquals(Util.getMessageType(connecting, ConnectingV0_6.GET_STATUS), statusMsg.getString("@type"));
        assertNotNull(statusMsg.getString("@id"));
        assertEquals(statusMsg.getString("sourceId"), sourceId);
    }
}

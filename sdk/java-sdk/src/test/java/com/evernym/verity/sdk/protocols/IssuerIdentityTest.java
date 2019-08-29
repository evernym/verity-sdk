package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class IssuerIdentityTest {

    @Test
    public void testGetMessageType() {
        String msgName = "msg name";

        String expectedType = Util.getMessageType(
                IssuerIdentity.MSG_FAMILY,
                IssuerIdentity.MSG_FAMILY_VERSION,
                msgName);

        assertEquals(expectedType, IssuerIdentity.getMessageType(msgName));
    }

    @Test
    public void testConstructor() {
        IssuerIdentity p = new IssuerIdentity();
        testMessages(p);
    }

    private void testMessages(IssuerIdentity p) {
        JSONObject msg = p.messages.getJSONObject(IssuerIdentity.CREATE);
        assertEquals(IssuerIdentity.getMessageType("create"), msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
    }

    @Test
    public void testCreate() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            IssuerIdentity writeSchema = new IssuerIdentity();
            writeSchema.disableHTTPSend();
            byte[] message = writeSchema.create(context);
            JSONObject unpackedMessage = Util.unpackForwardMessage(context, message);
            assertEquals(IssuerIdentity.getMessageType(IssuerIdentity.CREATE), unpackedMessage.getString("@type"));
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }
}
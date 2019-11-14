package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class IssuerSetupTest {

    @Test
    public void testGetMessageType() {
        String msgName = "msg name";

        String expectedType = Util.getMessageType(
                Util.EVERNYM_MSG_QUALIFIER,
                IssuerSetup.MSG_FAMILY,
                IssuerSetup.MSG_FAMILY_VERSION,
                msgName);

        assertEquals(expectedType, IssuerSetup.getMessageType(msgName));
    }

    @Test
    public void testConstructor() {
        IssuerSetup p = new IssuerSetup();
        testMessages(p);
    }

    private void testMessages(IssuerSetup p) {
        JSONObject msg = p.messages.getJSONObject(IssuerSetup.CREATE);
        assertEquals(IssuerSetup.getMessageType("create"), msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
    }

    @Test
    public void testCreate() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            IssuerSetup writeSchema = new IssuerSetup();
            writeSchema.disableHTTPSend();
            byte[] message = writeSchema.create(context);
            JSONObject unpackedMessage = Util.unpackForwardMessage(context, message);
            assertEquals(IssuerSetup.getMessageType(IssuerSetup.CREATE), unpackedMessage.getString("@type"));
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }
}
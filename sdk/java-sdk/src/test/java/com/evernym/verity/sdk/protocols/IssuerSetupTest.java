package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.issuersetup.IssuerSetup;
import com.evernym.verity.sdk.protocols.issuersetup.v0_6.IssuerSetupV0_6;
import com.evernym.verity.sdk.protocols.questionanswer.CommittedAnswer;
import com.evernym.verity.sdk.protocols.questionanswer.v1_0.CommittedAnswerV1_0;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import static com.evernym.verity.sdk.TestHelpers.unpackForwardMessage;
import static org.junit.Assert.*;

public class IssuerSetupTest {

    @Test
    public void testGetMessageType() {
        String msgName = "msg name";

        IssuerSetupV0_6 t = IssuerSetup.v0_6();

        String expectedType = Util.getMessageType(
                Util.EVERNYM_MSG_QUALIFIER,
                t.family(),
                t.version(),
                msgName);

        assertEquals(expectedType, t.messageType(msgName));
    }

    @Test
    public void testGetThreadId() {
        IssuerSetupV0_6 testProtocol = IssuerSetup.v0_6();
        assertNotNull(testProtocol.getThreadId());
    }

    @Test
    public void testCreateMessages() throws VerityException {
        Context context = TestHelpers.getContext();
        IssuerSetupV0_6 p = IssuerSetup.v0_6();
        JSONObject msg = p.createMsg(context);
        assertEquals(
                "did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/create",
                msg.getString("@type")
        );
        assertNotNull(msg.getString("@id"));
    }

    @Test
    public void testCreate() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            IssuerSetupV0_6 testProtocol = IssuerSetup.v0_6();
            byte[] message = testProtocol.createMsgPacked(context);
            JSONObject unpackedMessage = unpackForwardMessage(context, message);
            assertEquals(
                    "did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/create",
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
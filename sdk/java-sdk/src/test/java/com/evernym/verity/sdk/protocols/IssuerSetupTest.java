package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.issuersetup.IssuerSetup;
import com.evernym.verity.sdk.protocols.issuersetup.v0_7.IssuerSetupV0_7;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static com.evernym.verity.sdk.TestHelpers.unpackForwardMessage;
import static org.junit.jupiter.api.Assertions.*;

public class IssuerSetupTest {

    @Test
    public void testGetMessageType() {
        String msgName = "msg name";

        IssuerSetupV0_7 t = IssuerSetup.v0_7();

        String expectedType = Util.getMessageType(
                Util.EVERNYM_MSG_QUALIFIER,
                t.family(),
                t.version(),
                msgName);

        assertEquals(expectedType, t.messageType(msgName));
    }

    @Test
    public void testGetThreadId() {
        IssuerSetupV0_7 testProtocol = IssuerSetup.v0_7();
        assertNotNull(testProtocol.getThreadId());
    }

    @Test
    public void testCreateMessages() throws VerityException {
        Context context = TestHelpers.getContext();
        IssuerSetupV0_7 p = IssuerSetup.v0_7();
        JSONObject msg = p.createMsg(context, "did:indy:sovrin:builder");
        assertEquals(
                Util.EVERNYM_MSG_QUALIFIER + "/issuer-setup/0.7/create",
                msg.getString("@type")
        );
        asserEquals(
                msg.getString('ledgerPrefix'),
                "did:indy:sovrin:builder"
        );
        assertNotNull(msg.getString("@id"));
    }

    @Test
    public void testCreateMessages() throws VerityException {
        Context context = TestHelpers.getContext();
        IssuerSetupV0_7 p = IssuerSetup.v0_7();
        JSONObject msg = p.createMsg(context, "did:indy:sovrin:builder", "someEndorser");
        assertEquals(
                Util.EVERNYM_MSG_QUALIFIER + "/issuer-setup/0.7/create",
                msg.getString("@type")
        );
        asserEquals(
                msg.getString('ledgerPrefix'),
                "did:indy:sovrin:builder"
        );
        asserEquals(
                msg.getString(endorser),
                "someEndorser"
        );
        assertNotNull(msg.getString("@id"));
    }

    @Test
    public void testCreate() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            IssuerSetupV0_7 testProtocol = IssuerSetup.v0_7();
            byte[] message = testProtocol.createMsgPacked(context, "did:indy:sovrin:builder");
            JSONObject unpackedMessage = unpackForwardMessage(context, message);
            assertEquals(
                    Util.EVERNYM_MSG_QUALIFIER + "/issuer-setup/0.7/create",
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
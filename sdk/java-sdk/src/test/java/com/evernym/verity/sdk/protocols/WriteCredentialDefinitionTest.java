package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.writecreddef.WriteCredentialDefinition;
import com.evernym.verity.sdk.protocols.writecreddef.v0_6.RevocationRegistryConfig;
import com.evernym.verity.sdk.protocols.writecreddef.v0_6.WriteCredentialDefinitionV0_6;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static com.evernym.verity.sdk.TestHelpers.unpackForwardMessage;
import static org.junit.jupiter.api.Assertions.*;

public class WriteCredentialDefinitionTest {

    private final String name = "cred def name";
    private final String schemaId = "...someSchemaId...";
    private final String tag = "latest";
    private final RevocationRegistryConfig revocationDetails = WriteCredentialDefinitionV0_6.disabledRegistryConfig();
    private final String endorserDID = "endorserDID";

    @Test
    public void testGetMessageType() {
        WriteCredentialDefinitionV0_6 writeCredDef = WriteCredentialDefinition.v0_6(name, schemaId, tag, revocationDetails);
        String msgName = "msg name";
        assertEquals(
                Util.getMessageType(Util.EVERNYM_MSG_QUALIFIER, writeCredDef.family(), writeCredDef.version(), msgName),
                writeCredDef.messageType(msgName)
        );
    }

    @Test
    public void testGetThreadId() {
        WriteCredentialDefinitionV0_6 testProtocol = WriteCredentialDefinition.v0_6(name, schemaId, tag, revocationDetails);
        assertNotNull(testProtocol.getThreadId());
    }

    @Test
    public void testConstructorWithNameAndSchemaId() throws WalletException, UndefinedContextException {
        Context context = TestHelpers.getContext();
        WriteCredentialDefinitionV0_6 testProtocol = WriteCredentialDefinition.v0_6(name, schemaId);
        JSONObject msg = testProtocol.writeMsg(context);
        assertEquals(name, msg.getString("name"));
        assertEquals(schemaId, msg.getString("schemaId"));
        assertNull(msg.opt("tag"));
        assertNull(msg.opt("revocationDetails"));
    }

    @Test
    public void testConstructorWithNameAndSchemaIdAndTag() throws UndefinedContextException, WalletException {
        Context context = TestHelpers.getContext();
        WriteCredentialDefinitionV0_6 testProtocol = WriteCredentialDefinition.v0_6(name, schemaId, tag);
        JSONObject msg = testProtocol.writeMsg(context);
        assertEquals(name, msg.getString("name"));
        assertEquals(schemaId, msg.getString("schemaId"));
        assertEquals(tag, msg.getString("tag"));
        assertNull(msg.opt("revocationDetails"));
    }

    @Test
    public void testConstructorWithNameAndSchemaIdAndRevDetails() throws UndefinedContextException, WalletException {
        Context context = TestHelpers.getContext();
        WriteCredentialDefinitionV0_6 testProtocol = WriteCredentialDefinition.v0_6(name, schemaId, revocationDetails);
        JSONObject msg = testProtocol.writeMsg(context);
        assertEquals(name, msg.getString("name"));
        assertEquals(schemaId, msg.getString("schemaId"));
        assertNull(msg.opt("tag"));
        assertNotNull(msg.get("revocationDetails"));
    }


    @Test
    public void testFullConstructor() throws UndefinedContextException, WalletException {
        Context context = TestHelpers.getContext();
        WriteCredentialDefinitionV0_6 testProtocol = WriteCredentialDefinition.v0_6(name, schemaId, tag, revocationDetails);
        JSONObject msg = testProtocol.writeMsg(context);
        assertEquals(name, msg.getString("name"));
        assertEquals(schemaId, msg.getString("schemaId"));
        assertEquals(tag, msg.getString("tag"));
        assertNotNull(msg.get("revocationDetails"));
    }

    @Test
    public void testWrite() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            WriteCredentialDefinitionV0_6 testProtocol = WriteCredentialDefinition.v0_6(name, schemaId, tag, revocationDetails);
            byte[] message = testProtocol.writeMsgPacked(context);
            JSONObject unpackedMessage = unpackForwardMessage(context, message);
            testBaseMessage(unpackedMessage);
            assertEquals(Util.EVERNYM_MSG_QUALIFIER + "/write-cred-def/0.6/write", unpackedMessage.getString("@type"));

            // test endorser DID
            byte[] message2 = testProtocol.writeMsgPacked(context, endorserDID);
            JSONObject unpackedMessage2 = unpackForwardMessage(context, message2);
            testMessageWithEndorser(unpackedMessage2);
            assertEquals(Util.EVERNYM_MSG_QUALIFIER + "/write-cred-def/0.6/write", unpackedMessage2.getString("@type"));
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }

    private void testBaseMessage(JSONObject msg) {
        assertNotNull(msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertNotNull(msg.getJSONObject("~thread").getString("thid"));
    }

    private void testMessageWithEndorser(JSONObject msg) {
        testBaseMessage(msg);
        assertEquals(endorserDID, msg.getString("endorserDID"));
    }
}
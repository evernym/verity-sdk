package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;

import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class WriteCredentialDefinitionTest {

    private String name = "cred def name";
    private String schemaId = "...someSchemaId...";
    private String tag = "latest";
    private JSONObject revocationDetails = new JSONObject();

    public WriteCredentialDefinitionTest() {
        revocationDetails.put("support_revocation", false);
    }

    @Test
    public void testGetMessageType() {
        WriteCredentialDefinition writeCredDef = new WriteCredentialDefinition(name, schemaId, tag, revocationDetails);
        String msgName = "msg name";
        assertEquals(Util.getMessageType("write-cred-def", "0.6", msgName), WriteCredentialDefinition.getMessageType(msgName));
    }

    @Test
    public void testConstructorWithNameAndSchemaId() {
        WriteCredentialDefinition writeCredDef = new WriteCredentialDefinition(name, schemaId);
        assertEquals(name, writeCredDef.name);
        assertEquals(schemaId, writeCredDef.schemaId);
        assertNull(writeCredDef.tag);
        assertNull(writeCredDef.revocationDetails);
        testMessages(writeCredDef);
    }

    @Test
    public void testConstructorWithNameAndSchemaIdAndTag() {
        WriteCredentialDefinition writeCredDef = new WriteCredentialDefinition(name, schemaId, tag);
        assertEquals(name, writeCredDef.name);
        assertEquals(schemaId, writeCredDef.schemaId);
        assertEquals(tag, writeCredDef.tag);
        assertNull(writeCredDef.revocationDetails);
        testMessages(writeCredDef);
    }

    @Test
    public void testConstructorWithNameAndSchemaIdAndRevDetails() {
        WriteCredentialDefinition writeCredDef = new WriteCredentialDefinition(name, schemaId, revocationDetails);
        assertEquals(name, writeCredDef.name);
        assertEquals(schemaId, writeCredDef.schemaId);
        assertNull(writeCredDef.tag);
        assertEquals(revocationDetails.toString(), writeCredDef.revocationDetails.toString());
        testMessages(writeCredDef);
    }


    @Test
    public void testFullConstructor() {
        WriteCredentialDefinition writeCredDef = new WriteCredentialDefinition(name, schemaId, tag, revocationDetails);
        assertEquals(name, writeCredDef.name);
        assertEquals(schemaId, writeCredDef.schemaId);
        assertEquals(tag, writeCredDef.tag);
        assertEquals(revocationDetails.toString(), writeCredDef.revocationDetails.toString());
        testMessages(writeCredDef);
    }

    private void testMessages(WriteCredentialDefinition writeCredDef) {
        JSONObject msg = writeCredDef.messages.getJSONObject(WriteCredentialDefinition.WRITE_CRED_DEF);
        assertEquals(WriteCredentialDefinition.getMessageType("write"), msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertEquals(name, msg.getString("name"));
        assertEquals(schemaId, msg.getString("schemaId"));
        if(writeCredDef.tag != null)
            assertEquals(tag, msg.getString("tag"));
        if(writeCredDef.revocationDetails != null)
            assertEquals(revocationDetails.toString(), msg.getJSONObject("revocationDetails").toString());
    }

    @Test
    public void testWrite() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            WriteCredentialDefinition writeCredDef = new WriteCredentialDefinition(name, schemaId, tag, revocationDetails);
            writeCredDef.disableHTTPSend();
            byte[] message = writeCredDef.write(context);
            JSONObject unpackedMessage = Util.unpackForwardMessage(context, message);
            assertEquals(WriteCredentialDefinition.getMessageType(WriteCredentialDefinition.WRITE_CRED_DEF), unpackedMessage.getString("@type"));
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }
}
package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.writeschema.WriteSchema;
import com.evernym.verity.sdk.protocols.writeschema.v0_6.WriteSchemaV0_6;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import static com.evernym.verity.sdk.TestHelpers.unpackForwardMessage;
import static org.junit.Assert.*;

public class WriteSchemaTest {

    private final String schemaName = "test schema";
    private final String schemaVersion = "0.0.1";
    private final String attr1 = "name";
    private final String attr2 = "degree";

    @Test
    public void testGetMessageType() {
        WriteSchemaV0_6 testProtocol = WriteSchema.v0_6(schemaName, schemaVersion, attr1);
        String msgName = "msg name";
        assertEquals(
                Util.getMessageType(Util.EVERNYM_MSG_QUALIFIER, testProtocol.family(), testProtocol.version(), msgName),
                testProtocol.messageType(msgName));
    }

    @Test
    public void testConstructor() throws VerityException {
        Context context = TestHelpers.getContext();
        WriteSchemaV0_6 writeSchema = WriteSchema.v0_6(schemaName, schemaVersion, attr1, attr2);
        JSONObject msg = writeSchema.writeMsg(context);

        assertEquals(schemaName, msg.get("name"));
        assertEquals(schemaVersion, msg.get("version"));
        assertArrayEquals(new String[]{attr1, attr2}, msg.getJSONArray("attrNames").toList().toArray());
    }

//    private void testMessages(WriteSchema writeSchema) {
//        JSONObject msg = writeSchema.messages.getJSONObject(WriteSchema.WRITE_SCHEMA);
//        assertEquals(WriteSchema.getMessageType("write"), msg.getString("@type"));
//        assertNotNull(msg.getString("@id"));
//        assertEquals(schemaName, msg.getString("name"));
//        assertEquals(schemaVersion, msg.getString("version"));
//        assertEquals(attr1, msg.getJSONArray("attrNames").getString(0));
//        assertEquals(attr2, msg.getJSONArray("attrNames").getString(1));
//
//    }

    @Test
    public void testWrite() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            WriteSchemaV0_6 testProtocol = WriteSchema.v0_6(schemaName, schemaVersion, attr1, attr2);
            byte[] message = testProtocol.writeMsgPacked(context);
            JSONObject unpackedMessage = unpackForwardMessage(context, message);
            assertEquals(
                    "did:sov:123456789abcdefghi1234;spec/write-schema/0.6/write",
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
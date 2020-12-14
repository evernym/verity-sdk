package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.basicmessage.BasicMessage;
import com.evernym.verity.sdk.protocols.basicmessage.v1_0.BasicMessageV1_0;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import static com.evernym.verity.sdk.TestHelpers.unpackForwardMessage;
import static org.junit.Assert.*;

public class BasicMessageTest {

    private final String forRelationship = "abcd12345";
    private final String content = "message text";
    private final String sent_time = "2018-12-13T17:29:34+0000";
    private final String localization = "en";

    @Test
    public void testGetMessageType() {
        BasicMessageV1_0 testProtocol = BasicMessage.v1_0(
                forRelationship,
                content,
                sent_time,
                localization);

        String msgName = "msg name";
        String msgType = Util.getMessageType(
                Util.COMMUNITY_MSG_QUALIFIER,
                testProtocol.family(),
                testProtocol.version(),
                msgName);
        assertEquals(msgType, testProtocol.messageType(msgName));
    }

    @Test
    public void testGetThreadId() {
        BasicMessageV1_0 testProtocol = BasicMessage.v1_0(
                forRelationship,
                content,
                sent_time,
                localization);
        assertNotNull(testProtocol.getThreadId());
    }

    @Test
    public void testConstructor() throws VerityException {
        Context context = TestHelpers.getContext();
        BasicMessageV1_0 testProtocol = BasicMessage.v1_0(
                forRelationship,
                content,
                sent_time,
                localization
        );
        JSONObject msg = testProtocol.messageMsg(context);
        testMessageMessages(msg);
    }

    private void testMessageMessages(JSONObject msg) {
        testBaseMessage(msg);
        assertEquals(forRelationship, msg.getString("~for_relationship"));
        assertEquals(content, msg.getString("content"));
        assertEquals(sent_time, msg.getString("sent_time"));
        assertEquals(localization, msg.getJSONObject("~l10n").getString("locale"));
    }

    @Test
    public void testMessage() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            BasicMessageV1_0 testProtocol = BasicMessage.v1_0(
                    forRelationship,
                    content,
                    sent_time,
                    localization);

            byte[] message = testProtocol.messageMsgPacked(context);
            JSONObject unpackedMessage = unpackForwardMessage(context, message);
            testBaseMessage(unpackedMessage);
            assertEquals(
                    "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/basicmessage/1.0/send-message",
                    unpackedMessage.getString("@type")
            );
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
}
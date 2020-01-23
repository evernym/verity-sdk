package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.questionanswer.CommittedAnswer;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommittedAnswerTest {

    private String forRelationship = "abcd12345";
    private String questionText = "Question text";
    private String questionDetail = "Optional question detail";
    private String[] validResponses = {"Yes", "No"};
    private boolean requireSignature = true;

    @Test
    public void testGetMessageType() {
        CommittedAnswer testProtocol = CommittedAnswer.v1_0(
                forRelationship,
                questionText,
                questionDetail,
                validResponses,
                requireSignature);

        String msgName = "msg name";
        String msgType = Util.getMessageType(
                Util.COMMUNITY_MSG_QUALIFIER,
                testProtocol.family(),
                testProtocol.version(),
                msgName);
        assertEquals(msgType, testProtocol.getMessageType(msgName));
    }

    @Test
    public void testConstructor() throws VerityException {
        Context context = TestHelpers.getContext();
        CommittedAnswer testProtocol = CommittedAnswer.v1_0(
                forRelationship,
                questionText,
                questionDetail,
                validResponses,
                requireSignature
        );
        JSONObject msg = testProtocol.askMsg(context);
        testAskMessages(msg);
    }

    private void testAskMessages(JSONObject msg) {

        assertNotNull(msg.getString("@id"));
        assertEquals(forRelationship, msg.getString("~for_relationship"));
        assertEquals(questionText, msg.getString("text"));
        assertEquals(questionDetail, msg.getString("detail"));
        assertEquals(validResponses[0], msg.getJSONArray("valid_responses").getString(0));
        assertEquals(requireSignature, msg.getBoolean("signature_required"));
        assertNotNull(msg.getJSONArray("valid_responses"));
    }

    private void testStatusMessage(JSONObject msg) {
        assertNotNull(msg.getString("@id"));
        assertNotNull(msg.getJSONObject("~thread").getString("thid"));
    }

    @Test
    public void testAsk() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            CommittedAnswer testProtocol = CommittedAnswer.v1_0(
                    forRelationship,
                    questionText,
                    questionDetail,
                    validResponses,
                    requireSignature);

            byte[] message = testProtocol.askMsgPacked(context);
            JSONObject unpackedMessage = Util.unpackForwardMessage(context, message);
            assertEquals(
                    "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/committedanswer/1.0/ask-question",
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
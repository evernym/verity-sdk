package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class QuestionAnswerTest {

    private String connectionId = "abcd12345";
    private String questionText = "Question text";
    private String questionDetail = "Optional question detail";
    private String[] validResponses = {"Yes", "No"};

    @Test
    public void testGetMessageType() {
        QuestionAnswer questionAnswer = new QuestionAnswer(connectionId, questionText, questionDetail, validResponses);
        String msgName = "msg name";
        assertEquals(Util.getMessageType("questionanswer", "1.0", msgName), QuestionAnswer.getMessageType(msgName));
    }

    @Test
    public void testConstructor() {
        QuestionAnswer questionAnswer = new QuestionAnswer(connectionId, questionText, questionDetail, validResponses);
        assertEquals(connectionId, questionAnswer.forRelationship);
        assertEquals(questionText, questionAnswer.questionText);
        assertEquals(questionDetail, questionAnswer.questionDetail);
        assertEquals(validResponses.length, questionAnswer.validResponses.length);
        testMessages(questionAnswer);
    }

    private void testMessages(QuestionAnswer questionAnswer) {
        JSONObject msg = questionAnswer.messages.getJSONObject(QuestionAnswer.ASK_QUESTION);

        msg = new JSONObject(msg.toString());

        assertEquals(QuestionAnswer.getMessageType("ask-question"), msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertEquals(connectionId, msg.getString("~for_relationship"));
        assertEquals(questionText, msg.getString("text"));
        assertEquals(questionDetail, msg.getString("detail"));
        assertEquals(validResponses[0], msg.getJSONArray("valid_responses").getString(0));
        assertNotNull(msg.getJSONArray("valid_responses"));
    }

    @Test
    public void testAsk() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            QuestionAnswer questionAnswer = new QuestionAnswer(connectionId, questionText, questionDetail, validResponses);
            questionAnswer.disableHTTPSend();
            byte[] message = questionAnswer.ask(context);
            JSONObject unpackedMessage = Util.unpackForwardMessage(context, message);
            assertEquals(QuestionAnswer.getMessageType(QuestionAnswer.ASK_QUESTION), unpackedMessage.getString("@type"));
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }
}
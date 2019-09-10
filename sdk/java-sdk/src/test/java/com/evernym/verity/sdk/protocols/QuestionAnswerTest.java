package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;

import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class QuestionAnswerTest {

    private String connectionId = "abcd12345";
    private String notificationTitle = "Challenge Question";
    private String questionText = "Question text";
    private String questionDetail = "Optional question detail";
    private String[] validResponses = {"Yes", "No"};

    @Test
    public void testGetMessageType() {
        QuestionAnswer questionAnswer = new QuestionAnswer(connectionId, notificationTitle, questionText, questionDetail, validResponses);
        String msgName = "msg name";
        assertEquals(Util.getMessageType("question-answer", "1.0", msgName), QuestionAnswer.getMessageType(msgName));
    }

    @Test
    public void testConstructor() {
        QuestionAnswer questionAnswer = new QuestionAnswer(connectionId, notificationTitle, questionText, questionDetail, validResponses);
        assertEquals(connectionId, questionAnswer.connectionId);
        assertEquals(notificationTitle, questionAnswer.notificationTitle);
        assertEquals(questionText, questionAnswer.questionText);
        assertEquals(questionDetail, questionAnswer.questionDetail);
        assertEquals(validResponses[0], questionAnswer.validResponses.getJSONObject(0).getString("text"));
        assertNotNull(questionAnswer.validResponses.getJSONObject(0).getString("nonce"));
        assertEquals(validResponses[1], questionAnswer.validResponses.getJSONObject(1).getString("text"));
        assertNotNull(questionAnswer.validResponses.getJSONObject(1).getString("nonce"));
        testMessages(questionAnswer);
    }

    private void testMessages(QuestionAnswer questionAnswer) {
        JSONObject msg = questionAnswer.messages.getJSONObject(QuestionAnswer.QUESTION);
        assertEquals(QuestionAnswer.getMessageType("question"), msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertEquals(connectionId, msg.getString("connectionId"));
        assertEquals(notificationTitle, msg.getJSONObject("question").getString("notification_title"));
        assertEquals(questionText, msg.getJSONObject("question").getString("question_text"));
        assertEquals(questionDetail, msg.getJSONObject("question").getString("question_detail"));
        assertEquals(validResponses[0], msg.getJSONObject("question").getJSONArray("valid_responses").getJSONObject(0).getString("text"));
        assertNotNull(msg.getJSONObject("question").getJSONArray("valid_responses").getJSONObject(0).getString("nonce"));
        assertEquals(validResponses[1], msg.getJSONObject("question").getJSONArray("valid_responses").getJSONObject(1).getString("text"));
        assertNotNull(msg.getJSONObject("question").getJSONArray("valid_responses").getJSONObject(1).getString("nonce"));
    }

    @Test
    public void testAsk() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            QuestionAnswer questionAnswer = new QuestionAnswer(connectionId, notificationTitle, questionText, questionDetail, validResponses);
            questionAnswer.disableHTTPSend();
            byte[] message = questionAnswer.ask(context);
            JSONObject unpackedMessage = Util.unpackForwardMessage(context, message);
            assertEquals(QuestionAnswer.getMessageType(QuestionAnswer.QUESTION), unpackedMessage.getString("@type"));
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }
}
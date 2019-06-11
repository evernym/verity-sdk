package com.evernym.verity.sdk.protocols;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;

import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;
import org.junit.Test;

public class QuestionTest {

    @Test
    public void properlyBuildMessage() throws Exception {
        try {
            Context context = TestHelpers.getConfig();
            
            String connectionId = "abcd12345";
            String notificationTitle = "Challenge Question";
            String questionText = "Question text";
            String questionDetail = "Optional question detail";
            String[] validResponses = {"Yes", "No"};
            Question provableQuestion = new Question(connectionId, notificationTitle, questionText, questionDetail, validResponses);
            JSONObject unpackedMessage = TestHelpers.unpackMessage(context, provableQuestion.getMessage(context));
            assertEquals(provableQuestion.toString(), unpackedMessage.toString());
            assertEquals(connectionId, unpackedMessage.getString("connectionId"));
            assertEquals(notificationTitle, unpackedMessage.getJSONObject("question").getString("notification_title"));
            assertEquals(questionText, unpackedMessage.getJSONObject("question").getString("question_text"));
            assertEquals(questionDetail, unpackedMessage.getJSONObject("question").getString("question_detail"));
            assertEquals(validResponses[0], unpackedMessage.getJSONObject("question").getJSONArray("valid_responses").getJSONObject(0).getString("text"));
            assertNotNull(unpackedMessage.getJSONObject("question").getJSONArray("valid_responses").getJSONObject(0).getString("nonce"));
            assertEquals(validResponses[1], unpackedMessage.getJSONObject("question").getJSONArray("valid_responses").getJSONObject(1).getString("text"));
            assertNotNull(unpackedMessage.getJSONObject("question").getJSONArray("valid_responses").getJSONObject(1).getString("nonce"));

            context.closeWallet();
        } catch(Exception e) {
            e.printStackTrace();
            assertTrue(false);
        } finally {
            String walletConfig = new JSONObject().put("id", "java_test_wallet").toString();
            String walletCredentials = new JSONObject().put("key", "12345").toString();
            Wallet.deleteWallet(walletConfig, walletCredentials).get();
        }
    }
}
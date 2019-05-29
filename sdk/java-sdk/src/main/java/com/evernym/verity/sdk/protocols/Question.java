package com.evernym.verity.sdk.protocols;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.utils.VerityConfig;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3;
import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.indy.sdk.IndyException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Builds and sends a new encrypted agent message for the Question protocol.
 */
public class Question extends Protocol {

    // Message Type Definitions
    public static String ASK_QUESTION_MESSAGE_TYPE = "vs.service/question/0.1/question";
    public static String PROBLEM_REPORT_MESSAGE_TYPE = "vs.service/question/0.1/problem-report";
    public static String STATUS_MESSAGE_TYPE = "vs.service/question/0.1/status";

    // Status Definitions
    public static Integer QUESTION_SENT_STATUS = 0;
    public static Integer QUESTION_ANSWERED_STATUS = 1;

    private String connectionId;
    private String notificationTitle;
    private String questionText;
    private String questionDetail;
    private JSONArray validResponses;

    /**
     * Create a new Question object
     * @param connectionId the pairwise DID of the connection you want to send the question to
     * @param notificationTitle the title of the push notification (currently only rendered in Connect.Me when questionDetail is omitted)
     * @param questionText The main text of the question (included in the message the Connect.Me user signs with their private key)
     * @param questionDetail Any additional information about the question
     * @param validResponses The possible responses. See the Verity Protocol documentation for more information on how Connect.Me will render these options.
     * @throws UnsupportedEncodingException when the encoding of the question_text and valid_responses are not encoded in a a supported format (utf-8 recommended)
     */
    public Question(String connectionId, String notificationTitle, String questionText, String questionDetail,
            String[] validResponses) throws UnsupportedEncodingException {
        super();
        this.connectionId = connectionId;
        this.notificationTitle = notificationTitle;
        this.questionText = questionText;
        this.questionDetail = questionDetail;
        this.validResponses = new JSONArray();
        for (String validResponseString : validResponses) {
            JSONObject validResponse = new JSONObject();
            validResponse.put("text", validResponseString);
            validResponse.put("nonce", getHashedMessage(this.questionText, validResponseString));
            this.validResponses.put(validResponse);
        }
    }

    private String getHashedMessage(String questionText, String responseOption) throws UnsupportedEncodingException {
        DigestSHA3 sha3256 = new SHA3.Digest256();
        sha3256.update(questionText.getBytes("UTF-8"));
        sha3256.update(responseOption.getBytes("UTF-8"));
        sha3256.update(UUID.randomUUID().toString().getBytes("UTF-8"));
        return Hex.toHexString(sha3256.digest());
    }

    /**
     * Returns the JSON string of the Question message
     */
    @Override
    public String toString() {
        JSONObject message = new JSONObject();
        message.put("@type", Question.ASK_QUESTION_MESSAGE_TYPE);
        message.put("@id", this.id);
        message.put("connectionId", this.connectionId);
        JSONObject question = new JSONObject();
        question.put("notification_title", this.notificationTitle);
        question.put("question_text", this.questionText);
        question.put("question_detail", this.questionDetail);
        question.put("valid_responses", this.validResponses);
        message.put("question", question);
        return message.toString();
    }

    /**
     * Sends the question message to Verity
     * @param verityConfig an instance of VerityConfig configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public void ask(VerityConfig verityConfig) throws IOException, InterruptedException, ExecutionException, IndyException {
        this.sendMessage(verityConfig);
    }
}
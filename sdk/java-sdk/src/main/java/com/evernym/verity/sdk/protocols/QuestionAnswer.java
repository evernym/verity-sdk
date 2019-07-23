package com.evernym.verity.sdk.protocols;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.utils.Context;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3;
import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.indy.sdk.IndyException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Builds and sends a new encrypted agent message for the Question protocol.
 */
public class QuestionAnswer extends Protocol {

    private static String MSG_FAMILY = "question-answer";
    private static String MSG_FAMILY_VERSION = "0.1";

    // Messages
    @SuppressWarnings("WeakerAccess")
    public static String QUESTION = "question";

    // Status Definitions
    public static Integer QUESTION_SENT_STATUS = 0;
    public static Integer QUESTION_ANSWERED_STATUS = 1;

    String connectionId;
    String notificationTitle;
    String questionText;
    String questionDetail;
    JSONArray validResponses;

    /**
     * Create a new Question object
     * @param connectionId the pairwise DID of the connection you want to send the question to
     * @param notificationTitle the title of the push notification (currently only rendered in Connect.Me when questionDetail is omitted)
     * @param questionText The main text of the question (included in the message the Connect.Me user signs with their private key)
     * @param questionDetail Any additional information about the question
     * @param validResponses The possible responses. See the Verity Protocol documentation for more information on how Connect.Me will render these options.
     */
    @SuppressWarnings("WeakerAccess")
    public QuestionAnswer(String connectionId, String notificationTitle, String questionText, String questionDetail,
            String[] validResponses) {
        super(MSG_FAMILY, MSG_FAMILY_VERSION);
        this.connectionId = connectionId;
        this.notificationTitle = notificationTitle;
        this.questionText = questionText;
        this.questionDetail = questionDetail;
        this.validResponses = formatValidResponses(questionText, validResponses);

        defineMessages();
    }

    private JSONArray formatValidResponses(String questionText, String[] validResponses) {
        JSONArray formattedValidResponses = new JSONArray();
        for (String validResponseString : validResponses) {
            JSONObject validResponse = new JSONObject();
            validResponse.put("text", validResponseString);
            validResponse.put("nonce", getHashedMessage(questionText, validResponseString));
            formattedValidResponses.put(validResponse);
        }
        return formattedValidResponses;
    }

    @Override
    protected void defineMessages() {
        JSONObject message = new JSONObject();
        message.put("@type", this.getMessageType(QuestionAnswer.QUESTION));
        message.put("@id", QuestionAnswer.getNewId());
        message.put("connectionId", this.connectionId);
            JSONObject question = new JSONObject();
            question.put("notification_title", this.notificationTitle);
            question.put("question_text", this.questionText);
            question.put("question_detail", this.questionDetail);
            question.put("valid_responses", this.validResponses);
            message.put("question", question);
        this.messages.put(QuestionAnswer.QUESTION, message);
    }

    private String getHashedMessage(String questionText, String responseOption) {
        DigestSHA3 sha3256 = new SHA3.Digest256();
        sha3256.update(questionText.getBytes(StandardCharsets.UTF_8));
        sha3256.update(responseOption.getBytes(StandardCharsets.UTF_8));
        sha3256.update(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        return Hex.toHexString(sha3256.digest());
    }


    /**
     * Sends the question message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    @SuppressWarnings("WeakerAccess")
    public byte[] ask(Context context) throws IOException, InterruptedException, ExecutionException, IndyException {
        return this.send(context, this.messages.getJSONObject(QuestionAnswer.QUESTION));
    }
}
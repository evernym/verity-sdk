package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Builds and sends a new encrypted agent message for the Question protocol.
 */
public class QuestionAnswer extends Protocol {

    final private static String MSG_FAMILY = "questionanswer";
    final private static String MSG_FAMILY_VERSION = "1.0";

    // Messages
    @SuppressWarnings("WeakerAccess")
    public static String ASK_QUESTION = "ask-question";

    // Status Definitions
    public static Integer QUESTION_SENT_STATUS = 0;
    public static Integer QUESTION_ANSWERED_STATUS = 1;

    String forRelationship;
    String questionText;
    String questionDetail;
    String[] validResponses;

    /**
     * Create a new Question object
     * @param forRelationship the owning pairwise DID of the relationship you want to send the question to
     * @param questionText The main text of the question (included in the message the Connect.Me user signs with their private key)
     * @param questionDetail Any additional information about the question
     * @param validResponses The possible responses. See the Verity Protocol documentation for more information on how Connect.Me will render these options.
     */
    @SuppressWarnings("WeakerAccess")
    public QuestionAnswer(String forRelationship, String questionText, String questionDetail,
                          String[] validResponses) {
        super();
        this.forRelationship = forRelationship;
        this.questionText = questionText;
        this.questionDetail = questionDetail;
        this.validResponses = validResponses;

        defineMessages();
    }

    public static String getMessageType(String msgName) {
        return Util.getMessageType(QuestionAnswer.MSG_FAMILY, QuestionAnswer.MSG_FAMILY_VERSION, msgName);
    }

    public static String getProblemReportMessageType() {
        return Util.getProblemReportMessageType(QuestionAnswer.MSG_FAMILY, QuestionAnswer.MSG_FAMILY_VERSION);
    }

    public static String getStatusMessageType() {
        return Util.getStatusMessageType(QuestionAnswer.MSG_FAMILY, QuestionAnswer.MSG_FAMILY_VERSION);
    }

    @Override
    protected void defineMessages() {
        JSONObject message = new JSONObject();
        message.put("@type", QuestionAnswer.getMessageType(QuestionAnswer.ASK_QUESTION));
        message.put("@id", QuestionAnswer.getNewId());
        addThread(message);
        message.put("~for_relationship", this.forRelationship);
        message.put("text", this.questionText);
        message.put("detail", this.questionDetail);
        message.put("valid_responses", this.validResponses);
        this.messages.put(QuestionAnswer.ASK_QUESTION, message);
    }

    /**
     * Sends the question message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    @SuppressWarnings("WeakerAccess")
    public byte[] ask(Context context) throws IOException, UndefinedContextException, WalletException {
        return this.send(context, this.messages.getJSONObject(QuestionAnswer.ASK_QUESTION));
    }
}
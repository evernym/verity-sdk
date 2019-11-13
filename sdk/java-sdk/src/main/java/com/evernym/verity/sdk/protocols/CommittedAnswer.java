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
public class CommittedAnswer extends Protocol {
    final private static String MSG_QUALIFIER = Util.COMMUNITY_MSG_QUALIFIER;
    final private static String MSG_FAMILY = "committedanswer";
    final private static String MSG_FAMILY_VERSION = "1.0";

    // Messages
    @SuppressWarnings("WeakerAccess")
    public static String ASK_QUESTION = "ask-question";
    public static String GET_STATUS = "get-status";

    // Status Definitions
    public static Integer QUESTION_SENT_STATUS = 0;
    public static Integer QUESTION_ANSWERED_STATUS = 1;

    String forRelationship;
    String questionText;
    String questionDetail;
    String[] validResponses;
    private boolean signatureRequired;

    /**
     * Create a new Question object
     * @param forRelationship the owning pairwise DID of the relationship you want to send the question to
     * @param questionText The main text of the question (included in the message the Connect.Me user signs with their private key)
     * @param questionDetail Any additional information about the question
     * @param validResponses The possible responses. See the Verity Protocol documentation for more information on how Connect.Me will render these options.
     */
    @SuppressWarnings("WeakerAccess")
    public CommittedAnswer(String forRelationship,
                          String questionText,
                          String questionDetail,
                          String[] validResponses) {
        this(forRelationship, questionText, questionDetail, validResponses, true);
    }

    @SuppressWarnings("WeakerAccess")
    public CommittedAnswer(String forRelationship,
                          String questionText,
                          String questionDetail,
                          String[] validResponses,
                          Boolean signatureRequired
    ) {
        super();
        this.forRelationship = forRelationship;
        this.questionText = questionText;
        this.questionDetail = questionDetail;
        this.validResponses = validResponses;
        this.signatureRequired = signatureRequired;

        defineMessages();
    }

    public static String getMessageType(String msgName) {
        return Util.getMessageType(MSG_QUALIFIER, MSG_FAMILY, MSG_FAMILY_VERSION, msgName);
    }

    public static String getProblemReportMessageType() {
        return Util.getProblemReportMessageType(MSG_QUALIFIER, MSG_FAMILY, MSG_FAMILY_VERSION);
    }

    public static String getStatusMessageType() {
        return Util.getStatusMessageType(MSG_QUALIFIER, MSG_FAMILY, MSG_FAMILY_VERSION);
    }

    @Override
    protected void defineMessages() {
        JSONObject message = new JSONObject();
        message.put("@type", CommittedAnswer.getMessageType(ASK_QUESTION));
        message.put("@id", CommittedAnswer.getNewId());
        addThread(message);
        message.put("~for_relationship", this.forRelationship);
        message.put("text", this.questionText);
        message.put("detail", this.questionDetail);
        message.put("valid_responses", this.validResponses);
        message.put("signature_required", this.signatureRequired);
        this.messages.put(ASK_QUESTION, message);

        JSONObject statusMsg = new JSONObject();
        statusMsg.put("@type", CommittedAnswer.getMessageType(GET_STATUS));
        statusMsg.put("@id", CommittedAnswer.getNewId());
        addThread(statusMsg);
        statusMsg.put("~for_relationship", this.forRelationship);
        this.messages.put(GET_STATUS, statusMsg);
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
        return this.send(context, this.messages.getJSONObject(ASK_QUESTION));
    }

    /**
     * Sends the status request message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    public byte[] status(Context context) throws IOException, UndefinedContextException, WalletException {
        return this.send(context, this.messages.getJSONObject(GET_STATUS));
    }
}
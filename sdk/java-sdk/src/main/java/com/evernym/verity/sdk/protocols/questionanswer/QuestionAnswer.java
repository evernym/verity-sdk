package com.evernym.verity.sdk.protocols.questionanswer;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Builds and sends a new encrypted agent message for the Question protocol.
 */
public interface QuestionAnswer extends MessageFamily {
    default String qualifier() {return Util.COMMUNITY_MSG_QUALIFIER;}
    default String family() {return "questionanswer";};
    default String version() {return "1.0";}

    /**
     * Create a new Question object
     * @param forRelationship the owning pairwise DID of the relationship you want to send the question to
     * @param questionText The main text of the question (included in the message the Connect.Me user signs with their private key)
     * @param questionDetail Any additional information about the question
     * @param validResponses The possible responses. See the Verity Protocol documentation for more information on how Connect.Me will render these options.
     */
    static QuestionAnswer v1_0(String forRelationship,
                               String questionText,
                               String questionDetail,
                               String[] validResponses,
                               Boolean signatureRequired) {
        return new QuestionAnswerImpl(forRelationship, questionText, questionDetail, validResponses, signatureRequired);
    }

    static QuestionAnswer v1_0(String forRelationship,
                                String threadId,
                                String answer) {
        return new QuestionAnswerImpl(forRelationship, threadId, answer);
    }

    /**
     * Sends the question message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    void ask(Context context) throws IOException, VerityException;

    JSONObject askMsg(Context context) throws VerityException;

    byte[] askMsgPacked(Context context) throws VerityException;


    void answer(Context context) throws IOException, VerityException;

    JSONObject answerMsg(Context context) throws VerityException;

    byte[] answerMsgPacked(Context context) throws VerityException;

    /**
     * Sends the status request message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    void status(Context context) throws IOException, VerityException;

    JSONObject statusMsg(Context context) throws VerityException;

    byte[] statusMsgPacked(Context context) throws VerityException;

}
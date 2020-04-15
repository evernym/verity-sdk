package com.evernym.verity.sdk.protocols.questionanswer;

import com.evernym.verity.sdk.protocols.questionanswer.v1_0.QuestionAnswerV1_0;

public class QuestionAnswer {
    /**
     * Create a new Question object
     * @param forRelationship the owning pairwise DID of the relationship you want to send the question to
     * @param questionText The main text of the question (included in the message the Connect.Me user signs with their private key)
     * @param questionDetail Any additional information about the question
     * @param validResponses The possible responses. See the Verity Protocol documentation for more information on how Connect.Me will render these options.
     */
    public static QuestionAnswerV1_0 v1_0(String forRelationship,
                                   String questionText,
                                   String questionDetail,
                                   String[] validResponses,
                                   Boolean signatureRequired) {
        return new QuestionAnswerImplV1_0(forRelationship, questionText, questionDetail, validResponses, signatureRequired);
    }

    public static QuestionAnswerV1_0 v1_0(String forRelationship,
                                   String threadId,
                                   String answer) {
        return new QuestionAnswerImplV1_0(forRelationship, threadId, answer);
    }
}

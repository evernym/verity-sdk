package com.evernym.verity.sdk.protocols.questionanswer;

/**
 * Builds and sends a new encrypted agent message for the Question protocol.
 */
public class QuestionAnswerImpl extends AskCommonImpl implements QuestionAnswer {

    public QuestionAnswerImpl(String forRelationship,
                              String questionText,
                              String questionDetail,
                              String[] validResponses,
                              Boolean signatureRequired) {
        super(forRelationship, questionText, questionDetail, validResponses, signatureRequired);
    }

    public QuestionAnswerImpl(String forRelationship,
                              String threadId,
                              String answer) {
        super(forRelationship, threadId, answer);
    }

    public QuestionAnswerImpl(String forRelationship, String threadId) {
        super(forRelationship, threadId);
    }
}
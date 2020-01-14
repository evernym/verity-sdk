package com.evernym.verity.sdk.protocols.questionanswer;

/**
 * Builds and sends a new encrypted agent message for the Question protocol.
 */
public class QuestionAnswerImpl extends AskCommonImpl implements QuestionAnswer {

    public QuestionAnswerImpl(String forRelationship,
                              String questionText,
                              String questionDetail,
                              String[] validResponses) {
        super(forRelationship, questionText, questionDetail, validResponses);
    }

    public QuestionAnswerImpl(String forRelationship,
                              String questionText,
                              String questionDetail,
                              String[] validResponses,
                              Boolean signatureRequired) {
        super(forRelationship, questionText, questionDetail, validResponses, signatureRequired);
    }
}
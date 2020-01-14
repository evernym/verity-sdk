package com.evernym.verity.sdk.protocols.questionanswer;

/**
 * Builds and sends a new encrypted agent message for the Question protocol.
 */
public class CommittedAnswerImpl extends AskCommonImpl implements CommittedAnswer {

    public CommittedAnswerImpl(String forRelationship,
                               String questionText,
                               String questionDetail,
                               String[] validResponses) {
        super(forRelationship, questionText, questionDetail, validResponses);
    }

    public CommittedAnswerImpl(String forRelationship,
                               String questionText,
                               String questionDetail,
                               String[] validResponses,
                               Boolean signatureRequired) {
        super(forRelationship, questionText, questionDetail, validResponses, signatureRequired);
    }
}
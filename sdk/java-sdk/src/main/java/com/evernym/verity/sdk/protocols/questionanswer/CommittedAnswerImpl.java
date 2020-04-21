package com.evernym.verity.sdk.protocols.questionanswer;

/**
 * Builds and sends a new encrypted agent message for the Question protocol.
 */
public class CommittedAnswerImpl extends AskCommonImpl implements CommittedAnswer {

    public CommittedAnswerImpl(String forRelationship,
                               String questionText,
                               String questionDetail,
                               String[] validResponses,
                               Boolean signatureRequired) {
        super(forRelationship, questionText, questionDetail, validResponses, signatureRequired);
    }

    public CommittedAnswerImpl(String forRelationship, String threadId, String answer) {
        super(forRelationship, threadId, answer);
    }

    public CommittedAnswerImpl(String forRelationship, String threadId) {
        super(forRelationship, threadId);
    }
}
package com.evernym.verity.sdk.protocols.questionanswer.v1_0;

import com.evernym.verity.sdk.protocols.questionanswer.AskCommonImpl;

/**
 * Builds and sends a new encrypted agent message for the Question protocol.
 */
public class CommittedAnswerImplV1_0 extends AskCommonImpl implements CommittedAnswerV1_0 {

    public CommittedAnswerImplV1_0(String forRelationship,
                                   String questionText,
                                   String questionDetail,
                                   String[] validResponses,
                                   Boolean signatureRequired) {
        super(forRelationship, questionText, questionDetail, validResponses, signatureRequired);
    }

    public CommittedAnswerImplV1_0(String forRelationship, String threadId, String answer) {
        super(forRelationship, threadId, answer);
    }
}
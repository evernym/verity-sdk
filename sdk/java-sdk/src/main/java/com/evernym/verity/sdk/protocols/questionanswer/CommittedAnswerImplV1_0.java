package com.evernym.verity.sdk.protocols.questionanswer;

import com.evernym.verity.sdk.protocols.questionanswer.v1_0.CommittedAnswerV1_0;

/*
 * NON_VISIBLE
 *
 * This is an implementation of CommittedAnswerImplV1_0 but is not viable to user of Verity SDK. Created using the
 * static CommittedAnswer class
 */
class CommittedAnswerImplV1_0 extends AskCommonImpl implements CommittedAnswerV1_0 {

    CommittedAnswerImplV1_0(String forRelationship,
                                   String questionText,
                                   String questionDetail,
                                   String[] validResponses,
                                   Boolean signatureRequired) {
        super(forRelationship, questionText, questionDetail, validResponses, signatureRequired);
    }

    CommittedAnswerImplV1_0(String forRelationship, String threadId, String answer) {
        super(forRelationship, threadId, answer);
    }

    CommittedAnswerImplV1_0(String forRelationship, String threadId) {
        super(forRelationship, threadId);
    }
}
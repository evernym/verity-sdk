package com.evernym.verity.sdk.protocols.questionanswer;

import com.evernym.verity.sdk.protocols.questionanswer.v1_0.QuestionAnswerV1_0;

/*
 * NON_VISIBLE
 *
 * This is an implementation of QuestionAnswerImplV1_0 but is not viable to user of Verity SDK. Created using the
 * static QuestionAnswer class
 */
class QuestionAnswerImplV1_0 extends AskCommonImpl implements QuestionAnswerV1_0 {

    QuestionAnswerImplV1_0(String forRelationship,
                                  String questionText,
                                  String questionDetail,
                                  String[] validResponses,
                                  Boolean signatureRequired) {
        super(forRelationship, questionText, questionDetail, validResponses, signatureRequired);
    }

    QuestionAnswerImplV1_0(String forRelationship,
                                  String threadId,
                                  String answer) {
        super(forRelationship, threadId, answer);
    }

    QuestionAnswerImplV1_0(String forRelationship,
                           String threadId) {
        super(forRelationship, threadId);
    }
}
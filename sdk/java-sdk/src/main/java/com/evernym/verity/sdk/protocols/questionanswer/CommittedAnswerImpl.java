package com.evernym.verity.sdk.protocols.questionanswer;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;

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
}
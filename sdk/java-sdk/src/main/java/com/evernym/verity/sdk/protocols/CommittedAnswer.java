package com.evernym.verity.sdk.protocols;

public class CommittedAnswer extends QuestionAnswer {
    final private String MSG_FAMILY = "committedanswer";
    @Override
    protected String family() {return MSG_FAMILY;}

    public CommittedAnswer(String forRelationship,
                           String questionText,
                           String questionDetail,
                           String[] validResponses) {
        super(forRelationship, questionText, questionDetail, validResponses);
    }

    public CommittedAnswer(String forRelationship,
                           String questionText,
                           String questionDetail,
                           String[] validResponses,
                           Boolean signatureRequired) {
        super(forRelationship, questionText, questionDetail, validResponses, signatureRequired);
    }
}
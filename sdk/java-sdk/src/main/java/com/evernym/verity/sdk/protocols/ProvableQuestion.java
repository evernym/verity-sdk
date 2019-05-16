package com.evernym.verity.sdk.protocols;

import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProvableQuestion extends Protocol {
    private static String type = "vs.service/question/0.1/question";
    private String connectionId;
    private String questionText;
    private String questionDetail;
    private JSONArray validResponses;

    public ProvableQuestion(String connectionId, String questionText, String questionDetail, String[] validResponses) {
        super();
        this.connectionId = connectionId;
        this.questionText = questionText;
        this.questionDetail = questionDetail;
        this.validResponses = new JSONArray();
        for(String validResponseString : validResponses) {
            JSONObject validResponse = new JSONObject();
            validResponse.put("text", validResponseString);
            validResponse.put("nonce", validResponse + "#" + UUID.randomUUID().toString());
            this.validResponses.put(validResponse);
        }
    }

    // TODO: Add support for external links
    // TODO: Add constructor w/o question detail

    @Override
    public String toString() {
        JSONObject message = new JSONObject();
        message.put("@type", this.type);
        message.put("@id", this.id);
        message.put("connection_id", this.connectionId);
        JSONObject question = new JSONObject();
        question.put("question_text", this.questionText);
        question.put("question_detail", this.questionDetail);
        question.put("valid_responses", this.validResponses);
        message.put("question", question);
        return message.toString();
    }
}
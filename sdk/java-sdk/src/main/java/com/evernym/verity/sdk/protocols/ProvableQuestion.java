package com.evernym.verity.sdk.protocols;

import org.json.JSONObject;

public class ProvableQuestion extends Protocol {
    private String type = "vs.service/question/0.1/question";
    private JSONObject question;

    public ProvableQuestion(String question) {
        super();
        this.question = new JSONObject(question);
    }

    @Override public String toString() {
        JSONObject message = new JSONObject();
        message.put("@type", this.type);
        message.put("@id", this.id);
        message.put("question", this.question);
        return message.toString();
    }
}
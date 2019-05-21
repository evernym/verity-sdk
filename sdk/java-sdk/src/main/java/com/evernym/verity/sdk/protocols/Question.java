package com.evernym.verity.sdk.protocols;

import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

public class Question extends Protocol {
    // Message Type Definitions
    public static String ASK_QUESTION_MESSAGE_TYPE = "vs.service/question/0.1/question";
    public static String PROBLEM_REPORT_MESSAGE_TYPE = "vs.service/question/0.1/problem-report";
    public static String STATUS_MESSAGE_TYPE = "vs.service/question/0.1/status";

    // Status Definitions
    public static Integer QUESTION_SENT_STATUS_STATE = 0;
    public static Integer QUESTION_ANSWERED_STATE = 1;
    public static Integer ERROR_STATE = 2;

    private String connectionId;
    private String notificationTitle;
    private String questionText;
    private String questionDetail;
    private JSONArray validResponses;

    public Question(String connectionId, String notificationTitle, String questionText, String questionDetail, String[] validResponses) {
        super();
        this.connectionId = connectionId;
        this.notificationTitle = notificationTitle;
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
        message.put("@type", Question.ASK_QUESTION_MESSAGE_TYPE);
        message.put("@id", this.id);
        message.put("connection_id", this.connectionId);
        JSONObject question = new JSONObject();
        question.put("notification_title", this.notificationTitle);
        question.put("question_text", this.questionText);
        question.put("question_detail", this.questionDetail);
        question.put("valid_responses", this.validResponses);
        message.put("question", question);
        return message.toString();
    }
}
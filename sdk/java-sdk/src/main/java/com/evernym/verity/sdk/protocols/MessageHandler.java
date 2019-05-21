package com.evernym.verity.sdk.protocols;

import org.json.JSONObject;

public class MessageHandler {
    private String messageType;
    private Integer messageStatus;
    private Handler messageHandler;

    public interface Handler {
        public void handle(JSONObject message);
    }

    MessageHandler(String messageType, Handler messageHandler) {
        this.messageType = messageType;
        this.messageHandler = messageHandler;
    }

    MessageHandler(String messageType, Integer messageStatus, Handler messageHandler) {
        this.messageType = messageType;
        this.messageStatus = messageStatus;
        this.messageHandler = messageHandler;
    }

    public boolean handles(JSONObject message) {
        return message.getString("@type").equals(this.messageType) && (messageStatus == null || message.getInt("status") == this.messageStatus);
    }

    public void handle(JSONObject message) {
        messageHandler.handle(message);
    }
}
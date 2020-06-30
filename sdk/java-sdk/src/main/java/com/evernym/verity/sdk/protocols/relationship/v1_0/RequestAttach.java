package com.evernym.verity.sdk.protocols.relationship.v1_0;

import org.json.JSONObject;

public class RequestAttach{
    String id;
    String mimeType;
    JSONObject data;

    public RequestAttach(String id, String mimeType, JSONObject data) {
        this.id = id;
        this.mimeType = mimeType;
        this.data = data;
    }
}

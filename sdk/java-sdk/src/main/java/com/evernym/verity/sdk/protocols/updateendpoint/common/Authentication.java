package com.evernym.verity.sdk.protocols.updateendpoint.common;

import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONObject;

public class Authentication implements AsJsonObject {
    final JSONObject json;

    public Authentication(String type, String version, JSONObject data) {
        json = new JSONObject();
        json.put("type", type);
        json.put("version", version);
        json.put("data", data);
    }

    @Override
    public JSONObject toJson() {
        return json;
    }
}

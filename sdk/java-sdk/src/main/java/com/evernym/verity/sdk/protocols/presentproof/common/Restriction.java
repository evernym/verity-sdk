package com.evernym.verity.sdk.protocols.presentproof.common;

import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONObject;

public class Restriction implements AsJsonObject {
    JSONObject data;

    protected Restriction(JSONObject data) {
        this.data = data;
    }

    @Override
    public JSONObject toJson() {
        return this.data;
    }
}

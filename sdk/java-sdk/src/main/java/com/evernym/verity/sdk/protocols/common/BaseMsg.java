package com.evernym.verity.sdk.protocols.common;

import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONObject;

public class BaseMsg implements AsJsonObject  {
    JSONObject data;

    public BaseMsg(JSONObject data) {
        this.data = data;
    }

    @Override
    public JSONObject toJson() {
        return this.data;
    }
}

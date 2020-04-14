package com.evernym.verity.sdk.protocols.writecreddef.v0_6;

import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONObject;

public class RevocationRegistryConfig implements AsJsonObject {
    private JSONObject data;

    public RevocationRegistryConfig(JSONObject data) {
        this.data = data;
    }

    @Override
    public JSONObject toJson() {
        return data;
    }
}

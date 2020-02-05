package com.evernym.verity.sdk.protocols.issuecredential.v_1_0.cred_preview;

import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONObject;

public class CredPreview implements AsJsonObject {
    JSONObject data;

    protected CredPreview(JSONObject data) {
        this.data = data;
    }

    @Override
    public JSONObject toJson() {
        return this.data;
    }
}

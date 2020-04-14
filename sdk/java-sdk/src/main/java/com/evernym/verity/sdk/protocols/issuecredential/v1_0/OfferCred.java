package com.evernym.verity.sdk.protocols.issuecredential.v1_0;

import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONObject;

public class OfferCred implements AsJsonObject {
    JSONObject data;

    protected OfferCred(JSONObject data) {
        this.data = data;
    }

    @Override
    public JSONObject toJson() {
        return data;
    }
}

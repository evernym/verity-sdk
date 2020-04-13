package com.evernym.verity.sdk.protocols.issuecredential.v_1_0.cred_req;

import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONObject;

public class CredReq implements AsJsonObject {
    JSONObject data;

    protected CredReq(JSONObject data) {
        this.data = data;
    }

    @Override
    public JSONObject toJson() {
        return data;
    }
}

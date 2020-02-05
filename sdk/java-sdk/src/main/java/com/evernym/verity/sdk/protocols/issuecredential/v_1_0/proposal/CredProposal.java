package com.evernym.verity.sdk.protocols.issuecredential.v_1_0.proposal;

import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONObject;

public class CredProposal implements AsJsonObject {
    JSONObject data;

    protected CredProposal(JSONObject data) {
        this.data = data;
    }

    @Override
    public JSONObject toJson() {
        return data;
    }
}

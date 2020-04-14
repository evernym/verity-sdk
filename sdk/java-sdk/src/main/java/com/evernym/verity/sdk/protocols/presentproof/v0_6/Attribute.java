package com.evernym.verity.sdk.protocols.presentproof.v0_6;

import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONObject;

import static com.evernym.verity.sdk.utils.JsonUtil.makeArray;

public class Attribute implements AsJsonObject {
    JSONObject data;

    protected Attribute(String name, Restriction...restrictions){
        this.data = new JSONObject()
                .put("name", name)
                .put("restrictions", makeArray(restrictions));
    }

    @Override
    public JSONObject toJson() {
        return data;
    }
}

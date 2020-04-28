package com.evernym.verity.sdk.protocols.presentproof.common;

import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONObject;

import static com.evernym.verity.sdk.utils.JsonUtil.makeArray;

public class Attribute implements AsJsonObject {
    final JSONObject data;

    public Attribute(String name, Restriction... restrictions){
        this.data = new JSONObject()
                .put("name", name)
                .put("restrictions", makeArray(restrictions));
    }

    @Override
    public JSONObject toJson() {
        return data;
    }
}

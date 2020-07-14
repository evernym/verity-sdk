package com.evernym.verity.sdk.protocols.presentproof.common;

import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONObject;

/**
 * A holder for a restriction expression
 */
public class Restriction implements AsJsonObject {
    final JSONObject data;

    protected Restriction(JSONObject data) {
        this.data = data;
    }

    /**
     * Convert this object to a JSON object
     *
     * @return this object as a JSON object
     */
    @Override
    public JSONObject toJson() {
        return this.data;
    }
}

package com.evernym.verity.sdk.protocols.presentproof.common;

import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONObject;

import static com.evernym.verity.sdk.utils.JsonUtil.makeArray;

/**
 * A holder for an attribute based restrictions
 */
public class Attribute implements AsJsonObject {
    final JSONObject data;

    /**
     * Constructs the Attribute object with the given attribute name and given restrictions
     * @param name the attribute name
     * @param restrictions the restrictions for requested presentation for the given attribute
     */
    public Attribute(String name, Restriction... restrictions){
        this.data = new JSONObject()
                .put("name", name)
                .put("restrictions", makeArray(restrictions));
    }

    /**
     * Convert this object to a JSON object
     *
     * @return this object as a JSON object
     */
    @Override
    public JSONObject toJson() {
        return data;
    }
}

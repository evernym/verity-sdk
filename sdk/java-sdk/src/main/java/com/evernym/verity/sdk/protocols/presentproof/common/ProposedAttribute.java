package com.evernym.verity.sdk.protocols.presentproof.common;

import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONObject;

public class ProposedAttribute implements AsJsonObject {
    final JSONObject data;


    /**
     * Constructs the ProposedAttribute object
     * @param name the attribute name
     * @param credDefId credential from which attribute is obtained.
     * @param value value of the attribute.
     */
    public ProposedAttribute(String name, String credDefId, String value){
        this.data = new JSONObject()
                .put("name", name)
                .put("cred_def_id", credDefId)
                .put("value", value);
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

package com.evernym.verity.sdk.protocols.presentproof.common;

import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONObject;

public class ProposedPredicate implements AsJsonObject {
    final JSONObject data;


    /**
     * Constructs the ProposedPredicate object
     * @param name the predicate name
     * @param credDefId the credential definition used for this predicate.
     * @param threshold threshold value of the predicate.
     */
    public ProposedPredicate(String name, String credDefId, int threshold){
        this.data = new JSONObject()
                .put("name", name)
                .put("cred_def_id", credDefId)
                .put("predicate", ">=")
                .put("threshold", threshold);
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

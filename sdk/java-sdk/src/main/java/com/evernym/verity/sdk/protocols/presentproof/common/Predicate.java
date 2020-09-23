package com.evernym.verity.sdk.protocols.presentproof.common;

import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONObject;

import static com.evernym.verity.sdk.utils.JsonUtil.makeArray;

/**
 * A holder for an predicate based restrictions
 */
public class Predicate implements AsJsonObject  {

    /**
     * Constructs the Predicate object with the given attribute name, type, value and given restrictions

     * @param name the attribute name
     * @param type the type only is supported "<=", "<", ">=" or ">"
     * @param value the value the given attribute must satisfy the type
     * @param restrictions the restrictions for requested presentation for this predicate
     */
    public Predicate(String name, String type, int value, Restriction... restrictions) {
        this.data = new JSONObject()
                .put("name", name)
                .put("p_type", type)
                .put("p_value", value)
                .put("restrictions", makeArray(restrictions));
    }

    final JSONObject data;

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

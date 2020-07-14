package com.evernym.verity.sdk.utils;

import org.json.JSONObject;

/**
 * Interface for object that can be converted to a JSON array
 */
public interface AsJsonObject {
    /**
     * Convert this object to a JSON object
     *
     * @return this object as a JSON object
     */
    JSONObject toJson();
}


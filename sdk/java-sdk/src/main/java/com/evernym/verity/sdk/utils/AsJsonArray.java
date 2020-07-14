package com.evernym.verity.sdk.utils;

import org.json.JSONArray;

/**
 * Interface for object that can be converted to a JSON array
 */
public interface AsJsonArray {
    /**
     * Convert this object to a JSON Array
     *
     * @return this object as a JSON Array
     */
    JSONArray toJson();
}

package com.evernym.verity.sdk.utils;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;

/**
 * Utilities for creating JSON objects
 */
public class JsonUtil {
    private JsonUtil(){}

    /**
     * Builds a JSONArray from an array of object that implement AsJsonObject interface
     * @param items an array of object that can be converted to an JSONObject
     * @return a JSON array
     */
    public static JSONArray makeArray(AsJsonObject[] items) {
        return buildArray(Arrays.asList(items));
    }

    /**
     * Builds a JSONArray from a list of object that implement AsJsonObject interface
     * @param items a List of object that can be converted to an JSONObject
     * @return a JSON array
     */
    public static JSONArray makeArrayFromList(List<AsJsonObject> items) {
        return buildArray(items);
    }

    private static JSONArray buildArray(Iterable<AsJsonObject> items) {
        JSONArray rtn = new JSONArray();
        if(items != null) {
            for(AsJsonObject i: items) {
                if(i != null) {
                    rtn.put(i.toJson());
                }
            }
        }
        return rtn;
    }
}

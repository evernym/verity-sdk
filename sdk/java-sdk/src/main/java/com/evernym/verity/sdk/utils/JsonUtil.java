package com.evernym.verity.sdk.utils;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;

public class JsonUtil {
    private JsonUtil(){}

    public static JSONArray makeArray(AsJsonObject[] items) {
        return buildArray(Arrays.asList(items));
    }

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

package com.evernym.verity.sdk.utils;

import org.json.JSONArray;

public class JsonUtil {
    private JsonUtil(){}

    public static JSONArray makeArray(AsJsonObject[] restrictions) {
        JSONArray rtn = new JSONArray();
        if(restrictions != null) {
            for(AsJsonObject i: restrictions) {
                if(i != null) {
                    rtn.put(i.toJson());
                }
            }
        }
        return rtn;
    }
}

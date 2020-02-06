package com.evernym.verity.sdk.utils;

import org.json.JSONArray;
import java.util.List;

public class JsonUtil {
    private JsonUtil(){}

    public static JSONArray makeArray(AsJsonObject[] items) {
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

    public static <T extends AsJsonObject> JSONArray makeArray(List<T> items) {
        if (items == null)
            return null;
        AsJsonObject[] array = items.toArray(new AsJsonObject[0]);  //FIXME: not sure about the 0
        return makeArray(array);
    }
}

package com.evernym.verity.sdk.protocols.common;

import org.json.JSONObject;

public abstract class BaseMsgBuilder<T> {

    private JSONObject data = new JSONObject();

    protected abstract T self();

    public T type(String type) {
        addToJSON("@type", type);
        return self();
    }

    public T id(String id) {
        addToJSON("@id", id);
        return self();
    }

    public T forRelationship(String relationshipId) {
        addToJSON("~for_relationship", relationshipId);
        return self();
    }

    public void addToJSON(String key, Object value) {
        if (value != null) {
            data.put(key, value);
        }
    }

    public JSONObject getJSONObject() {
        return data;
    }
}

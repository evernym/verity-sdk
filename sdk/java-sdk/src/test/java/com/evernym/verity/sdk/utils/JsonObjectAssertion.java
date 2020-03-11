package com.evernym.verity.sdk.utils;

import org.json.JSONObject;
import static org.junit.Assert.*;

abstract class JsonObjectAssertion {

    public void assertEqualsJSONObject(JSONObject expected, JSONObject actual) {
        assertEquals(expected.toMap().size(), actual.toMap().size());
        expected.toMap().forEach((s, o) ->
            assertEquals(actual.get(s), o)
        );
    }
}

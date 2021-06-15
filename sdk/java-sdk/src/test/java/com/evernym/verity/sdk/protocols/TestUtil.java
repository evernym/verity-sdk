package com.evernym.verity.sdk.protocols;

import org.json.JSONObject;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtil {
    private TestUtil() {}

    public static void assertJsonObjectEqual(JSONObject expected, JSONObject actual) {
        // Not ideal for comparing JSON objects but JSONObject don't provide a better way
        expected.remove("@id");
        actual.remove("@id");
        assertEquals(expected.toString(), actual.toString());
    }
}

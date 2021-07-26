package com.evernym.verity.sdk.protocols.updateendpoint;

import com.evernym.verity.sdk.protocols.updateendpoint.common.Authentication;
import com.evernym.verity.sdk.protocols.updateendpoint.common.AuthenticationFactory;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthenticationTest {
    @Test
    void testOAuth2V1() throws MalformedURLException {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("grant_type", "client_credentials");
        data.put("client_id", "ajeyKDizsDkwYeEmRmHU78gf7W3VIEoA");
        data.put("client_secret", "aaGxxcGi6kb6AxIe");
        Authentication auth = AuthenticationFactory.createOAuth2V1(new URL("https://auth.url/token"), data);
        JSONObject json = auth.toJson();

        assertEquals("OAuth2", json.getString("type"));
        assertEquals("v1", json.getString("version"));
        assertEquals("https://auth.url/token", json.getJSONObject("data").getString("url"));
        assertEquals("client_credentials", json.getJSONObject("data").getString("grant_type"));
        assertEquals("ajeyKDizsDkwYeEmRmHU78gf7W3VIEoA", json.getJSONObject("data").getString("client_id"));
        assertEquals("aaGxxcGi6kb6AxIe", json.getJSONObject("data").getString("client_secret"));
    }
}

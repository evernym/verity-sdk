package com.evernym.verity.sdk.protocols.updateendpoint.common;

import org.json.JSONObject;

import java.net.URL;
import java.util.Map;

public class AuthenticationFactory {
    /**
     * Create authentication parameter for update endpoint which require OAuth2 auth.
     * This is for version v1 of OAuth2 for verity.
     * @param tokenUrl Url on which token is requested
     * @param data additional data required, for eg:
     *         {
     *             "grant_type": "client_credentials",
     *             "client_id": "ajeyKDizsDkwYeEmRmHU78gf7W3VIEoA",
     *             "client_secret": "aaGxxcGi6kb6AxIe"
     *         }
     * @return Authentication instance
     */
    public static Authentication createOAuth2V1(URL tokenUrl, Map<String, String> data) {
        JSONObject jsonData = new JSONObject();
        jsonData.put("url", tokenUrl.toString());
        data.forEach(jsonData::put);
        return new Authentication("OAuth2", "v1", jsonData);
    }
}

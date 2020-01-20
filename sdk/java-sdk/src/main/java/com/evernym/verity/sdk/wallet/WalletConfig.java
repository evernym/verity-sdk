package com.evernym.verity.sdk.wallet;

import org.json.JSONObject;

public interface WalletConfig {
    String config();
    String credential();
    void addToJson(JSONObject json);
}

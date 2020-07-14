package com.evernym.verity.sdk.wallet;

import org.json.JSONObject;

/**
 * The generic interface for the wallet configuration for the Indy-SDK wallet
 */
public interface WalletConfig {
    /**
     * Provides a JSON string that is valid config for the Indy-SDK wallet API
     *
     * @return a JSON wallet config string
     */
    String config();

    /**
     * Provides a JSON string that is valid credential for the Indy-SDK wallet API
     *
     * @return a JSON wallet credential string
     */
    String credential();

    /**
     * Add relevant fields to a JSON object to express this configuration
     * @param json the JSON object that the fields are injected into
     */
    void addToJson(JSONObject json);
}

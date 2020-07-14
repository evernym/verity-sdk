package com.evernym.verity.sdk.wallet;

import org.json.JSONObject;

/**
 * The config object for the local default wallet provided by the Indy SDK
 */
public class DefaultWalletConfig implements WalletConfig {
    /**
     * Constructs a DefaultWalletConfig with the given basic parameters
     *
     * @param id the given name or identifier for the wallet
     * @param key the given key (encrypting) for the wallet
     * @param path the given path location where the wallet on disk file is found
     * @return a DefaultWalletConfig object
     */
    public static DefaultWalletConfig build(String id, String key, String path) {
        return new DefaultWalletConfig(id, key, path);
    }

    /**
     * Constructs a DefaultWalletConfig with the given basic parameters
     * @param id the given name or identifier for the wallet
     * @param key the given key (encrypting) for the wallet
     * @return a DefaultWalletConfig object
     */
    public static DefaultWalletConfig build(String id, String key) {
        return new DefaultWalletConfig(id, key);
    }

    /**
     * the name or identifier for the wallet
     */
    public final String id;
    /**
     * the key (encrypting) for the wallet
     */
    public final String key;
    /**
     * the path location where the wallet on disk file is found
     */
    public final String path;

    private DefaultWalletConfig(String id, String key) {
        this.id = id;
        this.key = key;
        this.path = null;
    }

    private DefaultWalletConfig(String id, String key, String path) {
        this.id = id;
        this.key = key;
        this.path = path;
    }

    /**
     * Provides a default JSON config string
     *
     * @return a JSON wallet config string
     */
    @Override
    public String config() {
        JSONObject rtn = new JSONObject();
        rtn.put("id", this.id);
        if (path != null && !path.trim().equals("")) {
            JSONObject storageOptions = new JSONObject();
            storageOptions.put("path", path);
            rtn.put("storage_config", storageOptions);
        }
        return rtn.toString();
    }

    /**
     * Provides a default JSON credential string
     *
     * @return a JSON wallet credential string
     */
    @Override
    public String credential() {
        return new JSONObject().put("key", this.key).toString();
    }

    /**
     * Injects the default wallet fields into a JSON Object
     *
     * @param json the JSON object that the fields are injected into
     */
    @Override
    public void addToJson(JSONObject json) {
        if(id != null) json.put("walletName", id);
        if(key != null) json.put("walletKey", key);
        if(path != null) json.put("walletPath", path);
    }
}

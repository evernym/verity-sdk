package com.evernym.verity.sdk.wallet;

import org.json.JSONObject;

public class DefaultWalletConfig implements WalletConfig {
    public static DefaultWalletConfig build(String id, String key, String path) {
        return new DefaultWalletConfig(id, key, path);
    }

    public static DefaultWalletConfig build(String id, String key) {
        return new DefaultWalletConfig(id, key);
    }

    public final String id;
    public final String key;
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

    @Override
    public String config() {
        JSONObject rtn = new JSONObject();
        rtn.put("id", this.id);
        if (path != null) {
            JSONObject storageOptions = new JSONObject();
            storageOptions.put("path", path);
            rtn.put("storage_config", storageOptions);
        }
        return rtn.toString();
    }

    @Override
    public String credential() {
        return new JSONObject().put("key", this.key).toString();
    }

    @Override
    public void addToJson(JSONObject json) {
        if(id != null) json.put("walletName", id);
        if(key != null) json.put("walletKey", key);
        if(path != null) json.put("walletPath", path);
    }
}

package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.exceptions.WalletOpenException;
import com.evernym.verity.sdk.wallet.DefaultWalletConfig;
import com.evernym.verity.sdk.wallet.WalletConfig;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.evernym.verity.sdk.utils.ContextConstants.*;
import static com.evernym.verity.sdk.utils.VerityUtil.retrieveVerityPublicDid;
import static com.evernym.verity.sdk.utils.WalletUtil.tryCreateWallet;

public class ContextBuilder {

    public static Context fromScratch(String walletId,
                                      String walletKey,
                                      String verityUrl,
                                      String domainDID,
                                      String verityAgentVerKey) throws IOException, WalletException {
        return fromScratch(walletId, walletKey, verityUrl, domainDID, verityAgentVerKey, null);
    }

    public static Context fromScratch(String walletId,
                                      String walletKey,
                                      String verityUrl,
                                      String domainDID,
                                      String verityAgentVerKey,
                                      String seed) throws IOException, WalletException {
        tryCreateWallet(walletId, walletKey);
        Context inter = scratchContext(DefaultWalletConfig.build(walletId, walletKey), verityUrl, seed);
        return withProvisionedAgent(inter, domainDID, verityAgentVerKey);
    }

    public static Context fromScratch(WalletConfig walletConfig,
                                      String verityUrl,
                                      String domainDID,
                                      String verityAgentVerKey) throws IOException, WalletException {
        return fromScratch(walletConfig, verityUrl, domainDID, verityAgentVerKey, null);
    }

    public static Context fromScratch(WalletConfig walletConfig,
                                      String verityUrl,
                                      String domainDID,
                                      String verityAgentVerKey,
                                      String seed) throws IOException, WalletException {
        tryCreateWallet(walletConfig);
        Context inter = scratchContext(walletConfig, verityUrl, seed);
        return withProvisionedAgent(inter, domainDID, verityAgentVerKey);
    }

    public static Context fromScratch(String walletId,
                                      String walletKey,
                                      String verityUrl) throws IOException, WalletException {
        return fromScratch(walletId, walletKey, verityUrl, null);
    }

    public static Context fromScratch(String walletId,
                                      String walletKey,
                                      String verityUrl,
                                      String seed) throws IOException, WalletException {
        tryCreateWallet(walletId, walletKey);
        return scratchContext(DefaultWalletConfig.build(walletId, walletKey), verityUrl, seed);
    }

    public static Context fromScratch(WalletConfig walletConfig,
                                      String verityUrl) throws IOException, WalletException {
        return fromScratch(walletConfig, verityUrl, null);
    }

    public static Context fromScratch(WalletConfig walletConfig,
                                      String verityUrl,
                                      String seed) throws IOException, WalletException {
        tryCreateWallet(walletConfig);
        return scratchContext(walletConfig, verityUrl, seed);
    }

    public static ContextBuilder fromJson(String json) {
        return new ContextBuilder().json(json);
    }

    public static ContextBuilder fromJson(JSONObject json) {
        return new ContextBuilder().json(json);
    }

    public static ContextBuilder blank() {
        return new ContextBuilder();
    }

    //********************
    // NON-PUBLIC METHODS
    //********************

    protected static Context scratchContext(WalletConfig wallet, String verityUrl, String seed)
            throws WalletException, IOException {

        Did verityDid = retrieveVerityPublicDid(verityUrl);

        return scratchContext(wallet, verityUrl, verityDid, seed);
    }

    private static Context withProvisionedAgent(Context inter, String domainDID, String verityAgentVerKey)
            throws WalletException {
        return inter.toContextBuilder()
                .domainDID(domainDID)
                .verityAgentVerKey(verityAgentVerKey)
                .build();
    }

    protected static Context scratchContext(WalletConfig wallet, String verityUrl, Did verityDid, String seed)
            throws WalletException {

        Context inter = new ContextBuilder()
                .verityPublicDID(verityDid.did)
                .verityPublicVerKey(verityDid.verkey)
                .walletConfig(wallet)
                .verityUrl(verityUrl)
                .build();

        Did mime = Did.createNewDid(inter.walletHandle(), seed);

        return inter.toContextBuilder()
                .sdkVerKeyId(mime.did)
                .sdkVerKey(mime.verkey)
                .build();
    }

    private Map<String, String> elements = new HashMap<>();
    private WalletConfig walletConfig;
    private Wallet walletHandle = null;

    private ContextBuilder() {}

    private ContextBuilder putElement(String key, String val) {
        elements.put(key, val);
        return this;
    }

    private ContextBuilder putElementIgnoreNull(String key, String val) {
        if(val != null) {
            return putElement(key, val);
        }
        else return this;
    }

    public ContextBuilder json(JSONObject json){
        WalletConfig w = DefaultWalletConfig.build(
                json.getString("walletName"),
                json.getString("walletKey"),
                json.optString("walletPath", null)
        );
        this.walletConfig(w);
        putElementIgnoreNull(ENDPOINT_URL, json.optString(ENDPOINT_URL));
        putElementIgnoreNull(VERITY_URL, json.optString(VERITY_URL));
        String version = json.optString(VERSION);
        if (V_0_2.equals(version)) {
            return json_0_2(json);
        }
        return json_0_1(json);
    }

    private ContextBuilder json_0_2(JSONObject json){
        putElementIgnoreNull(VERITY_PUBLIC_DID, json.optString(VERITY_PUBLIC_DID));
        putElementIgnoreNull(VERITY_PUBLIC_VER_KEY, json.optString(VERITY_PUBLIC_VER_KEY));
        putElementIgnoreNull(DOMAIN_DID, json.optString(DOMAIN_DID));
        putElementIgnoreNull(VERITY_AGENT_VER_KEY, json.optString(VERITY_AGENT_VER_KEY));
        putElementIgnoreNull(SDK_VER_KEY_ID, json.optString(SDK_VER_KEY_ID));
        putElementIgnoreNull(SDK_VER_KEY, json.optString(SDK_VER_KEY));
        putElementIgnoreNull(VERSION, V_0_2);

        return this;
    }

    private ContextBuilder json_0_1(JSONObject json){
        putElementIgnoreNull(VERITY_PUBLIC_DID, json.optString(VERITY_PUBLIC_DID));
        putElementIgnoreNull(VERITY_PUBLIC_VER_KEY, json.optString(LEGACY_VERITY_PUBLIC_VER_KEY));
        putElementIgnoreNull(DOMAIN_DID, json.optString(LEGACY_DOMAIN_DID));
        putElementIgnoreNull(VERITY_AGENT_VER_KEY, json.optString(LEGACY_VERITY_AGENT_VER_KEY));
        putElementIgnoreNull(SDK_VER_KEY_ID, json.optString(LEGACY_SDK_VER_KEY_ID));
        putElementIgnoreNull(SDK_VER_KEY, json.optString(LEGACY_SDK_VER_KEY));
        putElementIgnoreNull(VERSION, V_0_2); // Converts to latest version -- NOT current version
        return this;
    }

    public ContextBuilder json(String json) {return json(new JSONObject(json));}

    public ContextBuilder walletConfig(WalletConfig config) {walletConfig = config; return this;}
    public ContextBuilder verityUrl(String val) {return putElement(VERITY_URL, val);}
    public ContextBuilder verityPublicDID(String val) {return putElement(VERITY_PUBLIC_DID, val);}
    public ContextBuilder verityPublicVerKey(String val) {return putElement(VERITY_PUBLIC_VER_KEY, val);}
    public ContextBuilder domainDID(String val) {return putElement(DOMAIN_DID, val);}
    public ContextBuilder verityAgentVerKey(String val) {return putElement(VERITY_AGENT_VER_KEY, val);}
    public ContextBuilder sdkVerKeyId(String val) {return putElement(SDK_VER_KEY_ID, val);}
    public ContextBuilder sdkVerKey(String val) {return putElement(SDK_VER_KEY, val);}
    public ContextBuilder endpointUrl(String val) {return putElement(ENDPOINT_URL, val);}

    ContextBuilder walletHandle(Wallet val) {
        walletHandle = val;
        return this;
    }

    /**
     *
     * @return Built Context instance from this builder.
     * @throws WalletOpenException when the wallet does not exist or Indy is unable to open it.
     */
    public Context build() throws WalletOpenException {
        if (walletHandle == null) {
            return new Context(
                    walletConfig,
                    elements.get(VERSION),
                    elements.get(VERITY_URL),
                    elements.get(VERITY_PUBLIC_DID),
                    elements.get(VERITY_PUBLIC_VER_KEY),
                    elements.get(DOMAIN_DID),
                    elements.get(VERITY_AGENT_VER_KEY),
                    elements.get(SDK_VER_KEY_ID),
                    elements.get(SDK_VER_KEY),
                    elements.get(ENDPOINT_URL)
            );
        }
        else {
            return new Context(
                    walletConfig,
                    elements.get(VERSION),
                    elements.get(VERITY_URL),
                    elements.get(VERITY_PUBLIC_DID),
                    elements.get(VERITY_PUBLIC_VER_KEY),
                    elements.get(DOMAIN_DID),
                    elements.get(VERITY_AGENT_VER_KEY),
                    elements.get(SDK_VER_KEY_ID),
                    elements.get(SDK_VER_KEY),
                    elements.get(ENDPOINT_URL),
                    walletHandle
            );
        }
    }
}


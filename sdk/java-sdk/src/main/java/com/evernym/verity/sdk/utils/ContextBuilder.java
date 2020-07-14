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
import static com.evernym.verity.sdk.wallet.WalletUtil.tryCreateWallet;

/**
 * A builder pattern object for initializing Context objects.
 * @see Context
 */
public class ContextBuilder {

    /**
     * Initializes a ContextBuilder from scratch for a verity-sdk that is using an already provisioned agent on
     * the verity-application. This involves three main steps. First, a new local wallet is created
     * using the given wallet id and key. Second, a new local key-pair is created in the new created wallet that will
     * be used by the verity-sdk. Third, the verity-application public DID and verkey a retrieved from the
     * verity-application instance referenced by the given verkey url. After these two step, a given fields are loaded
     * into a blank ContextBuilder and returned.
     * @param walletId the id for the local wallet
     * @param walletKey the key (credential) for the local wallet
     * @param verityUrl the url for the targeted instance of the verity-application
     * @param domainDID the domain DID for the agent already provisioned
     * @param verityAgentVerKey the verkey for the agent already provisioned
     * @return a ContextBuilder with given, retrieved and created fields
     * @throws IOException when retrieving info from the verity-application fails (bad url, unreachable, etc)
     * @throws WalletException when wallet operations fail
     */
    public static Context fromScratch(String walletId,
                                      String walletKey,
                                      String verityUrl,
                                      String domainDID,
                                      String verityAgentVerKey) throws IOException, WalletException {
        return fromScratch(walletId, walletKey, verityUrl, domainDID, verityAgentVerKey, null);
    }

    /**
     * Initializes a ContextBuilder from scratch for a verity-sdk that is using an already provisioned agent on
     * the verity-application. This involves three main steps. First, a new local wallet is created
     * using the given wallet id and key. Second, a new local key-pair is created in the new created wallet that will
     * be used by the verity-sdk. Third, the verity-application public DID and verkey a retrieved from the
     * verity-application instance referenced by the given verkey url. After these two step, a given fields are loaded
     * into a blank ContextBuilder and returned.
     * @param walletId the id for the local wallet
     * @param walletKey the key (credential) for the local wallet
     * @param verityUrl the url for the targeted instance of the verity-application
     * @param domainDID the domain DID for the agent already provisioned
     * @param verityAgentVerKey the verkey for the agent already provisioned
     * @param seed the seed used to generate the local key-pair
     * @return a ContextBuilder with given, retrieved and created fields
     * @throws IOException when retrieving info from the verity-application fails (bad url, unreachable, etc)
     * @throws WalletException when wallet operations fail
     */
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

    /**
     * Initializes a ContextBuilder from scratch for a verity-sdk that is using an already provisioned agent on
     * the verity-application. This involves three main steps. First, a new local wallet is created
     * using the given wallet config. Second, a new local key-pair is created in the new created wallet that will
     * be used by the verity-sdk. Third, the verity-application public DID and verkey a retrieved from the
     * verity-application instance referenced by the given verkey url. After these two step, a given fields are loaded
     * into a blank ContextBuilder and returned.
     * @param walletConfig the wallet config for accessing the local wallet
     * @param verityUrl the url for the targeted instance of the verity-application
     * @param domainDID the domain DID for the agent already provisioned
     * @param verityAgentVerKey the verkey for the agent already provisioned
     * @return a ContextBuilder with given, retrieved and created fields
     * @throws IOException when retrieving info from the verity-application fails (bad url, unreachable, etc)
     * @throws WalletException when wallet operations fail
     */
    public static Context fromScratch(WalletConfig walletConfig,
                                      String verityUrl,
                                      String domainDID,
                                      String verityAgentVerKey) throws IOException, WalletException {
        return fromScratch(walletConfig, verityUrl, domainDID, verityAgentVerKey, null);
    }

    /**
     * Initializes a ContextBuilder from scratch for a verity-sdk that is using an already provisioned agent on
     * the verity-application. This involves three main steps. First, a new local wallet is created
     * using the given wallet config. Second, a new local key-pair is created in the new created wallet that will
     * be used by the verity-sdk. Third, the verity-application public DID and verkey a retrieved from the
     * verity-application instance referenced by the given verkey url. After these two step, a given fields are loaded
     * into a blank ContextBuilder and returned.
     * @param walletConfig the wallet config for accessing the local wallet
     * @param verityUrl the url for the targeted instance of the verity-application
     * @param domainDID the domain DID for the agent already provisioned
     * @param verityAgentVerKey the verkey for the agent already provisioned
     * @param seed the seed used to generate the local key-pair
     * @return a ContextBuilder with given, retrieved and created fields
     * @throws IOException when retrieving info from the verity-application fails (bad url, unreachable, etc)
     * @throws WalletException when wallet operations fail
     */
    public static Context fromScratch(WalletConfig walletConfig,
                                      String verityUrl,
                                      String domainDID,
                                      String verityAgentVerKey,
                                      String seed) throws IOException, WalletException {
        tryCreateWallet(walletConfig);
        Context inter = scratchContext(walletConfig, verityUrl, seed);
        return withProvisionedAgent(inter, domainDID, verityAgentVerKey);
    }

    /**
     * Initializes a ContextBuilder from scratch for a verity-sdk without a provisioned agent on the
     * verity-application. This involves three main steps. First, a new local wallet is created
     * using the given wallet id and key. Second, a new local key-pair is created in the new created wallet that will
     * be used by the verity-sdk. Third, the verity-application public DID and verkey a retrieved from the
     * verity-application instance referenced by the given verkey url. After these two step, a given fields are loaded
     * into a blank ContextBuilder and returned.
     * @param walletId the id for the local wallet
     * @param walletKey the key (credential) for the local wallet
     * @param verityUrl the url for the targeted instance of the verity-application
     * @return a ContextBuilder with given, retrieved and created fields
     * @throws IOException when retrieving info from the verity-application fails (bad url, unreachable, etc)
     * @throws WalletException when wallet operations fail
     */
    public static Context fromScratch(String walletId,
                                      String walletKey,
                                      String verityUrl) throws IOException, WalletException {
        return fromScratch(walletId, walletKey, verityUrl, null);
    }

    /**
     * Initializes a ContextBuilder from scratch for a verity-sdk without a provisioned agent on the
     * verity-application. This involves three main steps. First, a new local wallet is created
     * using the given wallet id and key. Second, a new local key-pair is created in the new created wallet that will
     * be used by the verity-sdk. Third, the verity-application public DID and verkey a retrieved from the
     * verity-application instance referenced by the given verkey url. After these two step, a given fields are loaded
     * into a blank ContextBuilder and returned.
     * @param walletId the id for the local wallet
     * @param walletKey the key (credential) for the local wallet
     * @param verityUrl the url for the targeted instance of the verity-application
     * @param seed the seed used to generate the local key-pair
     * @return a ContextBuilder with given, retrieved and created fields
     * @throws IOException when retrieving info from the verity-application fails (bad url, unreachable, etc)
     * @throws WalletException when wallet operations fail
     */
    public static Context fromScratch(String walletId,
                                      String walletKey,
                                      String verityUrl,
                                      String seed) throws IOException, WalletException {
        tryCreateWallet(walletId, walletKey);
        return scratchContext(DefaultWalletConfig.build(walletId, walletKey), verityUrl, seed);
    }

    /**
     * Initializes a ContextBuilder from scratch for a verity-sdk without a provisioned agent on the
     * verity-application. This involves three main steps. First, a new local wallet is created
     * using the given wallet config. Second, a new local key-pair is created in the new created wallet that will
     * be used by the verity-sdk. Third, the verity-application public DID and verkey a retrieved from the
     * verity-application instance referenced by the given verkey url. After these two step, a given fields are loaded
     * into a blank ContextBuilder and returned.
     * @param walletConfig the wallet config for accessing the local wallet
     * @param verityUrl the url for the targeted instance of the verity-application
     * @return a ContextBuilder with given, retrieved and created fields
     * @throws IOException when retrieving info from the verity-application fails (bad url, unreachable, etc)
     * @throws WalletException when wallet operations fail
     */
    public static Context fromScratch(WalletConfig walletConfig,
                                      String verityUrl) throws IOException, WalletException {
        return fromScratch(walletConfig, verityUrl, null);
    }

    /**
     * Initializes a ContextBuilder from scratch for a verity-sdk without a provisioned agent on the
     * verity-application. This involves three main steps. First, a new local wallet is created
     * using the given wallet config. Second, a new local key-pair is created in the new created wallet that will
     * be used by the verity-sdk. Third, the verity-application public DID and verkey a retrieved from the
     * verity-application instance referenced by the given verkey url. After these two step, a given fields are loaded
     * into a blank ContextBuilder and returned.
     * @param walletConfig the wallet config for accessing the local wallet
     * @param verityUrl the url for the targeted instance of the verity-application
     * @param seed the seed used to generate the local key-pair
     * @return a ContextBuilder with given, retrieved and created fields
     * @throws IOException when retrieving info from the verity-application fails (bad url, unreachable, etc)
     * @throws WalletException when wallet operations fail
     */
    public static Context fromScratch(WalletConfig walletConfig,
                                      String verityUrl,
                                      String seed) throws IOException, WalletException {
        tryCreateWallet(walletConfig);
        return scratchContext(walletConfig, verityUrl, seed);
    }

    /**
     * Initializes a ContextBuilder from a String that contains JSON. This static method is used to rebuild
     * a Context object that was converted to JSON ( see: {@link Context#toJson()} )
     * @param json a String that contains properly formatted JSON
     * @return a ContextBuilder that contains the data contained in the given JSON
     */
    public static ContextBuilder fromJson(String json) {
        return new ContextBuilder().json(json);
    }

    /**
     * Initializes a ContextBuilder from a JSON object. This static method is used to rebuild
     * a Context object that was converted to JSON ( see: {@link Context#toJson()} )
     * @param json a JSON object that contains properly formatted JSON
     * @return a ContextBuilder that contains the data contained in the given JSON
     */
    public static ContextBuilder fromJson(JSONObject json) {
        return new ContextBuilder().json(json);
    }

    /**
     * Initializes a completely blank ContextBuilder
     * @return a blank ContextBuilder
     */
    public static ContextBuilder blank() {
        return new ContextBuilder();
    }

    /**
     * Loads the fields contained in the given JSON String into this ContextBuilder. The JSON String should be
     * from a Context object that was converted to JSON ( see: {@link Context#toJson()} )
     * @param json a String that contains properly formatted JSON
     * @return this ContextBuilder with loaded fields
     */
    public ContextBuilder json(String json) {return json(new JSONObject(json));}

    /**
     * Loads the fields contained in the given JSON Object into this ContextBuilder. The JSON Object should be
     * from a Context object that was converted to JSON ( see: {@link Context#toJson()} )
     * @param json a JSON Object that contains properly formatted JSON
     * @return this ContextBuilder with loaded fields
     */
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


    /**
     * Adds given wallet config to this ContextBuilder. The wallet config defines how access to the wallet is achieved.
     * @param config the given wallet config
     * @return this ContextBuilder with added field
     */
    public ContextBuilder walletConfig(WalletConfig config) {walletConfig = config; return this;}

    /**
     * Adds the given Verity url to this ContextBuilder. The Verity url is the url to the verity-application that is
     * intended to be used by the build Context.
     * @param val the given verity-application url
     * @return this ContextBuilder with added field
     */
    public ContextBuilder verityUrl(String val) {return putElement(VERITY_URL, val);}

    /**
     * Adds the given public DID to this ContextBuilder. This public DID is normally retrieved from the
     * verity-application. It is the public DID for the verity-application as a whole and is the same value for all
     * agents on that application and the verity-sdks that connect to it.
     * @param val the given public DID
     * @return this ContextBuilder with added field
     */
    public ContextBuilder verityPublicDID(String val) {return putElement(VERITY_PUBLIC_DID, val);}

    /**
     * Adds the given public verkey to this ContextBuilder. This public verkey is normally retrieved from the
     * verity-application. It is the public verkey for the verity-application as a whole and is the same value for all
     * agents on that application and the verity-sdks that connect to it.
     * @param val the given public verkey
     * @return this ContextBuilder with added field
     */
    public ContextBuilder verityPublicVerKey(String val) {return putElement(VERITY_PUBLIC_VER_KEY, val);}

    /**
     * Adds the given domain DID to this ContextBuilder. The domain DID is a unique identifier for the identity domain
     * as a whole. It is common between all agents and verity-sdks (an their Context objects).
     * @param val the given domain DID
     * @return this ContextBuilder with added field
     */
    public ContextBuilder domainDID(String val) {return putElement(DOMAIN_DID, val);}

    /**
     * Adds the given verity agent verkey to this ContextBuilder. This verkey is the key used by the agent hosted on
     * the verity-application.
     * @param val the given verity agent verkey
     * @return this ContextBuilder with added field
     */
    public ContextBuilder verityAgentVerKey(String val) {return putElement(VERITY_AGENT_VER_KEY, val);}

    /**
     * Adds the given verity sdk verkey id to this ContextBuilder. This key id is a simple identifier for the locally held key that is used
     * by the verity-sdk. It is used to find this key in the wallet.
     * @param val the given verity sdk verkey id
     * @return this ContextBuilder with added field
     */
    public ContextBuilder sdkVerKeyId(String val) {return putElement(SDK_VER_KEY_ID, val);}

    /**
     * Adds the given verity sdk verkey to this ContextBuilder. This is the locally held verkey (public) that is used by the verity-sdk. There
     * must be an entry in the wallet for this key. And a corresponding private key in the wallet too.
     * @param val the given verity sdk verkey
     * @return this ContextBuilder with added field
     */
    public ContextBuilder sdkVerKey(String val) {return putElement(SDK_VER_KEY, val);}

    /**
     * Adds the given endpoint url to this ContextBuilder. This endpoint is a location that messages can be sent from
     * the verity-application. This endpoint must be reachable by the verity-application.
     * @param val the given endpoint url
     * @return this ContextBuilder with added field
     */
    public ContextBuilder endpointUrl(String val) {return putElement(ENDPOINT_URL, val);}


    /**
     * Creates the immutable Context object from the fields held in this ContextBuilder.
     *
     * @return a built Context instance from this builder.
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

    //********************
    // NON-PUBLIC METHODS
    //********************
    void walletHandle(Wallet val) {
        walletHandle = val;
    }



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

    private final Map<String, String> elements = new HashMap<>();
    private WalletConfig walletConfig;
    private Wallet walletHandle = null;

    private ContextBuilder() {}

    private ContextBuilder putElement(String key, String val) {
        elements.put(key, val);
        return this;
    }

    private void putElementIgnoreNull(String key, String val) {
        if(val != null) {
            putElement(key, val);
        }
    }
}


using System;
using System.Collections.Generic;
using System.Json;
using VeritySDK.Wallets;

namespace VeritySDK.Utils
{
    /// <summary>
    /// A builder pattern object for initializing Context objects.
    /// </summary>
    public class ContextBuilder
    {

        /// <summary>
        /// Initializes a ContextBuilder from scratch for a verity-sdk that is using an already provisioned agent on
        /// the verity-application. This involves three main steps. First, a new local wallet is created
        /// using the given wallet id and key. Second, a new local key-pair is created in the new created wallet that will
        /// be used by the verity-sdk. Third, the verity-application public DID and verkey a retrieved from the
        /// verity-application instance referenced by the given verkey url. After these two step, a given fields are loaded
        /// into a blank ContextBuilder and returned.
        /// </summary>
        /// <param name="walletId">the id for the local wallet</param>
        /// <param name="walletKey">the key (credential) for the local wallet</param>
        /// <param name="verityUrl">the url for the targeted instance of the verity-application</param>
        /// <param name="domainDID">the domain DID for the agent already provisioned</param>
        /// <param name="verityAgentVerKey">the verkey for the agent already provisioned</param>
        /// <return>a ContextBuilder with given, retrieved and created fields</return>
        public static Context fromScratch(string walletId,
                                          string walletKey,
                                          string verityUrl,
                                          string domainDID,
                                          string verityAgentVerKey) 
        {
            return fromScratch(walletId, walletKey, verityUrl, domainDID, verityAgentVerKey, null);
        }

        /// <summary>
        /// Initializes a ContextBuilder from scratch for a verity-sdk that is using an already provisioned agent on
        /// the verity-application. This involves three main steps. First, a new local wallet is created
        /// using the given wallet id and key. Second, a new local key-pair is created in the new created wallet that will
        /// be used by the verity-sdk. Third, the verity-application public DID and verkey a retrieved from the
        /// verity-application instance referenced by the given verkey url. After these two step, a given fields are loaded
        /// into a blank ContextBuilder and returned.
        /// </summary>
        /// <param name="walletId">the id for the local wallet</param>
        /// <param name="walletKey">the key (credential) for the local wallet</param>
        /// <param name="verityUrl">the url for the targeted instance of the verity-application</param>
        /// <param name="domainDID">the domain DID for the agent already provisioned</param>
        /// <param name="verityAgentVerKey">the verkey for the agent already provisioned</param>
        /// <param name="seed">the seed used to generate the local key-pair</param>
        /// <return>a ContextBuilder with given, retrieved and created fields</return>
        public static Context fromScratch(string walletId,
                                          string walletKey,
                                          string verityUrl,
                                          string domainDID,
                                          string verityAgentVerKey,
                                          string seed)
        {
            WalletUtil.tryCreateWallet(walletId, walletKey);
            Context inter = scratchContext(DefaultWalletConfig.build(walletId, walletKey), verityUrl, seed);
            return withProvisionedAgent(inter, domainDID, verityAgentVerKey);
        }

        /// <summary>
        /// Initializes a ContextBuilder from scratch for a verity-sdk that is using an already provisioned agent on
        /// the verity-application. This involves three main steps. First, a new local wallet is created
        /// using the given wallet id and key. Second, a new local key-pair is created in the new created wallet that will
        /// be used by the verity-sdk. Third, the verity-application public DID and verkey a retrieved from the
        /// verity-application instance referenced by the given verkey url. After these two step, a given fields are loaded
        /// into a blank ContextBuilder and returned.
        /// </summary>
        /// <param name="walletId">the id for the local wallet</param>
        /// <param name="walletKey">the key (credential) for the local wallet</param>
        /// <param name="verityUrl">the url for the targeted instance of the verity-application</param>
        /// <param name="domainDID">the domain DID for the agent already provisioned</param>
        /// <param name="verityAgentVerKey">the verkey for the agent already provisioned</param>
        /// <param name="seed">the seed used to generate the local key-pair</param>
        /// <param name="walletPath">custom path where libindy wallet should be stored (default is ~/.indy_client/wallet/)</param>
        /// <return>a ContextBuilder with given, retrieved and created fields</return>
        public static Context fromScratch(string walletId,
                                          string walletKey,
                                          string verityUrl,
                                          string domainDID,
                                          string verityAgentVerKey,
                                          string seed,
                                          string walletPath) 
        {
            WalletUtil.tryCreateWallet(walletId, walletKey, walletPath);
            Context inter = scratchContext(DefaultWalletConfig.build(walletId, walletKey, walletPath), verityUrl, seed);
            return withProvisionedAgent(inter, domainDID, verityAgentVerKey);
        }

        /// <summary>
        /// Initializes a ContextBuilder from scratch for a verity-sdk that is using an already provisioned agent on
        /// the verity-application. This involves three main steps. First, a new local wallet is created
        /// using the given wallet config. Second, a new local key-pair is created in the new created wallet that will
        /// be used by the verity-sdk. Third, the verity-application public DID and verkey a retrieved from the
        /// verity-application instance referenced by the given verkey url. After these two step, a given fields are loaded
        /// into a blank ContextBuilder and returned.
        /// </summary>
        /// <param name="walletConfig">the wallet config for accessing the local wallet</param>
        /// <param name="verityUrl">the url for the targeted instance of the verity-application</param>
        /// <param name="domainDID">the domain DID for the agent already provisioned</param>
        /// <param name="verityAgentVerKey">the verkey for the agent already provisioned</param>
        /// <return>a ContextBuilder with given, retrieved and created fields</return>
        public static Context fromScratch(WalletConfig walletConfig,
                                          string verityUrl,
                                          string domainDID,
                                          string verityAgentVerKey)
        {
            return fromScratch(walletConfig, verityUrl, domainDID, verityAgentVerKey, null);
        }

        /// <summary>
        /// Initializes a ContextBuilder from scratch for a verity-sdk that is using an already provisioned agent on
        /// the verity-application. This involves three main steps. First, a new local wallet is created
        /// using the given wallet config. Second, a new local key-pair is created in the new created wallet that will
        /// be used by the verity-sdk. Third, the verity-application public DID and verkey a retrieved from the
        /// verity-application instance referenced by the given verkey url. After these two step, a given fields are loaded
        /// into a blank ContextBuilder and returned.
        /// </summary>
        /// <param name="walletConfig">the wallet config for accessing the local wallet</param>
        /// <param name="verityUrl">the url for the targeted instance of the verity-application</param>
        /// <param name="domainDID">the domain DID for the agent already provisioned</param>
        /// <param name="verityAgentVerKey">the verkey for the agent already provisioned</param>
        /// <param name="seed">the seed used to generate the local key-pair</param>
        /// <return>a ContextBuilder with given, retrieved and created fields</return>
        public static Context fromScratch(WalletConfig walletConfig,
                                          string verityUrl,
                                          string domainDID,
                                          string verityAgentVerKey,
                                          string seed) 
        {
            WalletUtil.tryCreateWallet(walletConfig);
            Context inter = scratchContext(walletConfig, verityUrl, seed);
            return withProvisionedAgent(inter, domainDID, verityAgentVerKey);
        }

        /// <summary>
        /// Initializes a ContextBuilder from scratch for a verity-sdk without a provisioned agent on the
        /// verity-application. This involves three main steps. First, a new local wallet is created
        /// using the given wallet id and key. Second, a new local key-pair is created in the new created wallet that will
        /// be used by the verity-sdk. Third, the verity-application public DID and verkey a retrieved from the
        /// verity-application instance referenced by the given verkey url. After these two step, a given fields are loaded
        /// into a blank ContextBuilder and returned.
        /// </summary>
        /// <param name="walletId">the id for the local wallet</param>
        /// <param name="walletKey">the key (credential) for the local wallet</param>
        /// <param name="verityUrl">the url for the targeted instance of the verity-application</param>
        /// <return>a ContextBuilder with given, retrieved and created fields</return>
        public static Context fromScratch(string walletId,
                                          string walletKey,
                                          string verityUrl)
        {
            return fromScratch(walletId, walletKey, verityUrl, null);
        }

        /// <summary>
        /// Initializes a ContextBuilder from scratch for a verity-sdk without a provisioned agent on the
        /// verity-application. This involves three main steps. First, a new local wallet is created
        /// using the given wallet id and key. Second, a new local key-pair is created in the new created wallet that will
        /// be used by the verity-sdk. Third, the verity-application public DID and verkey a retrieved from the
        /// verity-application instance referenced by the given verkey url. After these two step, a given fields are loaded
        /// into a blank ContextBuilder and returned.
        /// </summary>
        /// <param name="walletId">the id for the local wallet</param>
        /// <param name="walletKey">the key (credential) for the local wallet</param>
        /// <param name="verityUrl">the url for the targeted instance of the verity-application</param>
        /// <param name="seed">the seed used to generate the local key-pair</param>
        /// <return>a ContextBuilder with given, retrieved and created fields</return>
        public static Context fromScratch(string walletId,
                                          string walletKey,
                                          string verityUrl,
                                          string seed) 
        {
            WalletUtil.tryCreateWallet(walletId, walletKey);
            return scratchContext(DefaultWalletConfig.build(walletId, walletKey), verityUrl, seed);
        }

        /// <summary>
        /// Initializes a ContextBuilder from scratch for a verity-sdk without a provisioned agent on the
        /// verity-application. This involves three main steps. First, a new local wallet is created
        /// using the given wallet config. Second, a new local key-pair is created in the new created wallet that will
        /// be used by the verity-sdk. Third, the verity-application public DID and verkey a retrieved from the
        /// verity-application instance referenced by the given verkey url. After these two step, a given fields are loaded
        /// into a blank ContextBuilder and returned.
        /// </summary>
        /// <param name="walletConfig">the wallet config for accessing the local wallet</param>
        /// <param name="verityUrl">the url for the targeted instance of the verity-application</param>
        /// <return>a ContextBuilder with given, retrieved and created fields</return>
        public static Context fromScratch(WalletConfig walletConfig,
                                          string verityUrl) 
        {
            return fromScratch(walletConfig, verityUrl, null);
        }

        /// <summary>
        /// Initializes a ContextBuilder from scratch for a verity-sdk without a provisioned agent on the
        /// verity-application. This involves three main steps. First, a new local wallet is created
        /// using the given wallet config. Second, a new local key-pair is created in the new created wallet that will
        /// be used by the verity-sdk. Third, the verity-application public DID and verkey a retrieved from the
        /// verity-application instance referenced by the given verkey url. After these two step, a given fields are loaded
        /// into a blank ContextBuilder and returned.
        /// </summary>
        /// <param name="walletConfig">the wallet config for accessing the local wallet</param>
        /// <param name="verityUrl">the url for the targeted instance of the verity-application</param>
        /// <param name="seed">the seed used to generate the local key-pair</param>
        /// <return>a ContextBuilder with given, retrieved and created fields</return>
        public static Context fromScratch(WalletConfig walletConfig,
                                          string verityUrl,
                                          string seed) 
        {
            WalletUtil.tryCreateWallet(walletConfig);
            return scratchContext(walletConfig, verityUrl, seed);
        }

        /// <summary>
        /// Initializes a ContextBuilder from a string that contains JSON. This static method is used to rebuild
        /// a Context object that was converted to JSON ( see: {@link Context#toJson()} )
        /// </summary>
        /// <param name="json">a string that contains properly formatted JSON</param>
        /// <return>a ContextBuilder that contains the data contained in the given JSON</return>
        public static ContextBuilder fromJson(string json)
        {
            return new ContextBuilder().json(json);
        }

        /// <summary>
        /// Initializes a ContextBuilder from a JSON object. This static method is used to rebuild
        /// a Context object that was converted to JSON ( see: {@link Context#toJson()} )
        /// </summary>
        /// <param name="json">a JSON object that contains properly formatted JSON</param>
        /// <return>a ContextBuilder that contains the data contained in the given JSON</return>
        public static ContextBuilder fromJson(JsonObject json)
        {
            return new ContextBuilder().json(json);
        }

        /// <summary>
        /// Initializes a completely blank ContextBuilder
        /// </summary>
        /// <return>a blank ContextBuilder</return>
        public static ContextBuilder blank()
        {
            return new ContextBuilder();
        }

        /// <summary>
        /// Loads the fields contained in the given JSON string into this ContextBuilder. The JSON string should be
        /// from a Context object that was converted to JSON ( see: {@link Context#toJson()} )
        /// </summary>
        /// <param name="json">a string that contains properly formatted JSON</param>
        /// <return>this ContextBuilder with loaded fields</return>
        public ContextBuilder json(string json)
        {
            JsonObject jsonObject = JsonObject.Parse(json) as JsonObject;
            return this.json(jsonObject);
        }

        /// <summary>
        /// Loads the fields contained in the given JSON Object into this ContextBuilder. The JSON Object should be
        /// from a Context object that was converted to JSON ( see: {@link Context#toJson()} )
        /// </summary>
        /// <param name="json">a JSON Object that contains properly formatted JSON</param>
        /// <return>this ContextBuilder with loaded fields</return>
        public ContextBuilder json(JsonObject json)
        {
            WalletConfig w = DefaultWalletConfig.build(
                    json.GetValue("walletName"),
                    json.GetValue("walletKey"),
                    json.GetValue("walletPath")
            );
            this.walletConfig(w);
            putElementIgnoreNull(ContextConstants.ENDPOINT_URL, json.GetValue(ContextConstants.ENDPOINT_URL));
            putElementIgnoreNull(ContextConstants.VERITY_URL, json.GetValue(ContextConstants.VERITY_URL));
            string version = json.GetValue(ContextConstants.VERSION);
            if (ContextConstants.V_0_2.Equals(version))
            {
                return json_0_2(json);
            }
            return json_0_1(json);
        }

        private ContextBuilder json_0_2(JsonObject json)
        {
            putElementIgnoreNull(ContextConstants.VERITY_PUBLIC_DID, json.GetValue(ContextConstants.VERITY_PUBLIC_DID));
            putElementIgnoreNull(ContextConstants.VERITY_PUBLIC_VER_KEY, json.GetValue(ContextConstants.VERITY_PUBLIC_VER_KEY));
            putElementIgnoreNull(ContextConstants.DOMAIN_DID, json.GetValue(ContextConstants.DOMAIN_DID));
            putElementIgnoreNull(ContextConstants.VERITY_AGENT_VER_KEY, json.GetValue(ContextConstants.VERITY_AGENT_VER_KEY));
            putElementIgnoreNull(ContextConstants.SDK_VER_KEY_ID, json.GetValue(ContextConstants.SDK_VER_KEY_ID));
            putElementIgnoreNull(ContextConstants.SDK_VER_KEY, json.GetValue(ContextConstants.SDK_VER_KEY));
            putElementIgnoreNull(ContextConstants.VERSION, ContextConstants.V_0_2);

            return this;
        }

        private ContextBuilder json_0_1(JsonObject json)
        {
            putElementIgnoreNull(ContextConstants.VERITY_PUBLIC_DID, json.GetValue(ContextConstants.VERITY_PUBLIC_DID));
            putElementIgnoreNull(ContextConstants.VERITY_PUBLIC_VER_KEY, json.GetValue(ContextConstants.LEGACY_VERITY_PUBLIC_VER_KEY));
            putElementIgnoreNull(ContextConstants.DOMAIN_DID, json.GetValue(ContextConstants.LEGACY_DOMAIN_DID));
            putElementIgnoreNull(ContextConstants.VERITY_AGENT_VER_KEY, json.GetValue(ContextConstants.LEGACY_VERITY_AGENT_VER_KEY));
            putElementIgnoreNull(ContextConstants.SDK_VER_KEY_ID, json.GetValue(ContextConstants.LEGACY_SDK_VER_KEY_ID));
            putElementIgnoreNull(ContextConstants.SDK_VER_KEY, json.GetValue(ContextConstants.LEGACY_SDK_VER_KEY));
            putElementIgnoreNull(ContextConstants.VERSION, ContextConstants.V_0_2); // Converts to latest version -- NOT current version
            return this;
        }


        /// <summary>
        /// Adds given wallet config to this ContextBuilder. The wallet config defines how access to the wallet is achieved.
        /// </summary>
        /// <param name="config">the given wallet config</param>
        /// <return>this ContextBuilder with added field</return>
        public ContextBuilder walletConfig(WalletConfig config) { _walletConfig = config; return this; }

        /// <summary>
        /// Adds the given Verity url to this ContextBuilder. The Verity url is the url to the verity-application that is
        /// intended to be used by the build Context.
        /// </summary>
        /// <param name="val">the given verity-application url</param>
        /// <return>this ContextBuilder with added field</return>
        public ContextBuilder verityUrl(string val) { return putElement(ContextConstants.VERITY_URL, val); }

        /// <summary>
        /// Adds the given public DID to this ContextBuilder. This public DID is normally retrieved from the
        /// verity-application. It is the public DID for the verity-application as a whole and is the same value for all
        /// agents on that application and the verity-sdks that connect to it.
        /// </summary>
        /// <param name="val">the given public DID</param>
        /// <return>this ContextBuilder with added field</return>
        public ContextBuilder verityPublicDID(string val) { return putElement(ContextConstants.VERITY_PUBLIC_DID, val); }

        /// <summary>
        /// Adds the given public verkey to this ContextBuilder. This public verkey is normally retrieved from the
        /// verity-application. It is the public verkey for the verity-application as a whole and is the same value for all
        /// agents on that application and the verity-sdks that connect to it.
        /// </summary>
        /// <param name="val">the given public verke</param>
        /// <return>this ContextBuilder with added field</return>
        public ContextBuilder verityPublicVerKey(string val) { return putElement(ContextConstants.VERITY_PUBLIC_VER_KEY, val); }

        /// <summary>
        /// Adds the given domain DID to this ContextBuilder. The domain DID is a unique identifier for the identity domain
        /// as a whole. It is common between all agents and verity-sdks (an their Context objects).
        /// </summary>
        /// <param name="val">the given domain DID</param>
        /// <return>this ContextBuilder with added field</return>
        public ContextBuilder domainDID(string val) { return putElement(ContextConstants.DOMAIN_DID, val); }

        /// <summary>
        /// Adds the given verity agent verkey to this ContextBuilder. This verkey is the key used by the agent hosted on
        /// the verity-application.
        /// </summary>
        /// <param name="val">the given verity agent verkey</param>
        /// <return>this ContextBuilder with added field</return>
        public ContextBuilder verityAgentVerKey(string val) { return putElement(ContextConstants.VERITY_AGENT_VER_KEY, val); }

        /// <summary>
        /// Adds the given verity sdk verkey id to this ContextBuilder. This key id is a simple identifier for the locally held key that is used
        /// by the verity-sdk. It is used to find this key in the wallet.
        /// </summary>
        /// <param name="val">the given verity sdk verkey id</param>
        /// <return>this ContextBuilder with added field</return>
        public ContextBuilder sdkVerKeyId(string val) { return putElement(ContextConstants.SDK_VER_KEY_ID, val); }

        /// <summary>
        /// Adds the given verity sdk verkey to this ContextBuilder. This is the locally held verkey (public) that is used by the verity-sdk. There
        /// must be an entry in the wallet for this key. And a corresponding private key in the wallet too.
        /// </summary>
        /// <param name="val">the given verity sdk verkey</param>
        /// <return>this ContextBuilder with added field</return>
        public ContextBuilder sdkVerKey(string val) { return putElement(ContextConstants.SDK_VER_KEY, val); }

        /// <summary>
        /// Adds the given endpoint url to this ContextBuilder. This endpoint is a location that messages can be sent from
        /// the verity-application. This endpoint must be reachable by the verity-application.
        /// </summary>
        /// <param name="val">the given endpoint url</param>
        /// <return>this ContextBuilder with added field</return>
        public ContextBuilder endpointUrl(string val) { return putElement(ContextConstants.ENDPOINT_URL, val); }


        /// <summary>
        /// Creates the immutable Context object from the fields held in this ContextBuilder.
        /// </summary>
        /// <return>a built Context instance from this builder.</return>
        public Context build() 
        {
            if (_walletHandle == null)
            {
                return new Context(
                        _walletConfig,
                        _elements.Get(ContextConstants.VERSION),
                        _elements.Get(ContextConstants.VERITY_URL),
                        _elements.Get(ContextConstants.VERITY_PUBLIC_DID),
                        _elements.Get(ContextConstants.VERITY_PUBLIC_VER_KEY),
                        _elements.Get(ContextConstants.DOMAIN_DID),
                        _elements.Get(ContextConstants.VERITY_AGENT_VER_KEY),
                        _elements.Get(ContextConstants.SDK_VER_KEY_ID),
                        _elements.Get(ContextConstants.SDK_VER_KEY),
                        _elements.Get(ContextConstants.ENDPOINT_URL)
                );
            }
            else
            {
                return new Context(
                        _walletConfig,
                        _elements.Get(ContextConstants.VERSION),
                        _elements.Get(ContextConstants.VERITY_URL),
                        _elements.Get(ContextConstants.VERITY_PUBLIC_DID),
                        _elements.Get(ContextConstants.VERITY_PUBLIC_VER_KEY),
                        _elements.Get(ContextConstants.DOMAIN_DID),
                        _elements.Get(ContextConstants.VERITY_AGENT_VER_KEY),
                        _elements.Get(ContextConstants.SDK_VER_KEY_ID),
                        _elements.Get(ContextConstants.SDK_VER_KEY),
                        _elements.Get(ContextConstants.ENDPOINT_URL),
                        _walletHandle
                );
            }
        }

        public void walletHandle(Hyperledger.Indy.WalletApi.Wallet val) { _walletHandle = val; }


        public static Context scratchContext(WalletConfig wallet, string verityUrl, string seed) 
        {
            Did verityDid = VerityUtil.retrieveVerityPublicDid(verityUrl);

            return scratchContext(wallet, verityUrl, verityDid, seed);
        }

        public static Context withProvisionedAgent(Context inter, string domainDID, string verityAgentVerKey) 
        {
            return inter.ToContextBuilder()
                    .domainDID(domainDID)
                    .verityAgentVerKey(verityAgentVerKey)
                    .build();
        }

        public static Context scratchContext(WalletConfig wallet, string verityUrl, Did verityDid, string seed)
        {
            Context inter = new ContextBuilder()
                .verityPublicDID(verityDid.did)
                .verityPublicVerKey(verityDid.verkey)
                .walletConfig(wallet)
                .verityUrl(verityUrl)
                .build();

            Did mime = Did.createNewDid(inter.WalletHandle(), seed);

            return inter.ToContextBuilder()
                    .sdkVerKeyId(mime.did)
                    .sdkVerKey(mime.verkey)
                    .build();
        }

        private Dictionary<string, string> _elements = new Dictionary<string, string>();
        private WalletConfig _walletConfig;
        private Hyperledger.Indy.WalletApi.Wallet _walletHandle = null;

        private ContextBuilder() { }

        private ContextBuilder putElement(string key, string val)
        {
            _elements[key] = val;
            return this;
        }

        private void putElementIgnoreNull(string key, string val)
        {
            if (val != null)
            {
                putElement(key, val);
            }
        }
    }
}

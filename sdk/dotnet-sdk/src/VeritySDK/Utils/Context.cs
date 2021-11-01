using Com.Evernym.Vdrtools;
using Com.Evernym.Vdrtools.CryptoApi;
using Com.Evernym.Vdrtools.WalletApi;
using SimpleBase;
using System;
using System.Json;
using System.Text;
using VeritySDK.Exceptions;
using VeritySDK.Wallets;

namespace VeritySDK.Utils
{
    /// <summary>
    /// This Context object holds the data for accessing an agent on a verity-application. A complete and correct data in
    /// the context allows for access and authentication to that agent.
    ///</summary>
    public class Context : AsJsonObject
    {
        private string version;

        private WalletConfig walletConfig;
        private Wallet walletHandle;
        private string endpointUrl;

        private string verityUrl;
        private string verityPublicDID;
        private string verityPublicVerKey;
        private string domainDID;
        private string verityAgentVerKey;
        private string sdkVerKeyId;
        private string sdkVerKey;

        bool walletClosedFlag = false;

        /// <summary>
        /// Not a public constructor! Allows work with ContextBuilder
        ///</summary>
        public Context(
            WalletConfig walletConfig,
            string version,
            string verityUrl,
            string verityPublicDID,
            string verityPublicVerKey,
            string domainDID,
            string verityAgentVerKey,
            string sdkVerKeyId,
            string sdkVerKey,
            string endpointUrl
        )
        {
            this.walletConfig = walletConfig;
            this.version = version;
            this.verityUrl = verityUrl;
            this.verityPublicDID = verityPublicDID;
            this.verityPublicVerKey = verityPublicVerKey;
            this.domainDID = domainDID;
            this.verityAgentVerKey = verityAgentVerKey;
            this.sdkVerKeyId = sdkVerKeyId;
            this.sdkVerKey = sdkVerKey;
            this.endpointUrl = endpointUrl;
            this.walletHandle = OpenWallet();
        }

        // Not a public constructor! Allows work with ContextBuilder
        public Context(
                WalletConfig walletConfig,
                string version,
                string verityUrl,
                string verityPublicDID,
                string verityPublicVerKey,
                string domainDID,
                string verityAgentVerKey,
                string sdkVerKeyId,
                string sdkVerKey,
                string endpointUrl,
                Wallet handle
        )
        {
            if (handle == null)
            {
                throw new WalletOpenException("Context can not be constructed without wallet handle");
            }

            this.walletConfig = walletConfig;
            this.version = version;
            this.verityUrl = verityUrl;
            this.verityPublicDID = verityPublicDID;
            this.verityPublicVerKey = verityPublicVerKey;
            this.domainDID = domainDID;
            this.verityAgentVerKey = verityAgentVerKey;
            this.sdkVerKeyId = sdkVerKeyId;
            this.sdkVerKey = sdkVerKey;
            this.endpointUrl = endpointUrl;
            this.walletHandle = handle;
        }

        private Wallet OpenWallet()
        {
            if (walletConfig == null)
            {
                throw new WalletOpenException("Unable to open wallet without wallet configuration.");
            }

            try
            {
                return Wallet.OpenWalletAsync(walletConfig.config(), walletConfig.credential()).GetAwaiter().GetResult();
            }
            catch (IndyException /*| ExecutionException | InterruptedException*/ e)
            {
                throw new WalletOpenException("Wallet failed to open", e);
            }
        }

        /// <summary>
        /// Closes the the open wallet handle stored inside the Context object.
        ///</summary>
        public void CloseWallet()
        {
            walletClosedFlag = true;
            try
            {
                walletHandle.CloseAsync().GetAwaiter().GetResult();
            }
            catch (Exception e) //when (e is IndyException || e is ExecutionException || e is InterruptedException),
            {
                throw new WalletException("Wallet failed to close", e);
            }
        }

        private T ThrowIfNull<T>(T val, string fieldName)
        {
            if (val == null)
                throw new UndefinedContextException($"Context field is used without definition-- {fieldName}");

            return val;
        }

        /// <summary>
        /// The WalletConfig expressed by this object
        ///</summary>
        /// <returns>the WalletConfig held</returns>
        public WalletConfig WalletConfig()
        {
            return ThrowIfNull<WalletConfig>(walletConfig, ContextConstants.WALLET_CONFIG);
        }

        /// <summary>
        /// The url for the verity-application that this object is connected to
        ///</summary>
        /// <returns>the url for the verity-application</returns>
        public string VerityUrl()
        {
            return ThrowIfNull<string>(verityUrl, ContextConstants.VERITY_URL);
        }

        /// <summary>
        /// The public identifier for the verity-application. This identifier is unique for each verity-application instance
        /// but is common for all agents on that instance.
        ///</summary>
        /// <returns>the public identifier (DID) for a verity-application instance</returns>
        public string VerityPublicDID()
        {
            return ThrowIfNull<string>(verityPublicDID, ContextConstants.VERITY_PUBLIC_DID);
        }

        /// <summary>
        /// The public verkey for the verity-application. This verkey is unique for each verity-application instance
        /// but is common for all agents on that instance.
        ///</summary>
        /// <returns>the public verkey for a verity-application instance</returns>
        public string VerityPublicVerKey()
        {
            return ThrowIfNull<string>(verityPublicVerKey, ContextConstants.VERITY_PUBLIC_VER_KEY);
        }

        /// <summary>
        ///The identifier for a identity domain. This identifier identifies a self-sovereign Identity. It will be common
        ///across all agents and controllers of that Identity.
        ///</summary>
        /// <returns>the Domain identifier (DID)</returns>
        public string DomainDID()
        {
            return ThrowIfNull<string>(domainDID, ContextConstants.DOMAIN_DID);
        }

        /// <summary>
        ///The verkey for the agent on the verity-application
        ///</summary>
        /// <returns>the verkey for the verity-application agent</returns>
        public string VerityAgentVerKey()
        {
            return ThrowIfNull<string>(verityAgentVerKey, ContextConstants.VERITY_AGENT_VER_KEY);
        }

        /// <summary>
        ///An id for the locally held sdk verkey
        ///</summary>
        /// <returns>the id for the sdk verkey</returns>
        public string SdkVerKeyId()
        {
            return ThrowIfNull<string>(sdkVerKeyId, ContextConstants.SDK_VER_KEY_ID);
        }

        /// <summary>
        ///The verkey for the locally held key-pair. A corresponding private key is held in the wallet for this verkey
        ///</summary>
        /// <returns>the verkey for the sdk</returns>
        public string SdkVerKey()
        {
            return ThrowIfNull<string>(sdkVerKey, ContextConstants.SDK_VER_KEY);
        }

        /// <summary>
        ///The endpoint for receiving messages from the agent on the verity-application. This endpoint must be registered
        ///using the UpdateEndpoint protocol to effectively change it.
        ///</summary>
        /// <returns>the endpoint contained in this context</returns>
        public string EndpointUrl()
        {
            return ThrowIfNull<string>(endpointUrl, ContextConstants.ENDPOINT_URL);
        }

        /// <summary>
        ///The context version
        ///</summary>
        /// <returns>the version</returns>
        public string Version()
        {
            return ThrowIfNull<string>(version, ContextConstants.VERSION);
        }

        /// <summary>
        ///Converts the local keys held in the context to REST api token. This token can be used with the REST API for the verity-application
        ///</summary>
        /// <returns>a REST API token</returns>
        public string RestApiToken()
        {
            try
            {
                string verkey = SdkVerKey();
                byte[] signature = Crypto.SignAsync(
                        WalletHandle(),
                        verkey,
                        Encoding.UTF8.GetBytes(verkey)
                ).GetAwaiter().GetResult();

                return verkey + ":" + Base58.Bitcoin.Encode(signature);
            }
            //catch (ThreadInterruptedException  | ExecutionException e) 
            catch (Exception e)
            {
                if (e.InnerException is IndyException)
                    throw (IndyException)e.InnerException;
                else
                {
                    throw new WalletException("Signing REST API key could not complete", e);
                }
            }
        }

        /// <summary>
        ///The open wallet handle used by this object.
        ///</summary>
        /// <returns>a wallet handle</returns>
        public Wallet WalletHandle()
        {
            if (walletClosedFlag)
            {
                throw new WalletClosedException();
            }
            return walletHandle;
        }

        /// <summary>
        ///Whether the wallet is closed or not
        ///</summary>
        /// <returns>true if the wallet is closed, otherwise false</returns>
        public bool WalletIsClosed()
        {
            return walletClosedFlag;
        }

        /// <summary>
        ///Builds a ContextBuilder based on this context. Since Context is immutable, this allows for changes by
        ///building a new Context using a ContextBuilder
        ///</summary>
        /// <returns>a ContextBuilder based on this context</returns>
        public ContextBuilder ToContextBuilder()
        {
            ContextBuilder rtn = ContextBuilder.blank();
            if (walletConfig != null) rtn.walletConfig(walletConfig);
            if (verityUrl != null) rtn.verityUrl(verityUrl);
            if (verityPublicDID != null) rtn.verityPublicDID(verityPublicDID);
            if (verityPublicVerKey != null) rtn.verityPublicVerKey(verityPublicVerKey);
            if (domainDID != null) rtn.domainDID(domainDID);
            if (verityAgentVerKey != null) rtn.verityAgentVerKey(verityAgentVerKey);
            if (sdkVerKeyId != null) rtn.sdkVerKeyId(sdkVerKeyId);
            if (sdkVerKey != null) rtn.sdkVerKey(sdkVerKey);
            if (endpointUrl != null) rtn.endpointUrl(endpointUrl);

            if (!walletClosedFlag)
            {
                rtn.walletHandle(walletHandle);
            }

            return rtn;
        }

        /// <summary>
        ///Converts this object into a JSON object
        ///</summary>
        /// <returns>an JSON object based on this context</returns>
        public JsonObject toJson()
        {
            JsonObject rtn = new JsonObject();

            if (walletConfig != null) walletConfig.addToJson(rtn);
            if (endpointUrl != null) rtn.Add(ContextConstants.ENDPOINT_URL, endpointUrl);
            if (verityUrl != null) rtn.Add(ContextConstants.VERITY_URL, verityUrl);
            if (verityPublicDID != null) rtn.Add(ContextConstants.VERITY_PUBLIC_DID, verityPublicDID);
            if (verityPublicVerKey != null) rtn.Add(ContextConstants.VERITY_PUBLIC_VER_KEY, verityPublicVerKey);
            if (domainDID != null) rtn.Add(ContextConstants.DOMAIN_DID, domainDID);
            if (verityAgentVerKey != null) rtn.Add(ContextConstants.VERITY_AGENT_VER_KEY, verityAgentVerKey);
            if (sdkVerKeyId != null) rtn.Add(ContextConstants.SDK_VER_KEY_ID, sdkVerKeyId);
            if (sdkVerKey != null) rtn.Add(ContextConstants.SDK_VER_KEY, sdkVerKey);

            if (version != null)
                rtn.Add(ContextConstants.VERSION, version);
            else
                rtn.Add(ContextConstants.VERSION, ContextConstants.V_0_2);

            return rtn;
        }
    }
}

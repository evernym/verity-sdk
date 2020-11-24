using System.Json;

namespace VeritySDK.Wallets
{
    /// <summary>
    /// The config object for the local default wallet provided by the Indy SDK
    /// </summary>
    public class DefaultWalletConfig : WalletConfig
    {
        /// <summary>
        /// Constructs a DefaultWalletConfig with the given basic parameters 
        /// </summary>
        /// <param name="id">the given name or identifier for the wallet</param>
        /// <param name="key">the given key (encrypting) for the wallet</param>
        /// <param name="path">the given path location where the wallet on disk file is found</param>
        /// <returns>a DefaultWalletConfig object</returns>
        public static DefaultWalletConfig build(string id, string key, string path)
        {
            return new DefaultWalletConfig(id, key, path);
        }

        /// <summary>
        /// Constructs a DefaultWalletConfig with the given basic parameters 
        /// </summary>
        /// <param name="id">the given name or identifier for the wallet</param>
        /// <param name="key">the given key (encrypting) for the wallet</param>
        /// <returns>a DefaultWalletConfig object</returns>
        public static DefaultWalletConfig build(string id, string key)
        {
            return new DefaultWalletConfig(id, key);
        }

        /// <summary>
        /// the name or identifier for the wallet
        /// </summary>
        public string id;

        /// <summary>
        /// the key (encrypting) for the wallet
        /// </summary>
        public string key;

        /// <summary>
        /// the path location where the wallet on disk file is found
        /// </summary>
        public string path;

        private DefaultWalletConfig(string id, string key)
        {
            this.id = id;
            this.key = key;
            this.path = null;
        }

        private DefaultWalletConfig(string id, string key, string path)
        {
            this.id = id;
            this.key = key;
            this.path = path;
        }

        /// <summary>
        /// Provides a default JSON config string 
        /// </summary>
        /// <returns>a JSON wallet config string</returns>
        public string config()
        {
            JsonObject rtn = new JsonObject();
            rtn.Add("id", this.id);
            if (path != null && !path.Trim().Equals(""))
            {
                JsonObject storageOptions = new JsonObject();
                storageOptions.Add("path", path);
                rtn.Add("storage_config", storageOptions);
            }
            return rtn.ToString();
        }

        /// <summary>
        /// Provides a default JSON credential string 
        /// </summary>
        /// <returns>a JSON wallet credential string</returns>
        public string credential()
        {
            JsonObject rtn = new JsonObject();
            rtn.Add("key", this.key);
            return rtn.ToString();
        }

        /// <summary>
        /// Injects the default wallet fields into a JSON Object 
        /// </summary>
        /// <param name="json">the JSON object that the fields are injected into</param>
        public void addToJson(JsonObject json)
        {
            if (id != null) json.Add("walletName", id);
            if (key != null) json.Add("walletKey", key);
            if (path != null) json.Add("walletPath", path);
        }
    }
}

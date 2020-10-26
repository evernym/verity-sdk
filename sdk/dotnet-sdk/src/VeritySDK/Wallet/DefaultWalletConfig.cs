using System.Json;

namespace VeritySDK
{
    /**
     * The config object for the local default wallet provided by the Indy SDK
     */
    public class DefaultWalletConfig : WalletConfig
    {
        /**
         * Constructs a DefaultWalletConfig with the given basic parameters
         *
         * @param id the given name or identifier for the wallet
         * @param key the given key (encrypting) for the wallet
         * @param path the given path location where the wallet on disk file is found
         * @return a DefaultWalletConfig object
         */
        public static DefaultWalletConfig build(string id, string key, string path)
        {
            return new DefaultWalletConfig(id, key, path);
        }

        /**
         * Constructs a DefaultWalletConfig with the given basic parameters
         * @param id the given name or identifier for the wallet
         * @param key the given key (encrypting) for the wallet
         * @return a DefaultWalletConfig object
         */
        public static DefaultWalletConfig build(string id, string key)
        {
            return new DefaultWalletConfig(id, key);
        }

        /**
         * the name or identifier for the wallet
         */
        public string id;
        /**
         * the key (encrypting) for the wallet
         */
        public string key;
        /**
         * the path location where the wallet on disk file is found
         */
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

        /**
         * Provides a default JSON config string
         *
         * @return a JSON wallet config string
         */
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

        /**
         * Provides a default JSON credential string
         *
         * @return a JSON wallet credential string
         */
        public string credential()
        {
            JsonObject rtn = new JsonObject();
            rtn.Add("key", this.key);
            return rtn.ToString();
        }

        /**
         * Injects the default wallet fields into a JSON Object
         *
         * @param json the JSON object that the fields are injected into
         */
        public void addToJson(JsonObject json)
        {
            if (id != null) json.Add("walletName", id);
            if (key != null) json.Add("walletKey", key);
            if (path != null) json.Add("walletPath", path);
        }
    }
}

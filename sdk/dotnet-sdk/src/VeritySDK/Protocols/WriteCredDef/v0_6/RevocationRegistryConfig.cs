using System;
using System.Json;

namespace VeritySDK
{
    /**
     * Holder of revocation registry configuration. This class holds a JSON object and is not intended to be created
     * directly. The versioned WriteCredentialDefinition interface provides helper functions to create this holder
     * class.
     *
     * @see WriteCredentialDefinitionV0_6#disabledRegistryConfig()
     */
    public class RevocationRegistryConfig : AsJsonObject
    {
        private JsonObject data;

        /**
         * Constructs the object with the given JSON object
         *
         * @param data the given configuration as expressed in JSON object
         */
        public RevocationRegistryConfig(JsonObject data)
        {
            this.data = data;
        }

        /**
         * Convert this object to a JSON object
         *
         * @return this object as a JSON object
         */
        public JsonObject toJson()
        {
            return data;
        }
    }
}
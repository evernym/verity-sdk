using System;
using System.Json;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.WriteCredDef
{
    /// <summary>
    /// Holder of revocation registry configuration. This class holds a JSON object and is not intended to be created
    /// directly. The versioned WriteCredentialDefinition interface provides helper functions to create this holder class.
    /// </summary>
    public class RevocationRegistryConfig : AsJsonObject
    {
        private JsonObject data;

        /// <summary>
        /// Constructs the object with the given JSON object 
        /// </summary>
        /// <param name="data">the given configuration as expressed in JSON object</param>
        public RevocationRegistryConfig(JsonObject data)
        {
            this.data = data;
        }

        /// <summary>
        /// Convert this object to a JSON object
        /// </summary>
        /// <returns>this object as a JSON object</returns>
        public JsonObject toJson()
        {
            return data;
        }
    }
}
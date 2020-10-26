using System;
using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// The generic interface for the wallet configuration for the Indy-SDK wallet
    /// </summary>
    public interface WalletConfig
    {
        /// <summary>
        /// Provides a JSON string that is valid config for the Indy-SDK wallet API
        /// </summary>
        /// <returns>JSON wallet config string</returns>
        String config();

        /// <summary>
        /// Provides a JSON string that is valid credential for the Indy-SDK wallet API
        /// </summary>
        /// <returns>JSON wallet credential string</returns>
        String credential();

        /// <summary>
        /// Add relevant fields to a JSON object to express this configuration
        /// </summary>
        /// <returns>json the JSON object that the fields are injected into</returns>
        void addToJson(JsonObject json);
    }
}

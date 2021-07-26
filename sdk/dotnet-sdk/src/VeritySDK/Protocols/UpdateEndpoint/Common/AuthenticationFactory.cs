using System.Json;
using System.Collections.Generic;
using System.Security.Policy;

namespace VeritySDK.Protocols.UpdateEndpoint
{
    /// <summary>
    /// A factory for a Authentication expression
    /// </summary>
    public class AuthenticationFactory
    {
        /// <summary>
        /// Create authentication parameter for update endpoint which require OAuth2 auth.
        /// This is for version v1 of OAuth2 for verity.
        /// </summary>
        /// <param name="tokenUrl">Url on which token is requested</param>
        /// <param name="data">
        /// Url on which token is requested
        ///     {
        ///         "grant_type": "client_credentials",
        ///         "client_id": "ajeyKDizsDkwYeEmRmHU78gf7W3VIEoA",
        ///         "client_secret": "aaGxxcGi6kb6AxIe"
        ///     }
        /// </param>
        /// <returns>Authentication instance</returns>
        public static Authentication CreateOAuth2V1(Url tokenUrl, Dictionary<string, string> data) {
            JsonObject jsonData = new JsonObject();
            jsonData.Add("url", tokenUrl.ToString());
            foreach(var item in data)
            {
                jsonData.Add(item.Key, item.Value);
            }
            return new Authentication("OAuth2", "v1", jsonData);
        }
    }
}
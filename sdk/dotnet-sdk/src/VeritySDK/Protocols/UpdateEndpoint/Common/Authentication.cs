using System.Json;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.UpdateEndpoint
{
    /// <summary>
    /// A holder for a Authentication expression
    /// </summary>
    public class Authentication : AsJsonObject
    {
        JsonObject json;

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="type">Type of authentication</param>
        /// <param name="version">Version of authentication implementation</param>
        /// <param name="data">Additional data</param>
        public Authentication(string type, string version, JsonObject data)
        {
            this.json = new JsonObject();
            this.json.Add("type", type);
            this.json.Add("version", version);
            this.json.Add("data", data);
        }

        /// <summary>
        /// Convert this object to a JSON object
        /// </summary>
        /// <returns>this object as a JSON object</returns>
        public JsonObject toJson()
        {
            return this.json;
        }
    }
}

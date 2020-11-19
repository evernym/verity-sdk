using System.Json;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.PresentProof
{
    /// <summary>
    /// A holder for a restriction expression
    /// </summary>
    public class Restriction : AsJsonObject
    {
        JsonObject data;

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="data">Restrinction as SSON object</param>
        public Restriction(JsonObject data)
        {
            this.data = data;
        }

        /// <summary>
        /// Convert this object to a JSON object 
        /// </summary>
        /// <returns>this object as a JSON object</returns>
        public JsonObject toJson()
        {
            return this.data;
        }
    }
}
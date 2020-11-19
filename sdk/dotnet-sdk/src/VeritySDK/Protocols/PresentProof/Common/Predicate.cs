using System.Json;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.PresentProof
{
    /// <summary>
    /// A holder for an predicate based restrictions
    /// </summary>
    public class Predicate : AsJsonObject
    {
        JsonObject data;

        /// <summary>
        /// Constructs the Predicate object with the given attribute name, value and given restrictions 
        /// </summary>
        /// <param name="name">the attribute name</param>
        /// <param name="value">the value the given attribute must be greater than</param>
        /// <param name="restrictions">the restrictions for requested presentation for this predicate</param>
        public Predicate(string name, int value, params Restriction[] restrictions)
        {
            this.data = new JsonObject();
            data.Add("name", name);
            data.Add("p_type", ">=");
            data.Add("p_value", value);
            data.Add("restrictions", JsonUtil.makeArray(restrictions));
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
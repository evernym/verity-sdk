using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// A holder for an attribute based restrictions
    /// </summary>
    public class Attribute : AsJsonObject
    {
        JsonObject data;

        /// <summary>
        /// Constructs the Attribute object with the given attribute name and given restrictions 
        /// </summary>
        /// <param name="name">the attribute name</param>
        /// <param name="restrictions">the restrictions for requested presentation for the given attribute</param>
        public Attribute(string name, params Restriction[] restrictions)
        {
            this.data = new JsonObject();
            data.Add("name", name);
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
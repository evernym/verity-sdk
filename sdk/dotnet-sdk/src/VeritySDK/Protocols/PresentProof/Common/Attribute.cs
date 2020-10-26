using System.Json;

namespace VeritySDK
{
    /**
     * A holder for an attribute based restrictions
     */
    public class Attribute : AsJsonObject
    {
        JsonObject data;

        /**
         * Constructs the Attribute object with the given attribute name and given restrictions
         * @param name the attribute name
         * @param restrictions the restrictions for requested presentation for the given attribute
         */
        public Attribute(string name, params Restriction[] restrictions)
        {
            this.data = new JsonObject();
            data.Add("name", name);
            data.Add("restrictions", JsonUtil.makeArray(restrictions));
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
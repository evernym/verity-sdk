using System.Json;

namespace VeritySDK
{
    /**
     * A holder for an predicate based restrictions
     */
    public class Predicate : AsJsonObject
    {
        JsonObject data;

        /**
         * Constructs the Predicate object with the given attribute name, value and given restrictions

         * @param name the attribute name
         * @param value the value the given attribute must be greater than
         * @param restrictions the restrictions for requested presentation for this predicate
         */
        public Predicate(string name, int value, params Restriction[] restrictions)
        {
            this.data = new JsonObject();
            data.Add("name", name);
            data.Add("p_type", ">=");
            data.Add("p_value", value);
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
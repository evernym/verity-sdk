using System.Json;

namespace VeritySDK
{
    /**
     * A holder for a restriction expression
     */
    public class Restriction : AsJsonObject
    {
        JsonObject data;

        public Restriction(JsonObject data)
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
            return this.data;
        }
    }
}
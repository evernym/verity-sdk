using System.Json;

namespace VeritySDK
{
    public interface AsJsonObject
    {
        /// <summary>
        /// Convert this object to a JSON object
        /// </summary>
        /// <returns>this object as a JSON object</returns>
        JsonObject toJson();
    }
}

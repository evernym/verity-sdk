using System.Json;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.PresentProof
{
    /// <summary>
    /// A holder for the ProposedAttribute
    /// </summary>
    public class ProposedAttribute : AsJsonObject
    {
        private JsonObject _data = new JsonObject();

        public JsonObject data { get { return _data; } }

        /// <summary>
        /// Constructs the ProposedAttribute object
        /// </summary>
        /// <param name="name">the attribute name</param>
        /// <param name="credDefId">credential from which attribute is obtained.</param>
        /// <param name="value">value of the attribute</param>
        public ProposedAttribute(string name, string credDefId, string value)
        {
            data.Add("name", name);
            data.Add("cred_def_id", credDefId);
            data.Add("value", value);
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
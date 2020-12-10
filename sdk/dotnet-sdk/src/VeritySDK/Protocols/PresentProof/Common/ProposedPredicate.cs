using System.Json;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.PresentProof
{
    /// <summary>
    /// A holder for the ProposedPredicate object
    /// </summary>
    public class ProposedPredicate : AsJsonObject
    {
        JsonObject data;

        /// <summary>
        /// Constructs the ProposedPredicate object 
        /// </summary>
        /// <param name="name">the predicate name</param>
        /// <param name="credDefId">the credential definition used for this predicate</param>
        /// <param name="threshold">threshold value of the predicate</param>
        public ProposedPredicate(string name, string credDefId, int threshold)
        {
            this.data = new JsonObject();
            data.Add("name", name);
            data.Add("cred_def_id", credDefId);
            data.Add("predicate", ">=");
            data.Add("threshold", threshold);
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
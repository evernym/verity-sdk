using System.Json;

namespace VeritySDK.Protocols.OutOfBand
{
    /// <summary>
    /// The OutOfBand protocol allow an interaction intent, connection information and reuse of a relationship.
    /// </summary>
    public class OutOfBand
    {
        private OutOfBand() { }

        /// <summary>
        /// used by invitee to send Reuse message 
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be reused</param>
        /// <param name="inviteUrl">the Out-of-Band invitation url</param>
        /// <returns></returns>
        public static OutOfBandV1_0 v1_0(string forRelationship, string inviteUrl)
        {
            return new OutOfBandV1_0(forRelationship, inviteUrl);
        }
    }
}
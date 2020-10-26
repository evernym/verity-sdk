using System.Json;

namespace VeritySDK
{
    /**
     * The OutOfBand protocol allow an interaction intent, connection information and reuse of a relationship.
     */
    public class OutOfBand
    {
        private OutOfBand() { }

        /**
         * used by invitee to send Reuse message
         *
         * @param forRelationship the relationship identifier (DID) for the pairwise
         *                        relationship that will be reused
         * @param inviteUrl the Out-of-Band invitation url
         */
        public static OutOfBandV1_0 v1_0(string forRelationship, string inviteUrl)
        {
            return new OutOfBandImplV1_0(forRelationship, inviteUrl);
        }
    }
}
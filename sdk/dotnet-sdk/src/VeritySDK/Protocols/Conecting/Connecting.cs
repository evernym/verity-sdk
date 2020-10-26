using System;

namespace VeritySDK
{
    /**
     * Factory for Connecting protocol objects.
     * <p/>
     * The Connecting protocols form secure, private and self-sovereign channel between two independent parties.
     * This protocol facilitates the exchange of keys and endpoints that will be used in all future interactions.
     * This connecting process starts with the Relationship protocol which provisions a relationship on the Verity
     * Application and creates the an invite. With the invite, this Connecting protocol can be started and completed.
     *
     * @see Relationship
     * @see <a href="https://github.com/hyperledger/aries-rfcs/tree/9b0aaa39df7e8bd434126c4b33c097aae78d65bf/features/0160-connection-protocol" target="_blank" rel="noopener noreferrer">Aries 0160: Connection Protocol</a>
     */
    public class Connecting
    {
        private Connecting() { }

        /**
         * Constructor for the 1.0 Connections object. This constructor creates an object that is ready to accept
         * an invitation and start the Connections protocol.
         *
         * @param forRelationship The relationship identifier (DID) to use for the connections exchange. Normally, its
         *                        relationship will have been created as a reaction to receiving the invitation.
         * @param label A human readable string that will label the caller identity (often an organization).
         *              E.g. 'Acme Corp`
         * @param base64InviteURL the invitation URL as specified by the Aries 0160: Connection Protocol (eg. https://<domain>/<path>?c_i=<invitation-string>)
         *
         * @return 1.0 Connections object
         */
        public static ConnectionsV1_0 v1_0(String forRelationship, String label, String base64InviteURL)
        {
            return new ConnectionsImplV1_0(forRelationship, label, base64InviteURL);
        }

        /**
         * Constructor for the 1.0 Connections object. This constructor re-creates an object from a known relationship and
         * threadId. This object can only check status of the protocol.
         *
         * @param forRelationship The relationship identifier (DID) to use for the connections protocol.
         * @param threadId the thread id of the already started protocol
         * @return 1.0 Connections object
         */
        public static ConnectionsV1_0 v1_0(String forRelationship, String threadId)
        {
            return new ConnectionsImplV1_0(forRelationship, threadId);
        }

    }
}

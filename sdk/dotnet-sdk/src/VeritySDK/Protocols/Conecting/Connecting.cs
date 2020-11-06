using System;

namespace VeritySDK
{
    
/// <summary>
    /// Factory for Connecting protocol objects.
    /// <p/>
    /// The Connecting protocols form secure, private and self-sovereign channel between two independent parties.
    /// This protocol facilitates the exchange of keys and endpoints that will be used in all future interactions.
    /// This connecting process starts with the Relationship protocol which provisions a relationship on the Verity
    /// Application and creates the an invite. With the invite, this Connecting protocol can be started and completed.
    /// </summary>
    /// <see cref="Relationship"/>
    /// <see cref="https://github.com/hyperledger/aries-rfcs/tree/9b0aaa39df7e8bd434126c4b33c097aae78d65bf/features/0160-connection-protocol"/>
    public class Connecting
    {
        /// <summary>
        /// Simple constructor
        /// </summary>
        private Connecting() { }

        
/// <summary>
        /// Constructor for the 1.0 Connections object. This constructor creates an object that is ready to accept an invitation and start the Connections protocol.
        /// </summary>
        /// <param name="forRelationship">The relationship identifier (DID) to use for the connections exchange. Normally, its relationship will have been created as a reaction to receiving the invitation.</param>
        /// <param name="label">A human readable string that will label the caller identity (often an organization). E.g. 'Acme Corp`</param>
        /// <param name="base64InviteURL">the invitation URL as specified by the Aries 0160: Connection Protocol (eg. https://<domain>/<path>?c_i=<invitation-string>)</param>
        /// <returns>1.0 Connections object</returns>
        public static ConnectionsV1_0 v1_0(String forRelationship, String label, String base64InviteURL)
        {
            return new ConnectionsImplV1_0(forRelationship, label, base64InviteURL);
        }

        /// <summary>
        /// Constructor for the 1.0 Connections object. This constructor re-creates an object from a known relationship and threadId. This object can only check status of the protocol.
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) to use for the connections protocol.</param>
        /// <param name="threadId">the thread id of the already started protocol</param>
        /// <returns>1.0 Connections object</returns>
        public static ConnectionsV1_0 v1_0(String forRelationship, String threadId)
        {
            return new ConnectionsImplV1_0(forRelationship, threadId);
        }
    }
}

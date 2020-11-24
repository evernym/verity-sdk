using System;
using System.Json;
using System.Security.Policy;

namespace VeritySDK.Protocols.Relationship
{
    /// <summary>
    /// Factory for the Relationship protocol objects
    /// 
    /// The Relationship protocol creates and manages relationships on the verity-application agent. These relationships
    /// are secure communication channels between self-sovereign parties. A relationship much be created before using the
    /// connections protocols. In the future this protocol will allow management of each given relationship
    /// (eg key rotation)
    /// </summary>
    public class Relationship
    {
        /// <summary>
        /// Constructor for the 1.0 Relationship object. This constructor creates an object that is ready to create a new relationship. 
        /// </summary>
        /// <returns>1.0 Relationship object</returns>
        public static RelationshipV1_0 v1_0()
        {
            return new RelationshipV1_0();
        }

        /// <summary>
        /// Constructor for the 1.0 Relationship object. This constructor creates an object that is ready to create a new relationship. 
        /// </summary>
        /// <param name="label">the label presented in the invitation to connect to this relationship</param>
        /// <returns>1.0 Relationship object</returns>
        public static RelationshipV1_0 v1_0(string label)
        {
            return new RelationshipV1_0(label);
        }

        /// <summary>
        /// Constructor for the 1.0 Relationship object. This constructor creates an object that is ready to create a new relationship. 
        /// </summary>
        /// <param name="label">the label presented in the invitation to connect to this relationship</param>
        /// <param name="logoUrl">logo url presented in invitation</param>
        /// <returns>1.0 Relationship object</returns>
        public static RelationshipV1_0 v1_0(string label, Url logoUrl)
        {
            return new RelationshipV1_0(label, logoUrl);
        }

        /// <summary>
        /// Constructor for the 1.0 Relationship object. This constructor creates an object that is ready to create a new relationship. 
        /// </summary>
        /// <param name="label">the label presented in the invitation to connect to this relationship</param>
        /// <param name="logoUrl">logo url presented in invitation</param>
        /// <param name="phoneNumber">mobile phone number in international format, eg. +18011234567</param>
        /// <returns>1.0 Relationship object</returns>
        public static RelationshipV1_0 v1_0(string label, Url logoUrl, string phoneNumber)
        {
            return new RelationshipV1_0(label, logoUrl, phoneNumber);
        }

        /// <summary>
        /// Constructor for the 1.0 Relationship object. This constructor re-creates an object from a known relationship and
        /// threadId. This object can only check status of the protocol.
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="threadId">the thread id of the already started protocol</param>
        /// <returns>1.0 Relationship object</returns>
        public static RelationshipV1_0 v1_0(string forRelationship, string threadId)
        {
            return new RelationshipV1_0(forRelationship, threadId);
        }
    }
}
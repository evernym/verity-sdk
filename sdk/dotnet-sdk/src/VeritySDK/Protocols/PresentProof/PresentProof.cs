using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// Factory for PresentProof protocol objects.
    ///
    /// The PresentProof protocol allows one self-sovereign party ask another self-sovereign party for a private
    /// and verifiable presentation from credentials they hold.This request can be restricted to certain selectable
    /// restrictions.
    /// </summary>
    /// <see cref="https://github.com/hyperledger/aries-rfcs/tree/4fae574c03f9f1013db30bf2c0c676b1122f7149/features/0037-present-proof"/>
    public class PresentProof
    {
        /// <summary>
        /// Constructor for the 1.0 PresentProof object. This constructor creates an object that is ready to start process of requesting a presentation of proof
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="name">A human readable name for the given request</param>
        /// <param name="proofAttrs">An array of attribute based restrictions</param>
        /// <returns>1.0 PresentProof object</returns>
        public static PresentProofV1_0 v1_0(string forRelationship,
                                            string name,
                                            params Attribute[] proofAttrs)
        {
            return new PresentProofImplV1_0(forRelationship, name, proofAttrs);
        }

        /// <summary>
        /// Constructor for the 1.0 PresentProof object. This constructor creates an object that is ready to start process of requesting a presentation of proof 
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="name">A human readable name for the given request</param>
        /// <param name="proofPredicate">An array of predicate based restrictions</param>
        /// <returns>1.0 PresentProof object</returns>
        public static PresentProofV1_0 v1_0(string forRelationship,
                                            string name,
                                            params Predicate[] proofPredicate)
        {
            return new PresentProofImplV1_0(forRelationship, name, proofPredicate);
        }

        /// <summary>
        /// Constructor for the 1.0 PresentProof object. This constructor creates an object that is ready to start process of requesting a presentation of proof 
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="name">A human readable name for the given request</param>
        /// <param name="proofAttrs">An array of attribute based restrictions</param>
        /// <param name="proofPredicate">An array of predicate based restrictions</param>
        /// <returns>1.0 PresentProof object</returns>
        public static PresentProofV1_0 v1_0(string forRelationship,
                                            string name,
                                            Attribute[] proofAttrs,
                                            Predicate[] proofPredicate)
        {
            return new PresentProofImplV1_0(forRelationship, name, proofAttrs, proofPredicate);
        }

        /// <summary>
        /// Constructor for the 1.0 PresentProof object. This constructor re-creates an object from a known relationship and threadId. This object can only check status of the protocol.
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="threadId">the thread id of the already started protocol</param>
        /// <returns>1.0 PresentProof object</returns>
        public static PresentProofV1_0 v1_0(string forRelationship,
                                            string threadId)
        {
            return new PresentProofImplV1_0(forRelationship, threadId);
        }
    }
}
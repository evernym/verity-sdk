using System.Json;

namespace VeritySDK.Protocols.PresentProof
{
    /// <summary>
    /// Factory for PresentProof protocol objects.
    ///
    /// The PresentProof protocol allows one self-sovereign party ask another self-sovereign party for a private
    /// and verifiable presentation from credentials they hold.This request can be restricted to certain selectable
    /// restrictions.
    /// </summary>
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
            return new PresentProofV1_0(forRelationship, name, proofAttrs);
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
            return new PresentProofV1_0(forRelationship, name, proofPredicate);
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
            return new PresentProofV1_0(forRelationship, name, proofAttrs, proofPredicate);
        }


        /// <summary>
        /// Constructor for the 1.0 PresentProof object. This constructor creates an object that is ready to start process of requesting a presentation of proof 
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="name">A human readable name for the given request</param>
        /// <param name="proofAttrs">An array of attribute based restrictions</param>
        /// <param name="proofPredicate">An array of predicate based restrictions</param>
        /// <param name="byInvitation">Flag to create out-of-band invitation as a part of the PresentProof protocol</param>
        /// <returns>1.0 PresentProof object</returns>
        public static PresentProofV1_0 v1_0(string forRelationship,
                                            string name,
                                            Attribute[] proofAttrs,
                                            Predicate[] proofPredicate,
                                            bool byInvitation)
        {
            return new PresentProofV1_0(forRelationship, name, proofAttrs, proofPredicate, byInvitation);
        }

        /// <summary>
        /// Constructor for the 1.0 PresentProof object.
        /// This constructor creates an object that is ready to start process of proposing a presentation of proof
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="attributes">An array of attribute based values</param>
        /// <param name="predicates">An array of predicate based values</param>
        /// <returns>1.0 PresentProof object</returns>
        public static PresentProofV1_0 v1_0(string forRelationship,
                                            ProposedAttribute[] attributes,
                                            ProposedPredicate[] predicates)
        {
            return new PresentProofV1_0(forRelationship, null, attributes, predicates);
        }

        /// <summary>
        /// Constructor for the 1.0 PresentProof object.
        /// This constructor creates an object that is ready to start process of proposing a presentation of proof as an answer to a presentation request.
        /// This constructor re-creates an object from a known relationship and threadId.
        /// It can be used only if protocol is already started.
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="threadId">the thread id of the already started protocol</param>
        /// <param name="attributes">An array of attribute based values</param>
        /// <param name="predicates">An array of predicate based values</param>
        /// <returns>1.0 PresentProof object</returns>
        public static PresentProofV1_0 v1_0(string forRelationship,
                                            string threadId,
                                            ProposedAttribute[] attributes,
                                            ProposedPredicate[] predicates)
        {
            return new PresentProofV1_0(forRelationship, threadId, attributes, predicates);
        }

        /// <summary>
        /// Constructor for the 1.0 PresentProof object. This constructor re-creates an object from a known relationship and threadId.
        /// This object can be used only if protocol is already started.
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="threadId">the thread id of the already started protocol</param>
        /// <returns>1.0 PresentProof object</returns>
        public static PresentProofV1_0 v1_0(string forRelationship,
                                            string threadId)
        {
            return new PresentProofV1_0(forRelationship, threadId);
        }
    }
}
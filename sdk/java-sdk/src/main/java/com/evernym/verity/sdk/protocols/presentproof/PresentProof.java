package com.evernym.verity.sdk.protocols.presentproof;

import com.evernym.verity.sdk.protocols.presentproof.common.Attribute;
import com.evernym.verity.sdk.protocols.presentproof.common.Predicate;
import com.evernym.verity.sdk.protocols.presentproof.v1_0.PresentProofV1_0;

/**
 * Factory for PresentProof protocol objects.
 * <p/>
 *
 * The PresentProof protocol allows one self-sovereign party ask another self-sovereign party for a private
 * and verifiable presentation from credentials they hold. This request can be restricted to certain selectable
 * restrictions.
 *
 * @see <a href="https://github.com/hyperledger/aries-rfcs/tree/4fae574c03f9f1013db30bf2c0c676b1122f7149/features/0037-present-proof" target="_blank" rel="noopener noreferrer">Aries 0037: Present Proof Protocol 1.0</a>
 */
public class PresentProof {
    /**
     * Constructor for the 1.0 PresentProof object. This constructor creates an object that is ready to start
     * process of requesting a presentation of proof
     *
     * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
     * @param name A human readable name for the given request
     * @param proofAttrs An array of attribute based restrictions
     * @return 1.0 PresentProof object
     */
    public static PresentProofV1_0 v1_0(String forRelationship,
                                        String name,
                                        Attribute...proofAttrs) {
        return new PresentProofImplV1_0(forRelationship, name, proofAttrs);
    }

    /**
     * Constructor for the 1.0 PresentProof object. This constructor creates an object that is ready to start
     * process of requesting a presentation of proof
     *
     * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
     * @param name A human readable name for the given request
     * @param proofPredicate An array of predicate based restrictions
     * @return 1.0 PresentProof object
     */
    public static PresentProofV1_0 v1_0(String forRelationship,
                                        String name,
                                        Predicate...proofPredicate) {
        return new PresentProofImplV1_0(forRelationship, name, proofPredicate);
    }

    /**
     * Constructor for the 1.0 PresentProof object. This constructor creates an object that is ready to start
     * process of requesting a presentation of proof
     *
     * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
     * @param name A human readable name for the given request
     * @param proofAttrs An array of attribute based restrictions
     * @param proofPredicate An array of predicate based restrictions
     * @return 1.0 PresentProof object
     */
    public static PresentProofV1_0 v1_0(String forRelationship,
                                        String name,
                                        Attribute[] proofAttrs,
                                        Predicate[] proofPredicate) {
        return new PresentProofImplV1_0(forRelationship, name, proofAttrs, proofPredicate);
    }

    /**
     * Constructor for the 1.0 PresentProof object. This constructor creates an object that is ready to start
     * process of requesting a presentation of proof
     *
     * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
     * @param name A human readable name for the given request
     * @param proofAttrs An array of attribute based restrictions
     * @param proofPredicate An array of predicate based restrictions
     * @param byInvitation flag to create out-of-band invitation as a part of the PresentProof protocol
     * @return 1.0 PresentProof object
     */
    public static PresentProofV1_0 v1_0(String forRelationship,
                                        String name,
                                        Attribute[] proofAttrs,
                                        Predicate[] proofPredicate,
                                        Boolean byInvitation) {
        return new PresentProofImplV1_0(forRelationship, name, proofAttrs, proofPredicate, byInvitation);
    }

    /**
     * Constructor for the 1.0 PresentProof object. This constructor re-creates an object from a known relationship and
     * threadId. This object can only check status of the protocol.
     *
     * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
     * @param threadId the thread id of the already started protocol
     * @return 1.0 PresentProof object
     */
    public static PresentProofV1_0 v1_0(String forRelationship,
                                        String threadId) {
        return new PresentProofImplV1_0(forRelationship, threadId);
    }

}

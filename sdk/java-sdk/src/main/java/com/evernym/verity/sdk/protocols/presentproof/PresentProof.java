package com.evernym.verity.sdk.protocols.presentproof;

import com.evernym.verity.sdk.protocols.presentproof.common.Attribute;
import com.evernym.verity.sdk.protocols.presentproof.common.Predicate;
import com.evernym.verity.sdk.protocols.presentproof.v1_0.PresentProofV1_0;

public class PresentProof {
    /**
     * Initializes the proof request object
     * @param forRelationship DID of relationship where proof request will be sent to
     * @param name The name of the proof request
     * @param proofAttrs The requested attributes of the proof request
     */
    public static PresentProofV1_0 v1_0(String forRelationship,
                                        String name,
                                        Attribute...proofAttrs) {
        return new PresentProofImplV1_0(forRelationship, name, proofAttrs);
    }

    public static PresentProofV1_0 v1_0(String forRelationship,
                                        String name,
                                        Predicate...proofPredicate) {
        return new PresentProofImplV1_0(forRelationship, name, proofPredicate);
    }

    public static PresentProofV1_0 v1_0(String forRelationship,
                                        String name,
                                        Attribute[] proofAttrs,
                                        Predicate[] proofPredicate) {
        return new PresentProofImplV1_0(forRelationship, name, proofAttrs, proofPredicate);
    }


    public static PresentProofV1_0 v1_0(String forRelationship,
                                        String threadId) {
        return new PresentProofImplV1_0(forRelationship, threadId);
    }

}

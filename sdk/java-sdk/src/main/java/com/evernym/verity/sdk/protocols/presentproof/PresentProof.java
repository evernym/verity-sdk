package com.evernym.verity.sdk.protocols.presentproof;

import com.evernym.verity.sdk.protocols.presentproof.v0_6.Attribute;
import com.evernym.verity.sdk.protocols.presentproof.v0_6.PresentProofV0_6;

public class PresentProof {
    /**
     * Initializes the proof request object
     * @param forRelationship DID of relationship where proof request will be sent to
     * @param name The name of the proof request
     * @param proofAttrs The requested attributes of the proof request
     */
    public static PresentProofV0_6 v0_6(String forRelationship,
                                     String name,
                                     Attribute...proofAttrs) {
        return new PresentProofImplV0_6(forRelationship, name, proofAttrs);
    }

    public static PresentProofV0_6 v0_6(String forRelationship,
                                        String threadId) {
        return new PresentProofImplV0_6(forRelationship, threadId);
    }

}

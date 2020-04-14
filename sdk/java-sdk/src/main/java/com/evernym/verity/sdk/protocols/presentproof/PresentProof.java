package com.evernym.verity.sdk.protocols.presentproof;

import com.evernym.verity.sdk.protocols.presentproof.v0_6.Attribute;
import com.evernym.verity.sdk.protocols.presentproof.v0_6.PresentProofImplV0_6;

public class PresentProof {
    public static PresentProofImplV0_6 v0_6(String forRelationship,
                                     String name,
                                     Attribute...attributes) {
        return new PresentProofImplV0_6(forRelationship, name, attributes);
    }

    public static PresentProofImplV0_6 v0_6(String forRelationship,
                                     String threadId) {
        return new PresentProofImplV0_6(forRelationship, threadId);
    }

}

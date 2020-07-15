package com.evernym.verity.sdk.protocols.outofband;

import com.evernym.verity.sdk.protocols.outofband.v1_0.OutOfBandV1_0;

/**
 * Builds and sends a new encrypted agent message for the Connections protocol.
 */
public class OutOfBand {
    private OutOfBand() {
    }

    /**
     * used by invitee to send Reuse message
     *
     * @param forRelationship the relationship identifier (DID) for the pairwise
     *                        relationship that will be reused
     * @param inviteUrl the Out-of-Band invitation url
     */
    public static OutOfBandV1_0 v1_0(String forRelationship, String inviteUrl) {
        return new OutOfBandImplV1_0(forRelationship, inviteUrl);
    }
}
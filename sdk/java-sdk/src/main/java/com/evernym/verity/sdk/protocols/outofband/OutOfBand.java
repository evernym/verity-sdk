package com.evernym.verity.sdk.protocols.outofband;

import com.evernym.verity.sdk.protocols.outofband.v1_0.OutOfBandV1_0;

/**
 * Builds and sends a new encrypted agent message for the Connections protocol.
 */
public class OutOfBand {
    private OutOfBand() {
    }

    /**
     * used by inviter to get a connection invitation message
     *
     * @param threadId        thread identifier
     */
    public static OutOfBandV1_0 v1_0(String threadId, String inviteUrl) {
        return new OutOfBandImplV1_0(threadId, inviteUrl);
    }
}
package com.evernym.verity.sdk.protocols.outofband;

import com.evernym.verity.sdk.protocols.outofband.v1_0.OutOfBandV1_0;
import com.evernym.verity.sdk.protocols.outofband.v1_0.RequestAttach;

import java.util.List;

/**
 * Builds and sends a new encrypted agent message for the Connections protocol.
 */
public class OutOfBand {
    private OutOfBand() {
    }

    /**
     * used by inviter/invitee to create relationship
     *
     * @param label label to be used in invitation
     * @return
     */
    public static OutOfBandV1_0 v1_0(String label, String goalCode, String goal,
                                     List<String> handshake_protocols, List<RequestAttach> request) {

        return new OutOfBandImplV1_0(label, goalCode, goal, handshake_protocols, request);
    }

    /**
     * used by inviter to get a connection invitation message
     *
     * @param threadId        thread identifier
     */
    public static OutOfBandV1_0 v1_0(String threadId) {
        return new OutOfBandImplV1_0(threadId);
    }
}
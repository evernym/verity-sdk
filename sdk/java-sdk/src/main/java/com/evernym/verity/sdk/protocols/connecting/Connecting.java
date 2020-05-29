package com.evernym.verity.sdk.protocols.connecting;

import com.evernym.verity.sdk.protocols.connecting.v1_0.ConnectionsV1_0;

/**
 * Builds and sends a new encrypted agent message for the Connections protocol.
 */
public class Connecting {
    private Connecting(){}

    /**
     * this is used by invitee to respond to an invitation
     * @param base64InviteURL received invitation's url
     */
    public static ConnectionsV1_0 v1_0(String forRelationship, String label, String base64InviteURL) {
        return new ConnectionsImplV1_0(forRelationship, label, base64InviteURL);
    }

    public static ConnectionsV1_0 v1_0(String forRelationship, String threadId) {
        return new ConnectionsImplV1_0(forRelationship, threadId);
    }

}

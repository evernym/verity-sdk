package com.evernym.verity.sdk.protocols.connecting;

import com.evernym.verity.sdk.protocols.connecting.v0_6.ConnectingV0_6;
import com.evernym.verity.sdk.protocols.connecting.v1_0.ConnectionsV1_0;

/**
 * Builds and sends a new encrypted agent message for the Connections protocol.
 */
public class Connecting {
    private Connecting(){}

    /**
     * Create connection without phone number
     * @param sourceId required optional param that sets an id of the connection
     */
    public static ConnectingV0_6 v0_6(String sourceId) {
        return new ConnectingImplV0_6(sourceId);
    }

    /**
     * Create connection without a phone number that uses a public DID.
     * @param sourceId required param that sets an id of the connection
     * @param includePublicDID optional param that indicates the connection invite should use the institution's public DID.
     */
    public static ConnectingV0_6 v0_6(String sourceId, boolean includePublicDID) {
        return new ConnectingImplV0_6(sourceId, includePublicDID);
    }

    /**
     * Create connection with phone number
     * @param sourceId required param that sets an id of the connection
     * @param phoneNo optional param that sets the sms phone number for an identity holder
     */
    public static ConnectingV0_6 v0_6(String sourceId, String phoneNo) {
        return new ConnectingImplV0_6(sourceId, phoneNo);
    }

    /**
     * Create connection with phone number that uses a public DID
     * @param sourceId required param that sets an id of the connection
     * @param phoneNo optional param that sets the sms phone number for an identity holder
     * @param includePublicDID optional param that indicates the connection invite should use the institution's public DID.
     */
    public static ConnectingV0_6 v0_6(String sourceId, String phoneNo, boolean includePublicDID) {
        return new ConnectingImplV0_6(sourceId, phoneNo, includePublicDID);
    }

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

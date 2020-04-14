package com.evernym.verity.sdk.protocols.connecting;

import com.evernym.verity.sdk.protocols.connecting.v0_6.ConnectingImplV0_6;
import com.evernym.verity.sdk.protocols.connecting.v1_0.ConnectionsImplV1_0;

public class Connecting {
    private Connecting(){}

    public static ConnectingImplV0_6 v0_6(String sourceId) {
        return new ConnectingImplV0_6(sourceId);
    }

    public static ConnectingImplV0_6 v0_6(String sourceId, boolean includePublicDID) {
        return new ConnectingImplV0_6(sourceId, includePublicDID);
    }

    public static ConnectingImplV0_6 v0_6(String sourceId, String phoneNo) {
        return new ConnectingImplV0_6(sourceId, phoneNo);
    }

    public static ConnectingImplV0_6 v0_6(String sourceId, String phoneNo, boolean includePublicDID) {
        return new ConnectingImplV0_6(sourceId, phoneNo, includePublicDID);
    }

    public static ConnectionsImplV1_0 v1_0(String parentThreadId, String label, String base64InviteURL) {
        return new ConnectionsImplV1_0(parentThreadId, label, base64InviteURL);
    }

}

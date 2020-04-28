package com.evernym.verity.sdk.protocols.updateendpoint;

import com.evernym.verity.sdk.protocols.updateendpoint.v0_6.UpdateEndpointV0_6;

public class UpdateEndpoint {
    private UpdateEndpoint() {}

    public static UpdateEndpointV0_6 v0_6() {
        return new UpdateEndpointImplV0_6();
    }
}

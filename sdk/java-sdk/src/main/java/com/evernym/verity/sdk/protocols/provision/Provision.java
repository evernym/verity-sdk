package com.evernym.verity.sdk.protocols.provision;

import com.evernym.verity.sdk.protocols.provision.v0_6.ProvisionImplV0_6;
import com.evernym.verity.sdk.protocols.provision.v0_6.ProvisionV0_6;

public class Provision {
    private Provision() {}

    public static ProvisionV0_6 v0_6() {
        return new ProvisionImplV0_6();
    }
}

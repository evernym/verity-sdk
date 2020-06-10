package com.evernym.verity.sdk.protocols.provision;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.provision.v0_7.ProvisionV0_7;

public class Provision {
    private Provision() {}

    public static ProvisionV0_7 v0_7() {
        return new ProvisionImplV0_7();
    }

    public static ProvisionV0_7 v0_7(String token) throws VerityException {
        return new ProvisionImplV0_7(token);
    }
}

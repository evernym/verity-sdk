package com.evernym.verity.sdk.protocols.issuersetup;

import com.evernym.verity.sdk.protocols.issuersetup.v0_6.IssuerSetupImplV0_6;
import com.evernym.verity.sdk.protocols.issuersetup.v0_6.IssuerSetupV0_6;

public class IssuerSetup {
    private IssuerSetup() {}

    public static IssuerSetupV0_6 v0_6() {
        return new IssuerSetupImplV0_6();
    }
}

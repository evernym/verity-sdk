package com.evernym.verity.sdk.protocols.issuersetup;

import com.evernym.verity.sdk.protocols.issuersetup.v0_6.IssuerSetupV0_6;
import com.evernym.verity.sdk.protocols.issuersetup.v0_7.IssuerSetupV0_7;

/**
 * Factory for the IssuerSetup protocol objects
 * <p/>
 *
 * The IssuerSetup protocol starts the setup process for issuers of verifiable credentials. The main task is creating
 * a public identity that can be used to issue credentials. This identity and it's keys are created and held on the
 * verity-application. The public elements (DID and verkey) are communicated back via a signal message. The setup
 * process encapsulated by this protocol must be completed before other issuing activities (write-schema, write-cred-def
 * and issue-credential) can be done.
 */
public class IssuerSetup {
    private IssuerSetup() {}

    /**
     * Constructor for the 0.6 IssuerSetup object. This constructor creates an object that is ready to start the setup
     * process of an issuer.
     * @return 0.6 IssuerSetup object
     */
    public static IssuerSetupV0_6 v0_6() {
        return new IssuerSetupImplV0_6();
    }

    /**
     * Constructor for the 0.7 IssuerSetup object. This constructor creates an object that is ready to start the setup
     * process of an issuer.
     * @return 0.7 IssuerSetup object
     */
    public static IssuerSetupV0_7 v0_7() {
        return new IssuerSetupImplV0_7();
    }
}

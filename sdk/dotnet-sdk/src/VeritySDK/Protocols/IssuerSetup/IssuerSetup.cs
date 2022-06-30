using System.Json;

namespace VeritySDK.Protocols.IssuerSetup
{
    /// <summary>
    /// Factory for the IssuerSetup protocol objects
    /// 
    /// The IssuerSetup protocol starts the setup process for issuers of verifiable credentials.The main task is creating
    /// a public identity that can be used to issue credentials.This identity and it's keys are created and held on the
    /// verity-application.The public elements(DID and verkey) are communicated back via a signal message.The setup
    /// process encapsulated by this protocol must be completed before other issuing activities (write-schema, write-cred-def
    /// and issue-credential) can be done.
    /// </summary>
    public class IssuerSetup
    {
        /// <summary>
        /// Constructor for the 0.6 IssuerSetup object. This constructor creates an object that is ready to start the setup process of an issuer.
        /// </summary>
        /// <returns>0.6 IssuerSetup object</returns>
        public static IssuerSetupV0_6 v0_6()
        {
            return new IssuerSetupV0_6();
        }

        /// <summary>
        /// Constructor for the 0.6 IssuerSetup object. This constructor creates an object that is ready to start the setup process of an issuer.
        /// </summary>
        /// <returns>0.6 IssuerSetup object</returns>
        public static IssuerSetupV0_7 v0_7()
        {
            return new IssuerSetupV0_7();
        }
    }
}
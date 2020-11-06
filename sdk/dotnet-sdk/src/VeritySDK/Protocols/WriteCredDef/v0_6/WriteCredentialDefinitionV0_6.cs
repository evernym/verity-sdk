using System;
using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// An abstract class for controlling a 0.6 WriteCredentialDefinition protocol.
    /// </summary>
    public abstract class WriteCredentialDefinitionV0_6 : AbstractProtocol
    {
        /// <summary>
        /// Constructor WriteCredentialDefinitionV0_6 object
        /// </summary>
        public WriteCredentialDefinitionV0_6() { }

        /// <summary>
        /// Constructor WriteCredentialDefinitionV0_6 object
        /// </summary>
        /// <param name="threadId">the thread id of the already started protocol</param>
        public WriteCredentialDefinitionV0_6(string threadId) : base(threadId) { }

        /// <summary>
        /// The qualifier for the message family. Uses Evernym's qualifier.
        /// </summary>
        string QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;

        /// <summary>
        /// The name for the message family.
        /// </summary>
        string FAMILY = "write-cred-def";

        /// <summary>
        /// The version for the message family.
        /// </summary>
        string VERSION = "0.6";

        /// <see cref=" MessageFamily.qualifier"/>
        public override string qualifier() { return QUALIFIER; }

        /// <see cref=" MessageFamily.family"/>
        public override string family() { return FAMILY; }

        /// <see cref=" MessageFamily.version"/>
        public override string version() { return VERSION; }

        /// <summary>
        /// Name for 'write' control message
        /// </summary>
        public string WRITE_CRED_DEF = "write";

        /// <summary>
        /// Creates a RevocationRegistryConfig object that effectively disables the revocation registry 
        /// </summary>
        /// <returns>a RevocationRegistryConfig object</returns>
        static RevocationRegistryConfig disabledRegistryConfig()
        {
            JsonObject json = new JsonObject();
            json.Add("support_revocation", false);
            return new RevocationRegistryConfig(json);
        }

        /// <summary>
        /// Directs verity-application to write the specified Credential Definition to the Ledger 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void write(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="write(Context)"/>
        public abstract JsonObject writeMsg(Context context);

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="write(Context)"/>
        public abstract byte[] writeMsgPacked(Context context);
    }
}
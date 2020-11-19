using System;
using System.Json;
using VeritySDK.Protocols;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.WriteCredDef
{
    /// <summary>
    /// A class for controlling a 0.6 WriteCredentialDefinition protocol.
    /// </summary>
    public class WriteCredentialDefinitionV0_6 : AbstractProtocol
    {
        #region Protocol identificator

        /// <summary>
        /// The qualifier for the message family. Uses Evernym's qualifier.
        /// </summary>
        public override string qualifier() { return Util.EVERNYM_MSG_QUALIFIER; }

        /// <summary>
        /// The name for the message family.
        /// </summary>
        public override string family() { return "write-cred-def"; }

        /// <summary>
        /// The version for the message family.
        /// </summary>
        public override string version() { return "0.6"; }

        #endregion 

        #region Constructors

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
        /// Constructor WriteCredentialDefinitionV0_6 object
        /// </summary>
        public WriteCredentialDefinitionV0_6(string name, string schemaId) : this(name, schemaId, null, null) { }

        /// <summary>
        /// Constructor WriteCredentialDefinitionV0_6 object
        /// </summary>
        public WriteCredentialDefinitionV0_6(string name, string schemaId, string tag) : this(name, schemaId, tag, null) { }

        /// <summary>
        /// Constructor WriteCredentialDefinitionV0_6 object
        /// </summary>
        public WriteCredentialDefinitionV0_6(string name, string schemaId, RevocationRegistryConfig revocation) : this(name, schemaId, null, revocation) { }

        /// <summary>
        /// Constructor WriteCredentialDefinitionV0_6 object
        /// </summary>
        public WriteCredentialDefinitionV0_6(string name, string schemaId, string tag, RevocationRegistryConfig revocation)
        {
            this.name = name;
            this.schemaId = schemaId;
            this.tag = tag;
            this.revocationConfig = revocation;
        }

        #endregion 

        /// <summary>
        /// Name for 'write' control message
        /// </summary>
        string WRITE_CRED_DEF = "write";
        string name;
        string schemaId;
        string tag;
        RevocationRegistryConfig revocationConfig;

        /// <summary>
        /// Creates a RevocationRegistryConfig object that effectively disables the revocation registry 
        /// </summary>
        /// <returns>a RevocationRegistryConfig object</returns>
        public static RevocationRegistryConfig disabledRegistryConfig()
        {
            JsonObject json = new JsonObject();
            json.Add("support_revocation", false);
            return new RevocationRegistryConfig(json);
        }

        /// <summary>
        /// Directs verity-application to write the specified Credential Definition to the Ledger 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void write(Context context)
        {
            send(context, writeMsg(context));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject writeMsg(Context context)
        {
            JsonObject message = new JsonObject();
            message.Add("@type", messageType(WRITE_CRED_DEF));
            message.Add("@id", WriteCredentialDefinitionV0_6.getNewId());
            message.Add("name", this.name);
            message.Add("schemaId", this.schemaId);
            addThread(message);
            if (this.tag != null) message.Add("tag", this.tag);
            if (this.revocationConfig != null) message.Add("revocationDetails", this.revocationConfig.toJson());
            return message;
        }

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] writeMsgPacked(Context context)
        {
            return packMsg(context, writeMsg(context));
        }
    }
}
using System;
using System.Json;

namespace VeritySDK
{
    /*
     * NON_VISIBLE
     *
     * This is an implementation of WriteCredentialDefinitionImplV0_6 but is not viable to user of Verity SDK. Created using the
     * static WriteCredentialDefinition class
     */
    public class WriteCredentialDefinitionImplV0_6 : WriteCredentialDefinitionV0_6
    {

        string name;
        protected string schemaId;
        string tag;
        RevocationRegistryConfig revocationConfig;

        public WriteCredentialDefinitionImplV0_6(string name, string schemaId) : this(name, schemaId, null, null) { }


        public WriteCredentialDefinitionImplV0_6(string name, string schemaId, string tag) : this(name, schemaId, tag, null) { }


        public WriteCredentialDefinitionImplV0_6(string name, string schemaId, RevocationRegistryConfig revocation) : this(name, schemaId, null, revocation) { }


        public WriteCredentialDefinitionImplV0_6(string name, string schemaId, string tag, RevocationRegistryConfig revocation)
        {
            this.name = name;
            this.schemaId = schemaId;
            this.tag = tag;
            this.revocationConfig = revocation;
        }


        public override void write(Context context)
        {
            send(context, writeMsg(context));
        }

        public override JsonObject writeMsg(Context context)
        {
            JsonObject message = new JsonObject();
            message.Add("@type", messageType(WRITE_CRED_DEF));
            message.Add("@id", WriteCredentialDefinitionImplV0_6.getNewId());
            message.Add("name", this.name);
            message.Add("schemaId", this.schemaId);
            addThread(message);
            if (this.tag != null) message.Add("tag", this.tag);
            if (this.revocationConfig != null) message.Add("revocationDetails", this.revocationConfig.toJson());
            return message;
        }

        public override byte[] writeMsgPacked(Context context)
        {
            return packMsg(context, writeMsg(context));
        }
    }
}
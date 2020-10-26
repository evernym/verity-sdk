using System;
using System.Json;
using System.Linq;

namespace VeritySDK
{
    /*
     * NON_VISIBLE
     *
     * This is an implementation of IssuerSetupImplV0_6 but is not viable to user of Verity SDK. Created using the
     * static IssuerSetup class
     */
    public class WriteSchemaImplV0_6 : WriteSchemaV0_6
    {

        string name;
        string version;
        string[] attrs;


        public WriteSchemaImplV0_6(string name, string version, params string[] attrs)
        {
            this.name = name;
            this.version = version;
            this.attrs = attrs;
        }

        public override void write(Context context)
        {
            send(context, writeMsg(context));
        }


        public override JsonObject writeMsg(Context context)
        {
            JsonObject message = new JsonObject();
            message.Add("@type", messageType(WRITE_SCHEMA));
            message.Add("@id", getNewId());
            addThread(message);
            message.Add("name", this.name);
            message.Add("version", this.version);

            var arr = new JsonArray();
            foreach (var a in attrs)
                arr.Add(a);
            message.Add("attrNames", arr);

            return message;
        }

        public override byte[] writeMsgPacked(Context context)
        {
            return packMsg(context, writeMsg(context));
        }

    }
}
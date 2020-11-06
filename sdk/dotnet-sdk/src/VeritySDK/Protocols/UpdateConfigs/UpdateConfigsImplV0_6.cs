using System;
using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// This is an implementation of UpdateConfigsImplV0_6 but is not viable to user of Verity SDK.
    /// </summary>
    public class UpdateConfigsImplV0_6 : UpdateConfigsV0_6
    {
        string UPDATE_CONFIGS = "update";
        string GET_STATUS = "get-status";

        string name;
        string logoUrl;

        public UpdateConfigsImplV0_6() { }

        public UpdateConfigsImplV0_6(string name, string logoUrl)
        {
            this.name = name;
            this.logoUrl = logoUrl;
        }

        public override void update(Context context)
        {
            send(context, updateMsg(context));
        }

        public override JsonObject updateMsg(Context context)
        {
            JsonObject message = new JsonObject();
            message.Add("@type", messageType(UPDATE_CONFIGS));
            message.Add("@id", getNewId());
            JsonArray configs = new JsonArray();
            JsonObject item1 = new JsonObject();
            item1.Add("name", "name");
            item1.Add("value", this.name);
            configs.Add(item1);
            JsonObject item2 = new JsonObject();
            item2.Add("name", "logoUrl");
            item2.Add("value", this.logoUrl);
            configs.Add(item2);
            message.Add("configs", configs);
            return message;
        }

        public override byte[] updateMsgPacked(Context context)
        {
            return packMsg(context, updateMsg(context));
        }

        public override void status(Context context)
        {
            send(context, statusMsg(context));
        }

        public override JsonObject statusMsg(Context context)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(GET_STATUS));
            msg.Add("@id", getNewId());
            addThread(msg);

            return msg;
        }

        public override byte[] statusMsgPacked(Context context)
        {
            return packMsg(context, statusMsg(context));
        }
    }
}
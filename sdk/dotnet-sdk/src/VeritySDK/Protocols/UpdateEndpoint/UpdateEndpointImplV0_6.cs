using System;
using System.Collections.Generic;
using System.Json;
using System.Text;

namespace VeritySDK
{
    /// <summary>
    /// This is an implementation of UpdateEndpointImplV0_6 but is not viable to user of Verity SDK. Created using the static UpdateEndpoint class
    /// </summary>
    public class UpdateEndpointImplV0_6 : UpdateEndpointV0_6
    {
        public UpdateEndpointImplV0_6() : base() { }

        int COM_METHOD_TYPE = 2;

        public override void update(Context context)
        {
            send(context, updateMsg(context));
        }

        public override JsonObject updateMsg(Context context)
        {
            JsonObject message = new JsonObject();

            message.Add("@type", messageType(UPDATE_ENDPOINT));
            message.Add("@id", getNewId());

            JsonObject comMethod = new JsonObject();
            comMethod.Add("id", "webhook");
            comMethod.Add("type", COM_METHOD_TYPE);
            comMethod.Add("value", context.EndpointUrl());

            JsonObject packaging = new JsonObject();
            packaging.Add("pkgType", "1.0");
            
            JsonArray recipientKeys = new JsonArray();
            recipientKeys.Add(context.SdkVerKey());
            packaging.Add("recipientKeys", recipientKeys);

            comMethod.Add("packaging", packaging);

            message.Add("comMethod", comMethod);
            return message;
        }

        public override byte[] updateMsgPacked(Context context)
        {
            return packMsg(context, updateMsg(context));
        }
    }
}

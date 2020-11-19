using System;
using System.Collections.Generic;
using System.Json;
using System.Text;
using VeritySDK.Protocols;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.UpdateEndpoint
{
    /// <summary>
    /// A class for controlling a 0.6 UpdateEndpoint protocol.
    /// </summary>
    public class UpdateEndpointV0_6 : AbstractProtocol
    {
        #region Protocol identificator

        /// <summary>
        /// The qualifier for the message family. Uses Evernym's qualifier.
        /// </summary>
        public override string qualifier() { return Util.EVERNYM_MSG_QUALIFIER; }

        /// <summary>
        /// The name for the message family.
        /// </summary>
        public override string family() { return "configs"; }

        /// <summary>
        /// The version for the message family.
        /// </summary>
        public override string version() { return "0.6"; }

        #endregion 

        /// <summary>
        /// Name for 'update-endpoint' control message
        /// </summary>
        string UPDATE_ENDPOINT = "UPDATE_COM_METHOD";
        int COM_METHOD_TYPE = 2;

        /// <summary>
        /// Directs verity-application to update the used endpoint for out-going signal message to the
        /// endpoint contained in the context object. See: {@link Context#endpointUrl()}
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void update(Context context)
        {
            send(context, updateMsg(context));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject updateMsg(Context context)
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

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] updateMsgPacked(Context context)
        {
            return packMsg(context, updateMsg(context));
        }
    }
}

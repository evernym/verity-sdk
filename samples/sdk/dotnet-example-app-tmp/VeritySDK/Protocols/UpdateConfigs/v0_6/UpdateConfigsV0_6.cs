using System;
using System.Json;
using VeritySDK.Protocols;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.UpdateConfigs
{
    /// <summary>
    /// A class for controlling a 0.6 UpdateConfigs protocol.
    /// </summary>
    public class UpdateConfigsV0_6 : AbstractProtocol
    {
        #region Protocol identificator

        /// <summary>
        /// The qualifier for the message family. Uses Evernym's qualifier.
        /// </summary>
        public override string qualifier() { return Util.EVERNYM_MSG_QUALIFIER; }

        /// <summary>
        /// The name for the message family.
        /// </summary>
        public override string family() { return "update-configs"; }

        /// <summary>
        /// The version for the message family.
        /// </summary>
        public override string version() { return "0.6"; }

        #endregion 

        #region Constructors

        /// <summary>
        /// Constructor UpdateConfigsV0_6 object
        /// </summary>
        public UpdateConfigsV0_6() { }

        /// <summary>
        /// Constructor UpdateConfigsV0_6 object
        /// </summary>
        /// <param name="threadId">the thread id of the already started protocol</param>
        public UpdateConfigsV0_6(string threadId) : base(threadId) { }

        /// <summary>
        /// Constructor UpdateConfigsV0_6 object
        /// </summary>
        public UpdateConfigsV0_6(string name, string logoUrl)
        {
            this.name = name;
            this.logoUrl = logoUrl;
        }

        #endregion 

        string UPDATE_CONFIGS = "update";
        string GET_STATUS = "get-status";

        string name;
        string logoUrl;

        /// <summary>
        /// Directs verity-application to update the configuration register with the verity-application 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void update(Context context)
        {
            send(context, updateMsg(context));
        }

        /// <summary>
        /// Directs verity-application to update the configuration register with the verity-application 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject updateMsg(Context context)
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

        /// <summary>
        /// Directs verity-application to update the configuration register with the verity-application 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] updateMsgPacked(Context context)
        {
            return packMsg(context, updateMsg(context));
        }

        /// <summary>
        /// Ask for status from the verity-application agent 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void status(Context context)
        {
            send(context, statusMsg(context));
        }

        /// <summary>
        /// Ask for status from the verity-application agent 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject statusMsg(Context context)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(GET_STATUS));
            msg.Add("@id", getNewId());
            addThread(msg);

            return msg;
        }

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] statusMsgPacked(Context context)
        {
            return packMsg(context, statusMsg(context));
        }

    }
}
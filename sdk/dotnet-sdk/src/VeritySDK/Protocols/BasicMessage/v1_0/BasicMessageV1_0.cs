using System;
using System.Collections.Generic;
using System.Json;
using System.Text;
using VeritySDK.Protocols;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.BasicMessage
{
    /// <summary>
    /// A class  for controlling a 1.0 BasicMessage protocol.
    /// </summary>
    public class BasicMessageV1_0 : AbstractProtocol
    {

        #region Protocol identificator

        /// <summary>
        /// The qualifier for message family. Uses the community qualifier.
        /// </summary>
        public override string qualifier() { return Util.COMMUNITY_MSG_QUALIFIER; }

        /// <summary>
        /// The name for message family.
        /// </summary>
        public override string family() { return "basicmessage"; }

        /// <summary>
        /// The version for message family.
        /// </summary>
        public override string version() { return "1.0"; }

        #endregion

        string forRelationship;
        string content;
        string sentTime;
        string localization;

        public static string SEND_MESSAGE = "send-message";

        #region Constructors

        /// <summary>
        /// Constructor for the 1.0 BasicMessageV1_0 object
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="content">The main text of the message </param>
        /// <param name="sentTime">The time the message was sent</param>
        /// <param name="localization">Locale data for localization vector (eg "en")</param>
        public BasicMessageV1_0(
            string forRelationship,
            string content,
            string sentTime,
            string localization)
        {
            this.forRelationship = forRelationship;
            this.content = content;
            this.sentTime = sentTime;
            this.localization = localization;
        }
        
        #endregion

        /// <summary>
        /// Directs verity-application to send the message 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void message(Context context)
        {
            send(context, messageMsg(context));
        }

        /// <summary>
        /// Directs verity-application to send the message 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject messageMsg(Context context)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(SEND_MESSAGE));
            msg.Add("@id", getNewId());
            addThread(msg);
            msg.Add("~for_relationship", this.forRelationship);
            msg.Add("content", this.content);
            msg.Add("sent_time", this.sentTime);
            msg.Add("~l10n", this.localization);

            return msg;
        }

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] messageMsgPacked(Context context)
        {
            return packMsg(context, messageMsg(context));
        }
    }
}

using System.Json;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.Conecting
{
    /// <summary>
    /// A class for controlling a 1.0 Connections protocol.
    /// </summary>
    public class ConnectionsV1_0 : AbstractProtocol
    {
        #region Protocol identificator

        /// <summary>
        /// The name for the message family.
        /// </summary>
        public override string qualifier() { return Util.COMMUNITY_MSG_QUALIFIER; }

        /// <summary>
        /// The qualifier for the message family. Uses the community qualifier.
        /// </summary>
        public override string family() { return "connections"; }

        /// <summary>
        /// The version for the message family.
        /// </summary>
        public override string version() { return "1.0"; }


        #endregion 

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        public ConnectionsV1_0() { }

        /// <summary>
        /// Constructor
        /// </summary>
        public ConnectionsV1_0(string threadId) : base(threadId) { }

        /// <summary>
        /// Constructor
        /// </summary>
        public ConnectionsV1_0(string forRelationship, string threadId) : base(threadId)
        {
            this.forRelationship = forRelationship;
            this.label = null;
            this.base64InviteURL = null;
        }

        /// <summary>
        /// Constructor
        /// </summary>
        public ConnectionsV1_0(string forRelationship, string label, string base64InviteURL)
        {
            this.forRelationship = forRelationship;
            this.label = label;
            this.base64InviteURL = base64InviteURL;
        }

        #endregion 

        string forRelationship;
        string label;
        string base64InviteURL;

        string ACCEPT_INVITE = "accept";
        string STATUS = "status";

        /// <summary>
        /// Sends the get status message to the connection
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void status(Context context)
        {
            send(context, statusMsg(context));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject statusMsg(Context context)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(STATUS));
            msg.Add("@id", getNewId());
            msg.Add("~for_relationship", forRelationship);
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

        /// <summary>
        /// Accepts connection defined by the given invitation 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public JsonObject acceptMsg(Context context)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(ACCEPT_INVITE));
            msg.Add("@id", getNewId());
            msg.Add("~for_relationship", forRelationship);
            addThread(msg);

            msg.Add("label", label);
            msg.Add("invite_url", base64InviteURL);

            return msg;
        }

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public void accept(Context context)
        {
            send(context, acceptMsg(context));
        }

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] acceptMsgPacked(Context context)
        {
            return packMsg(context, acceptMsg(context));
        }
    }
}
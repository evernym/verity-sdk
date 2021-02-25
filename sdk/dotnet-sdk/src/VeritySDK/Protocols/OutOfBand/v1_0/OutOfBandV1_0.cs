using System;
using System.Json;
using VeritySDK.Protocols;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.OutOfBand
{
    /// <summary>
    /// A class for controlling a 0.6 IssuerSetup protocol.
    /// </summary>
    public class OutOfBandV1_0 : AbstractProtocol
    {
        #region Protocol identificator

        /// <summary>
        /// The name for the message family.
        /// </summary>
        public override string qualifier() { return Util.COMMUNITY_MSG_QUALIFIER; }

        /// <summary>
        /// The qualifier for the message family. Uses the community qualifier.
        /// </summary>
        public override string family() { return "out-of-band"; }

        /// <summary>
        /// The version for the message family.
        /// </summary>
        public override string version() { return "1.0"; }

        #endregion 

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        public OutOfBandV1_0() { }

        /// <summary>
        /// Constructor
        /// </summary>
        public OutOfBandV1_0(string threadId) : base(threadId) { }

        /// <summary>
        /// Constructor
        /// </summary>
        public OutOfBandV1_0(string forRelationship, string inviteUrl)
        {
            this.forRelationship = forRelationship;
            this.inviteUrl = String.IsNullOrWhiteSpace(inviteUrl) ? null : inviteUrl;
        }

        #endregion 

        private string CONNECTION_INVITATION = "reuse";
        private string RELATIONSHIP_REUSED = "relationship-reused";

        private string inviteUrl;
        private string forRelationship;


        /// <summary>
        /// Direct the verity-application agent to reuse the relationship given. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public JsonObject handshakeReuseMsg(Context context)
        {
            JsonObject rtn = new JsonObject();
            rtn.Add("@type", messageType(CONNECTION_INVITATION));
            rtn.Add("@id", getNewId());
            rtn.Add("inviteUrl", this.inviteUrl);

            addThread(rtn);

            if (!String.IsNullOrWhiteSpace(this.forRelationship)) rtn.Add("~for_relationship", this.forRelationship);

            return rtn;
        }

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public void handshakeReuse(Context context)
        {
            send(context, handshakeReuseMsg(context));
        }

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] handshakeReuseMsgPacked(Context context)
        {
            return packMsg(context, handshakeReuseMsg(context));
        }
    }
}
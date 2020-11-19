using System;
using System.Json;
using System.Security.Policy;
using VeritySDK.Protocols;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.Relationship
{
    /// <summary>
    /// A class for controlling a 1.0 Relationship protocol.
    /// </summary>
    public class RelationshipV1_0 : AbstractProtocol
    {
        #region Protocol identificator

        /// <summary>
        /// The qualifier for the message family. Uses Evernym's qualifier.
        /// </summary>
        public override string qualifier() { return Util.EVERNYM_MSG_QUALIFIER; }

        /// <summary>
        /// The name for the message family.
        /// </summary>
        public override string family() { return "relationship"; }

        /// <summary>
        /// The version for the message family.
        /// </summary>
        public override string version() { return "1.0"; }

        #endregion 

        #region Constructors


        /// <summary>
        /// Constructor RelationshipV1_0 object
        /// </summary>
        public RelationshipV1_0()
        {
            this.created = true;
        }

        /// <summary>
        /// Constructor RelationshipV1_0 object
        /// </summary>
        public RelationshipV1_0(string label)
        {
            this.label = label;
            this.created = true;
        }

        /// <summary>
        /// Constructor RelationshipV1_0 object
        /// </summary>
        public RelationshipV1_0(string label, Url logoUrl)
        {
            this.label = label;
            this.logoUrl = logoUrl;

            this.created = true;
        }

        /// <summary>
        /// Constructor RelationshipV1_0 object
        /// </summary>
        public RelationshipV1_0(string label, Url logoUrl, string phoneNumber)
        {
            if (!String.IsNullOrWhiteSpace(label))
            {
                this.label = label;
            }
            else
            {
                this.label = "";
            }
            this.logoUrl = logoUrl;
            this.phoneNumber = phoneNumber;

            this.created = true;
        }

        /// <summary>
        /// Constructor RelationshipV1_0 object
        /// </summary>
        public RelationshipV1_0(string forRelationship, string threadId) : base(threadId)
        {
            this.forRelationship = forRelationship;
        }

        #endregion 

        static string CREATE = "create";
        static string CONNECTION_INVITATION = "connection-invitation";
        static string SMS_CONNECTION_INVITATION = "sms-connection-invitation";
        static string OUT_OF_BAND_INVITATION = "out-of-band-invitation";
        static string SMS_OUT_OF_BAND_INVITATION = "sms-out-of-band-invitation";

        string forRelationship;
        string label;
        Url logoUrl = null;
        string phoneNumber = null;

        // flag if this instance started the interaction
        bool created = false;


        /// <summary>
        /// Directs verity-application to create a new relationship 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void create(Context context)
        {
            send(context, createMsg(context));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject createMsg(Context context)
        {
            if (!created)
            {
                throw new ArgumentException("Unable to create relationship when NOT starting the interaction");
            }

            JsonObject rtn = new JsonObject();
            rtn.Add("@type", messageType(CREATE));
            rtn.Add("@id", getNewId());
            if (label != null)
                rtn.Add("label", label.ToString());
            if (logoUrl != null)
                rtn.Add("logoUrl", logoUrl.ToString());
            if (!String.IsNullOrWhiteSpace(phoneNumber))
                rtn.Add("phoneNumber", phoneNumber);
            addThread(rtn);
            return rtn;
        }

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] createMsgPacked(Context context)
        {
            return packMsg(context, createMsg(context));
        }

        /// <summary>
        /// Ask for aries invitation from the verity-application agent for the relationship created by this protocol 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void connectionInvitation(Context context)
        {
            send(context, connectionInvitationMsg(context));
        }

        /// <summary>
        /// Ask for aries invitation from the verity-application agent for the relationship created by this protocol 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="shortInvite">decides should short invite be provided as well</param>
        public void connectionInvitation(Context context, bool shortInvite)
        {
            send(context, connectionInvitationMsg(context, shortInvite));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject connectionInvitationMsg(Context context)
        {
            return connectionInvitationMsg(context, null);
        }

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="shortInvite">decides should short invite be provided as well</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject connectionInvitationMsg(Context context, bool? shortInvite)
        {
            JsonObject rtn = new JsonObject();
            rtn.Add("@type", messageType(CONNECTION_INVITATION));
            rtn.Add("@id", getNewId());
            rtn.Add("shortInvite", shortInvite);

            if (!String.IsNullOrWhiteSpace(forRelationship))
                rtn.Add("~for_relationship", forRelationship);

            addThread(rtn);

            return rtn;
        }

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] connectionInvitationMsgPacked(Context context)
        {
            return packMsg(context, connectionInvitationMsg(context));
        }

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="shortInvite">decides should short invite be provided as well</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] connectionInvitationMsgPacked(Context context, bool shortInvite)
        {
            return packMsg(context, connectionInvitationMsg(context, shortInvite));
        }

        /// <summary>
        /// Ask for sending SMS aries invitation from the verity-application agent for the relationship created by this protocol 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void smsConnectionInvitation(Context context)
        {
            send(context, smsConnectionInvitationMsg(context));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject smsConnectionInvitationMsg(Context context)
        {
            JsonObject rtn = new JsonObject();
            rtn.Add("@type", messageType(SMS_CONNECTION_INVITATION));
            rtn.Add("@id", getNewId());

            if (!String.IsNullOrWhiteSpace(forRelationship))
                rtn.Add("~for_relationship", forRelationship);

            addThread(rtn);
            return rtn;
        }

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] smsConnectionInvitationMsgPacked(Context context)
        {
            return packMsg(context, smsConnectionInvitationMsg(context));
        }

        /// <summary>
        /// Ask for aries out of band invitation from the verity-application agent for the relationship created by this protocol
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void outOfBandInvitation(Context context)
        {
            send(context, outOfBandInvitationMsg(context));
        }

        /// <summary>
        /// Ask for aries out of band invitation from the verity-application agent for the relationship created by this protocol
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="shortInvite">decides should short invite be provided as well</param>
        public void outOfBandInvitation(Context context, bool shortInvite)
        {
            send(context, outOfBandInvitationMsg(context, shortInvite));
        }

        /// <summary>
        /// Ask for aries out of band invitation from the verity-application agent for the relationship created by this protocol
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="shortInvite">decides should short invite be provided as well</param>
        /// <param name="goal">the initial intended goal of the relationship (this goal is expressed in the invite)</param>
        public void outOfBandInvitation(Context context, bool shortInvite, GoalCode goal)
        {
            send(context, outOfBandInvitationMsg(context, shortInvite, goal));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject outOfBandInvitationMsg(Context context)
        {
            return outOfBandInvitationMsg(context, null, null);
        }

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="shortInvite">decides should short invite be provided as well</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject outOfBandInvitationMsg(Context context, bool shortInvite)
        {
            return outOfBandInvitationMsg(context, shortInvite, null);
        }

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="shortInvite">decides should short invite be provided as well</param>
        /// <param name="goal">the initial intended goal of the relationship (this goal is expressed in the invite)</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject outOfBandInvitationMsg(Context context, bool? shortInvite, GoalCode? goal)
        {
            JsonObject rtn = new JsonObject();
            rtn.Add("@type", messageType(OUT_OF_BAND_INVITATION));
            rtn.Add("@id", getNewId());

            if (goal == null)
            {
                rtn.Add("goalCode", GoalCode.P2P_MESSAGING.code());
                rtn.Add("goal", GoalCode.P2P_MESSAGING.goalName());
            }
            else
            {
                rtn.Add("goalCode", goal?.code());
                rtn.Add("goal", goal?.goalName());
            }

            if (shortInvite != null)
            {
                rtn.Add("shortInvite", shortInvite);
            }

            if (!String.IsNullOrWhiteSpace(forRelationship))
                rtn.Add("~for_relationship", forRelationship);
            addThread(rtn);
            return rtn;
        }

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] outOfBandInvitationMsgPacked(Context context)
        {
            return packMsg(context, outOfBandInvitationMsg(context));
        }

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="shortInvite">decides should short invite be provided as well</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] outOfBandInvitationMsgPacked(Context context, bool shortInvite)
        {
            return packMsg(context, outOfBandInvitationMsg(context, shortInvite));
        }

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="shortInvite">decides should short invite be provided as well</param>
        /// <param name="goal">the initial intended goal of the relationship (this goal is expressed in the invite)</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] outOfBandInvitationMsgPacked(Context context, bool shortInvite, GoalCode goal)
        {
            return packMsg(context, outOfBandInvitationMsg(context, shortInvite, goal));
        }

        /// <summary>
        /// Ask for sending SMS aries out of band invitation from the verity-application agent for the relationship created by this protocol 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void smsOutOfBandInvitation(Context context)
        {
            send(context, smsOutOfBandInvitationMsg(context));
        }

        /// <summary>
        /// Ask for sending SMS aries out of band invitation from the verity-application agent for the relationship created by this protocol 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="goal">the initial intended goal of the relationship (this goal is expressed in the invite)</param>
        public void smsOutOfBandInvitation(Context context, GoalCode goal)
        {
            send(context, smsOutOfBandInvitationMsg(context, goal));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject smsOutOfBandInvitationMsg(Context context)
        {
            return smsOutOfBandInvitationMsg(context, null);
        }

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="goal">the initial intended goal of the relationship (this goal is expressed in the invite)</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject smsOutOfBandInvitationMsg(Context context, GoalCode? goal)
        {
            JsonObject rtn = new JsonObject();
            rtn.Add("@type", messageType(SMS_OUT_OF_BAND_INVITATION));
            rtn.Add("@id", getNewId());

            if (goal == null)
            {
                rtn.Add("goalCode", GoalCode.P2P_MESSAGING.code());
                rtn.Add("goal", GoalCode.P2P_MESSAGING.goalName());
            }
            else
            {
                rtn.Add("goalCode", goal?.code());
                rtn.Add("goal", goal?.goalName());
            }

            if (!String.IsNullOrWhiteSpace(forRelationship))
                rtn.Add("~for_relationship", forRelationship);
            addThread(rtn);
            return rtn;
        }

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] smsOutOfBandInvitationMsgPacked(Context context)
        {
            return packMsg(context, smsOutOfBandInvitationMsg(context));
        }

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="goal">the initial intended goal of the relationship (this goal is expressed in the invite)</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] smsOutOfBandInvitationMsgPacked(Context context, GoalCode goal)
        {
            return packMsg(context, smsOutOfBandInvitationMsg(context, goal));
        }
    }
}
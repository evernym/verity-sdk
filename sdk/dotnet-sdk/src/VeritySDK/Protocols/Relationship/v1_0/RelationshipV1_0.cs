using System;
using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// An abstract class for controlling a 1.0 Relationship protocol.
    /// </summary>
    public abstract class RelationshipV1_0 : AbstractProtocol
    {
        /// <summary>
        /// Constructor RelationshipV1_0 object
        /// </summary>
        public RelationshipV1_0() { }

        /// <summary>
        /// Constructor RelationshipV1_0 object
        /// </summary>
        /// <param name="threadId"></param>
        public RelationshipV1_0(string threadId) : base(threadId) { }

        /// <summary>
        /// The qualifier for the message family. Uses Evernym's qualifier.
        /// </summary>
        string QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
        
        /// <summary>
        /// The name for the message family.
        /// </summary>
        string FAMILY = "relationship";

        /// <summary>
        /// The version for the message family.
        /// </summary>
        string VERSION = "1.0";


        /// <see cref="MessageFamily.qualifier"/>
        public override string qualifier() { return QUALIFIER; }
        
        /// <see cref="MessageFamily.family"/>
        public override string family() { return FAMILY; }

        /// <see cref="MessageFamily.version"/>
        public override string version() { return VERSION; }

        /// <summary>
        /// Directs verity-application to create a new relationship 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void create(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="create(Context)"/>
        public abstract JsonObject createMsg(Context context);

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="create(Context)"/>
        public abstract byte[] createMsgPacked(Context context);

        /// <summary>
        /// Ask for aries invitation from the verity-application agent for the relationship created by this protocol 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void connectionInvitation(Context context);

        /// <summary>
        /// Ask for aries invitation from the verity-application agent for the relationship created by this protocol 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="shortInvite">decides should short invite be provided as well</param>
        public abstract void connectionInvitation(Context context, bool shortInvite);

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="connectionInvitation(Context)"/>
        public abstract JsonObject connectionInvitationMsg(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="shortInvite">decides should short invite be provided as well</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="connectionInvitation(Context)"/>
        public abstract JsonObject connectionInvitationMsg(Context context, bool? shortInvite);

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="connectionInvitation(Context)"/>
        public abstract byte[] connectionInvitationMsgPacked(Context context);

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="shortInvite">decides should short invite be provided as well</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="connectionInvitation(Context)"/>
        public abstract byte[] connectionInvitationMsgPacked(Context context, bool shortInvite);

        /// <summary>
        /// Ask for sending SMS aries invitation from the verity-application agent for the relationship created by this protocol 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void smsConnectionInvitation(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="connectionInvitation(Context)"/>
        public abstract JsonObject smsConnectionInvitationMsg(Context context);

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="connectionInvitation(Context)"/>
        public abstract byte[] smsConnectionInvitationMsgPacked(Context context);

        /// <summary>
        /// Ask for aries out of band invitation from the verity-application agent for the relationship created by this protocol
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void outOfBandInvitation(Context context);

        /// <summary>
        /// Ask for aries out of band invitation from the verity-application agent for the relationship created by this protocol
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="shortInvite">decides should short invite be provided as well</param>
        public abstract void outOfBandInvitation(Context context, bool shortInvite);

        /// <summary>
        /// Ask for aries out of band invitation from the verity-application agent for the relationship created by this protocol
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="shortInvite">decides should short invite be provided as well</param>
        /// <param name="goal">the initial intended goal of the relationship (this goal is expressed in the invite)</param>
        public abstract void outOfBandInvitation(Context context, bool shortInvite, GoalCode goal);

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="connectionInvitation(Context)"/>
        public abstract JsonObject outOfBandInvitationMsg(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="shortInvite">decides should short invite be provided as well</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="connectionInvitation(Context)"/>
        public abstract JsonObject outOfBandInvitationMsg(Context context, bool shortInvite);

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="shortInvite">decides should short invite be provided as well</param>
        /// <param name="goal">the initial intended goal of the relationship (this goal is expressed in the invite)</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="connectionInvitation(Context)"/>
        public abstract JsonObject outOfBandInvitationMsg(Context context, bool? shortInvite, GoalCode? goal);

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="connectionInvitation(Context)"/>
        public abstract byte[] outOfBandInvitationMsgPacked(Context context);

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="shortInvite">decides should short invite be provided as well</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="connectionInvitation(Context)"/>
        public abstract byte[] outOfBandInvitationMsgPacked(Context context, bool shortInvite);

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="shortInvite">decides should short invite be provided as well</param>
        /// <param name="goal">the initial intended goal of the relationship (this goal is expressed in the invite)</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="connectionInvitation(Context)"/>
        public abstract byte[] outOfBandInvitationMsgPacked(Context context, bool shortInvite, GoalCode goal);

        /// <summary>
        /// Ask for sending SMS aries out of band invitation from the verity-application agent for the relationship created by this protocol 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void smsOutOfBandInvitation(Context context);

        /// <summary>
        /// Ask for sending SMS aries out of band invitation from the verity-application agent for the relationship created by this protocol 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="goal">the initial intended goal of the relationship (this goal is expressed in the invite)</param>
        public abstract void smsOutOfBandInvitation(Context context, GoalCode goal);

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="connectionInvitation(Context)"/>
        public abstract JsonObject smsOutOfBandInvitationMsg(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="goal">the initial intended goal of the relationship (this goal is expressed in the invite)</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="connectionInvitation(Context)"/>
        public abstract JsonObject smsOutOfBandInvitationMsg(Context context, GoalCode? goal);

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="connectionInvitation(Context)"/>
        public abstract byte[] smsOutOfBandInvitationMsgPacked(Context context);

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="goal">the initial intended goal of the relationship (this goal is expressed in the invite)</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="connectionInvitation(Context)"/>
        public abstract byte[] smsOutOfBandInvitationMsgPacked(Context context, GoalCode goal);
    }
}
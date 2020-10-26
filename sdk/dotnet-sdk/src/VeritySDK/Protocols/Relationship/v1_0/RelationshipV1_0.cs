using System;
using System.Json;

namespace VeritySDK
{
    /**
     * An interface for controlling a 1.0 Relationship protocol.
     */
    public abstract class RelationshipV1_0 : AbstractProtocol
    {
        public RelationshipV1_0() { }
        public RelationshipV1_0(string threadId) : base(threadId) { }

        /**
         * The qualifier for the message family. Uses Evernym's qualifier.
         */
        string QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
        /**
         * The name for the message family.
         */
        string FAMILY = "relationship";
        /**
         * The version for the message family.
         */
        string VERSION = "1.0";


        /**
         * @see MessageFamily#qualifier()
         */
        public override string qualifier() { return QUALIFIER; }
        /**
         * @see MessageFamily#family()
         */
        public override string family() { return FAMILY; }
        /**
         * @see MessageFamily#version()
         */
        public override string version() { return VERSION; }

        /**
         * Directs verity-application to create a new relationship
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         */
        public abstract void create(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #create
         */
        public abstract JsonObject createMsg(Context context);
        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         *
         * @see #create
         */
        public abstract byte[] createMsgPacked(Context context);

        /**
         * Ask for aries invitation from the verity-application agent for the relationship created by this protocol
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         */
        public abstract void connectionInvitation(Context context);

        /**
         * Ask for aries invitation from the verity-application agent for the relationship created by this protocol
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @param shortInvite decides should short invite be provided as well
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         */
        public abstract void connectionInvitation(Context context, bool shortInvite);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #connectionInvitation
         */
        public abstract JsonObject connectionInvitationMsg(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @param shortInvite decides should short invite be provided as well
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #connectionInvitation
         */
        public abstract JsonObject connectionInvitationMsg(Context context, bool? shortInvite);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         *
         * @see #connectionInvitation
         */
        public abstract byte[] connectionInvitationMsgPacked(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @param shortInvite decides should short invite be provided as well
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         *
         * @see #connectionInvitation
         */
        public abstract byte[] connectionInvitationMsgPacked(Context context, bool shortInvite);

        /**
         * Ask for sending SMS aries invitation from the verity-application agent for the relationship created by this protocol
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         */
        public abstract void smsConnectionInvitation(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #connectionInvitation
         */
        public abstract JsonObject smsConnectionInvitationMsg(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         *
         * @see #connectionInvitation
         */
        public abstract byte[] smsConnectionInvitationMsgPacked(Context context);

        /**
         * Ask for aries out of band invitation from the verity-application agent for the relationship created by this protocol
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         */
        public abstract void outOfBandInvitation(Context context);

        /**
         * Ask for aries out of band invitation from the verity-application agent for the relationship created by this protocol
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @param shortInvite decides should short invite be provided as well
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         */
        public abstract void outOfBandInvitation(Context context, bool shortInvite);


        /**
         * Ask for aries out of band invitation from the verity-application agent for the relationship created by this protocol
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @param shortInvite decides should short invite be provided as well
         * @param goal the initial intended goal of the relationship (this goal is expressed in the invite)
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         */
        public abstract void outOfBandInvitation(Context context, bool shortInvite, GoalCode goal);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #connectionInvitation
         */
        public abstract JsonObject outOfBandInvitationMsg(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @param shortInvite decides should short invite be provided as well
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #connectionInvitation
         */
        public abstract JsonObject outOfBandInvitationMsg(Context context, bool shortInvite);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @param shortInvite decides should short invite be provided as well
         * @param goal the initial intended goal of the relationship (this goal is expressed in the invite)
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #connectionInvitation
         */
        public abstract JsonObject outOfBandInvitationMsg(Context context, bool? shortInvite, GoalCode? goal);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         *
         * @see #connectionInvitation
         */
        public abstract byte[] outOfBandInvitationMsgPacked(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @param shortInvite decides should short invite be provided as well
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         *
         * @see #connectionInvitation
         */
        public abstract byte[] outOfBandInvitationMsgPacked(Context context, bool shortInvite);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @param shortInvite decides should short invite be provided as well
         * @param goal the initial intended goal of the relationship (this goal is expressed in the invite)
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         *
         * @see #connectionInvitation
         */
        public abstract byte[] outOfBandInvitationMsgPacked(Context context, bool shortInvite, GoalCode goal);

        /**
         * Ask for sending SMS aries out of band invitation from the verity-application agent for the relationship created by this protocol
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         */
        public abstract void smsOutOfBandInvitation(Context context);

        /**
         * Ask for sending SMS aries out of band invitation from the verity-application agent for the relationship created by this protocol
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @param goal the initial intended goal of the relationship (this goal is expressed in the invite)
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         */
        public abstract void smsOutOfBandInvitation(Context context, GoalCode goal);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #connectionInvitation
         */
        public abstract JsonObject smsOutOfBandInvitationMsg(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @param goal the initial intended goal of the relationship (this goal is expressed in the invite)
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #connectionInvitation
         */
        public abstract JsonObject smsOutOfBandInvitationMsg(Context context, GoalCode? goal);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         *
         * @see #connectionInvitation
         */
        public abstract byte[] smsOutOfBandInvitationMsgPacked(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @param goal the initial intended goal of the relationship (this goal is expressed in the invite)
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         *
         * @see #connectionInvitation
         */
        public abstract byte[] smsOutOfBandInvitationMsgPacked(Context context, GoalCode goal);
    }
}
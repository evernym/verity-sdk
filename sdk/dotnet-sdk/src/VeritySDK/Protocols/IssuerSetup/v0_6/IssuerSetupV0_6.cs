using System.Json;

namespace VeritySDK
{

    /**
     * An interface for controlling a 0.6 IssuerSetup protocol.
     */
    public abstract class IssuerSetupV0_6 : AbstractProtocol
    {
        public IssuerSetupV0_6() { }
        public IssuerSetupV0_6(string threadId) : base(threadId) { }

        /**
         * @see MessageFamily#qualifier()
         */
        public override string qualifier() { return Util.EVERNYM_MSG_QUALIFIER; }
        /**
         * @see MessageFamily#family()
         */
        public override string family() { return "issuer-setup"; }
        /**
         * @see MessageFamily#version()
         */
        public override string version() { return "0.6"; }


        /**
        Name for 'create' control message
         */
        public string CREATE = "create";

        /**
         Name for 'current-public-identifier' control message
         */
        public string CURRENT_PUBLIC_IDENTIFIER = "current-public-identifier";

        /**
         * Directs verity-application to start and create an issuer identity and set it up
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
         * Asks the verity-application for the current issuer identity that is setup.
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         */
        public abstract void currentPublicIdentifier(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #currentPublicIdentifier
         */
        public abstract JsonObject currentPublicIdentifierMsg(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         *
         * @see #currentPublicIdentifier
         */
        public abstract byte[] currentPublicIdentifierMsgPacked(Context context);
    }
}
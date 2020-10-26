using System.Json;

namespace VeritySDK
{
    /**
     * An interface for controlling a 0.6 IssuerSetup protocol.
     */
    public abstract class OutOfBandV1_0 : AbstractProtocol
    {
        public OutOfBandV1_0() { }
        public OutOfBandV1_0(string threadId) : base(threadId) { }

        /**
         * The qualifier for the message family. Uses the community qualifier.
         */
        public string QUALIFIER = Util.COMMUNITY_MSG_QUALIFIER;
        /**
         * The name for the message family.
         */
        public string FAMILY = "out-of-band";
        /**
         * The version for the message family.
         */
        public string VERSION = "1.0";

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
         * Direct the verity-application agent to reuse the relationship given.
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         */
        public abstract void handshakeReuse(Context context);
        /**
         * Creates the control message without packaging and sending it.
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #handshakeReuse
         */
        public abstract JsonObject handshakeReuseMsg(Context context);
        /**
         * Creates and packages message without sending it.
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         *
         * @see #handshakeReuse
         */
        public abstract byte[] handshakeReuseMsgPacked(Context context);
    }
}
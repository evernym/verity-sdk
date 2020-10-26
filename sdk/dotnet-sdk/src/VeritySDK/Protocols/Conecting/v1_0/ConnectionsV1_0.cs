using System.Json;

namespace VeritySDK
{
    /**
     * An interface for controlling a 1.0 Connections protocol.
     *
     * @see <a href="https://github.com/hyperledger/aries-rfcs/tree/9b0aaa39df7e8bd434126c4b33c097aae78d65bf/features/0160-connection-protocol" target="_blank" rel="noopener noreferrer">Aries 0160: Connection Protocol</a>
     */
    public abstract class ConnectionsV1_0 : AbstractProtocol
    {
        public ConnectionsV1_0() { }
        public ConnectionsV1_0(string threadId) : base(threadId) { }

        /**
         * The qualifier for the message family. Uses the community qualifier.
         */
        public string QUALIFIER = Util.COMMUNITY_MSG_QUALIFIER;
        /**
         * The name for the message family.
         */
        public string FAMILY = "connections";
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
         * Sends the get status message to the connection
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         */
        public abstract void status(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #status
         */
        public abstract JsonObject statusMsg(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         * 
         * @see #status
         */
        public abstract byte[] statusMsgPacked(Context context);

        /**
         * Accepts connection defined by the given invitation
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         */
        public abstract void accept(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #accept
         */
        public abstract JsonObject acceptMsg(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         *
         * @see #accept
         */
        public abstract byte[] acceptMsgPacked(Context context);
    }
}

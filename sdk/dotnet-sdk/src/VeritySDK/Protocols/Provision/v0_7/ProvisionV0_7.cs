using System;
using System.Json;

namespace VeritySDK
{
    /**
     * An interface for controlling a 0.7 Provision protocol.
     */
    public abstract class ProvisionV0_7 : AbstractProtocol
    {
        /**
         * The qualifier for the message family. Uses Evernym's qualifier.
         */
        string QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
        /**
         * The name for the message family.
         */
        string FAMILY = "agent-provisioning";
        /**
         * The version for the message family.
         */
        string VERSION = "0.7";


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
         Name for 'create-edge-agent' control message
         */
        public string CREATE_EDGE_AGENT = "create-edge-agent";

        /**
         * Sends provisioning message that directs the creation of an agent to the to verity-application
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         * @return new Context with provisioned details
         */
        public abstract Context provision(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #provision
         */
        public abstract JsonObject provisionMsg(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         *
         * @see #provision
         */
        public abstract byte[] provisionMsgPacked(Context context);
    }
}
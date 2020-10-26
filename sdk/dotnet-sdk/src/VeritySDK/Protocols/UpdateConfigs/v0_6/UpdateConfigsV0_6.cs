using System;
using System.Json;

namespace VeritySDK
{
    /**
     * An interface for controlling a 0.6 UpdateConfigs protocol.
     */
    public abstract class UpdateConfigsV0_6 : AbstractProtocol
    {
        public UpdateConfigsV0_6() { }
        public UpdateConfigsV0_6(string threadId) : base(threadId) { }

        /**
         * The qualifier for the message family. Uses Evernym's qualifier.
         */
        string QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
        /**
         * The name for the message family.
         */
        string FAMILY = "update-configs";
        /**
         * The version for the message family.
         */
        string VERSION = "0.6";


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
         * Directs verity-application to update the configuration register with the verity-application
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @ when wallet operations fails or given invalid context
         */
        public abstract void update(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @ when given invalid context
         *
         * @see #updateMsg
         */
        public abstract JsonObject updateMsg(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @ when wallet operations fails or given invalid context
         *
         * @see #updateMsg
         */
        public abstract byte[] updateMsgPacked(Context context);

        /**
         * Ask for status from the verity-application agent
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @ when wallet operations fails or given invalid context
         */
        public abstract void status(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @ when given invalid context
         *
         * @see #status
         */
        public abstract JsonObject statusMsg(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @ when wallet operations fails or given invalid context
         *
         * @see #status
         */
        public abstract byte[] statusMsgPacked(Context context);
    }
}
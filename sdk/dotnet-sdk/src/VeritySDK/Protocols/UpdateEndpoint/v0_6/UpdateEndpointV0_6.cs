using System;
using System.Collections.Generic;
using System.Json;
using System.Text;

namespace VeritySDK
{
    /**
     * An interface for controlling a 0.6 UpdateEndpoint protocol.
     */
    public abstract class UpdateEndpointV0_6 : AbstractProtocol
    {
        /**
         * The qualifier for the message family. Uses Evernym's qualifier.
         */
        public string QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
        /**
         * The name for the message family.
         */
        public string FAMILY = "configs";
        /**
         * The version for the message family.
         */
        public string VERSION = "0.6";


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
         Name for 'update-endpoint' control message
         */
        public string UPDATE_ENDPOINT = "UPDATE_COM_METHOD";

        /**
         * Directs verity-application to update the used endpoint for out-going signal message to the
         * endpoint contained in the context object. See: {@link Context#endpointUrl()}
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         */
        public abstract void update(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #updateMsg
         */
        public abstract JsonObject updateMsg(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         *
         * @see #updateMsg
         */
        public abstract byte[] updateMsgPacked(Context context);
    }
}

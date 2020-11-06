using System;
using System.Collections.Generic;
using System.Json;
using System.Text;

namespace VeritySDK
{
    /// <summary>
    /// An abstract class for controlling a 0.6 UpdateEndpoint protocol.
    /// </summary>
    public abstract class UpdateEndpointV0_6 : AbstractProtocol
    {
        /// <summary>
        /// The qualifier for the message family. Uses Evernym's qualifier.
        /// </summary>
        public string QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;

        /// <summary>
        /// The name for the message family.
        /// </summary>
        public string FAMILY = "configs";

        /// <summary>
        /// The version for the message family.
        /// </summary>
        public string VERSION = "0.6";

        /// <see cref="MessageFamily.qualifier"/>
        public override string qualifier() { return QUALIFIER; }

        /// <see cref="MessageFamily.family"/>
        public override string family() { return FAMILY; }

        /// <see cref="MessageFamily.version"/>
        public override string version() { return VERSION; }

        /// <summary>
        /// Name for 'update-endpoint' control message
        /// </summary>
        public string UPDATE_ENDPOINT = "UPDATE_COM_METHOD";

        /// <summary>
        /// Directs verity-application to update the used endpoint for out-going signal message to the
        /// endpoint contained in the context object. See: {@link Context#endpointUrl()}
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void update(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="updateMsg(Context)"/>
        public abstract JsonObject updateMsg(Context context);

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="updateMsg(Context)"/>
        public abstract byte[] updateMsgPacked(Context context);
    }
}

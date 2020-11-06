using System;
using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// An abstract class for controlling a 0.7 Provision protocol.
    /// </summary>
    public abstract class ProvisionV0_7 : AbstractProtocol
    {
        /// <summary>
        /// The qualifier for the message family. Uses Evernym's qualifier.
        /// </summary>
        string QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;

        /// <summary>
        /// The name for the message family.
        /// </summary>
        string FAMILY = "agent-provisioning";

        /// <summary>
        /// The version for the message family.
        /// </summary>
        string VERSION = "0.7";

        /// <see cref="MessageFamily.qualifier"/>
        public override string qualifier() { return QUALIFIER; }

        /// <see cref="MessageFamily.family"/>
        public override string family() { return FAMILY; }

        /// <see cref="MessageFamily.version"/>
        public override string version() { return VERSION; }

        /// <summary>
        /// Name for 'create-edge-agent' control message
        /// </summary>
        public string CREATE_EDGE_AGENT = "create-edge-agent";

        /// <summary>
        /// Sends provisioning message that directs the creation of an agent to the to verity-application 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>new Context with provisioned details</returns>
        public abstract Context provision(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="provision(Context)"/>
        public abstract JsonObject provisionMsg(Context context);

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="provision(Context)"/>
        public abstract byte[] provisionMsgPacked(Context context);
    }
}
using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// An class for controlling a 1.0 Connections protocol.
    /// </summary>
    public abstract class ConnectionsV1_0 : AbstractProtocol
    {
        /// <summary>
        /// Constructor
        /// </summary>
        public ConnectionsV1_0() { }

        /// <summary>
        /// Constructor
        /// </summary>
        public ConnectionsV1_0(string threadId) : base(threadId) { }

        /// <summary>
        /// The qualifier for the message family. Uses the community qualifier.
        /// </summary>
        public string QUALIFIER = Util.COMMUNITY_MSG_QUALIFIER;

        /// <summary>
        /// The name for the message family.
        /// </summary>
        public string FAMILY = "connections";

        /// <summary>
        /// The version for the message family.
        /// </summary>
        public string VERSION = "1.0";

        /// <see cref="MessageFamily.qualifier"/>
        public override string qualifier() { return QUALIFIER; }

        /// <see cref="MessageFamily.family"/>
        public override string family() { return FAMILY; }

        /// <see cref="MessageFamily.version"/>
        public override string version() { return VERSION; }

        /// <summary>
        /// Sends the get status message to the connection
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void status(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="status(Context)"/>
        public abstract JsonObject statusMsg(Context context);

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="status(Context)"/>
        public abstract byte[] statusMsgPacked(Context context);

        /// <summary>
        /// Accepts connection defined by the given invitation 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void accept(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="accept(Context)"/>
        public abstract JsonObject acceptMsg(Context context);

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="accept(Context)"/>
        public abstract byte[] acceptMsgPacked(Context context);
    }
}

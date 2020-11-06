using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// An abstract class for controlling a 0.6 IssuerSetup protocol.
    /// </summary>
    public abstract class OutOfBandV1_0 : AbstractProtocol
    {
        /// <summary>
        /// Constructor
        /// </summary>
        public OutOfBandV1_0() { }

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="threadId">threadId given ID used for the thread. MUST not be null.</param>
        public OutOfBandV1_0(string threadId) : base(threadId) { }

        /// <summary>
        /// The qualifier for the message family. Uses the community qualifier.
        /// </summary>
        public string QUALIFIER = Util.COMMUNITY_MSG_QUALIFIER;
        
        /// <summary>
        /// The name for the message family.
        /// </summary>
        public string FAMILY = "out-of-band";

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
        /// Direct the verity-application agent to reuse the relationship given. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void handshakeReuse(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="handshakeReuse"/>
        public abstract JsonObject handshakeReuseMsg(Context context);

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="handshakeReuse(Context)"/>
        public abstract byte[] handshakeReuseMsgPacked(Context context);
    }
}
using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// An class for controlling a 0.6 IssuerSetup protocol.
    /// </summary>
    public abstract class IssuerSetupV0_6 : AbstractProtocol
    {
        /// <summary>
        /// Constructor
        /// </summary>
        public IssuerSetupV0_6() { }

        /// <summary>
        /// Constructor
        /// </summary>
        public IssuerSetupV0_6(string threadId) : base(threadId) { }

        /// <see cref="MessageFamily.qualifier"/>
        public override string qualifier() { return Util.EVERNYM_MSG_QUALIFIER; }

        /// <see cref="MessageFamily.family"/>
        public override string family() { return "issuer-setup"; }

        /// <see cref="MessageFamily.version"/>
        public override string version() { return "0.6"; }

        /// <summary>
        /// Name for 'create' control message
        /// </summary>
        public string CREATE = "create";

        /// <summary>
        /// Name for 'current-public-identifier' control message
        /// </summary>
        public string CURRENT_PUBLIC_IDENTIFIER = "current-public-identifier";

        /// <summary>
        /// Directs verity-application to start and create an issuer identity and set it up 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void create(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="create(Context)"/>
        public abstract JsonObject createMsg(Context context);

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="create(Context)"/>
        public abstract byte[] createMsgPacked(Context context);

        /// <summary>
        /// Asks the verity-application for the current issuer identity that is setup.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void currentPublicIdentifier(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="currentPublicIdentifier(Context)"/>
        public abstract JsonObject currentPublicIdentifierMsg(Context context);

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="currentPublicIdentifier(Context)"/>
        public abstract byte[] currentPublicIdentifierMsgPacked(Context context);
    }
}
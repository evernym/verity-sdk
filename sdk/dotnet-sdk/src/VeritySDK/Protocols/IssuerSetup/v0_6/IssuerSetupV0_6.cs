using System.Json;
using VeritySDK.Protocols;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.IssuerSetup
{
    /// <summary>
    /// A class for controlling a 0.6 IssuerSetup protocol.
    /// </summary>
    public class IssuerSetupV0_6 : AbstractProtocol
    {
        #region Protocol identificator

        /// <summary>
        /// The qualifier for the message family. Uses Evernym's qualifier.
        /// </summary>
        public override string qualifier() { return Util.EVERNYM_MSG_QUALIFIER; }

        /// <summary>
        /// The name for the message family.
        /// </summary>
        public override string family() { return "issuer-setup"; }

        /// <summary>
        /// The version for the message family.
        /// </summary>
        public override string version() { return "0.6"; }

        #endregion 

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        public IssuerSetupV0_6() { }

        /// <summary>
        /// Constructor
        /// </summary>
        public IssuerSetupV0_6(string threadId) : base(threadId) { }

        #endregion 

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
        public void create(Context context)
        {
            send(context, createMsg(context));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject createMsg(Context context)
        {
            JsonObject message = new JsonObject();
            message.Add("@type", messageType(CREATE));
            message.Add("@id", getNewId());
            addThread(message);
            return message;
        }

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] createMsgPacked(Context context)
        {
            return packMsg(context, createMsg(context));
        }

        /// <summary>
        /// Asks the verity-application for the current issuer identity that is setup.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void currentPublicIdentifier(Context context)
        {
            send(context, currentPublicIdentifierMsg(context));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject currentPublicIdentifierMsg(Context context)
        {
            JsonObject message = new JsonObject();
            message.Add("@type", messageType(CURRENT_PUBLIC_IDENTIFIER));
            message.Add("@id", getNewId());
            addThread(message);
            return message;
        }

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] currentPublicIdentifierMsgPacked(Context context)
        {
            return packMsg(context, currentPublicIdentifierMsg(context));
        }

    }
}
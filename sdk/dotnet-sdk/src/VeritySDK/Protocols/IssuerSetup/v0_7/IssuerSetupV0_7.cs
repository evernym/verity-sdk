using System.Json;
using VeritySDK.Protocols;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.IssuerSetup
{
    /// <summary>
    /// A class for controlling a 0.6 IssuerSetup protocol.
    /// </summary>
    public class IssuerSetupV0_7 : AbstractProtocol
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
        public override string version() { return "0.7"; }

        #endregion 

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        public IssuerSetupV0_7() { }

        /// <summary>
        /// Constructor
        /// </summary>
        public IssuerSetupV0_7(string threadId) : base(threadId) { }

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
        /// <param name="ledgerPrefix">a string indicating the ledger that the issuer identifier should be created for. Currently supported values are ["did:sov", "did:indy:sovrin:builder", "did:indy:sovrin:staging", "did:indy:sovrin"]</param>
        public void create(Context context, string ledgerPrefix)
        {
            send(context, createMsg(context, ledgerPrefix));
        }

        /// <summary>
        /// Directs verity-application to start and create an issuer identity and set it up
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="ledgerPrefix">a string indicating the ledger that the issuer identifier should be created for. Currently supported values are ["did:sov", "did:indy:sovrin:builder", "did:indy:sovrin:staging", "did:indy:sovrin"]</param>
        /// <param name="endorser">Optional: the desired endorser did. If left empty then Verity will attempt to use it's own endorser, otherwise it will return a transaction for manual endorsement</param>
        public void create(Context context, string ledgerPrefix, string endorser)
        {
            send(context, createMsg(context, ledgerPrefix, endorser));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="ledgerPrefix">a string indicating the ledger that the issuer identifier should be created for. Currently supported values are ["did:sov", "did:indy:sovrin:builder", "did:indy:sovrin:staging", "did:indy:sovrin"]</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject createMsg(Context context, string ledgerPrefix)
        {
            JsonObject message = new JsonObject();
            message.Add("@type", messageType(CREATE));
            message.Add("@id", getNewId());
            message.Add("ledgerPrefix", ledgerPrefix);
            addThread(message);
            return message;
        }

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="ledgerPrefix">a string indicating the ledger that the issuer identifier should be created for. Currently supported values are ["did:sov", "did:indy:sovrin:builder", "did:indy:sovrin:staging", "did:indy:sovrin"]</param>
        /// <param name="endorser">Optional: the desired endorser did. If left empty then Verity will attempt to use it's own endorser, otherwise it will return a transaction for manual endorsement</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject createMsg(Context context, string ledgerPrefix, string endorser)
        {
            JsonObject message = new JsonObject();
            message.Add("@type", messageType(CREATE));
            message.Add("@id", getNewId());
            message.Add("endorser", endorser);
            message.Add("ledgerPrefix", ledgerPrefix);
            addThread(message);
            return message;
        }

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="ledgerPrefix">a string indicating the ledger that the issuer identifier should be created for. Currently supported values are ["did:sov", "did:indy:sovrin:builder", "did:indy:sovrin:staging", "did:indy:sovrin"]</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] createMsgPacked(Context context, string ledgerPrefix)
        {
            return packMsg(context, createMsg(context, ledgerPrefix));
        }

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="ledgerPrefix">a string indicating the ledger that the issuer identifier should be created for. Currently supported values are ["did:sov", "did:indy:sovrin:builder", "did:indy:sovrin:staging", "did:indy:sovrin"]</param>
        /// <param name="endorser">Optional: the desired endorser did. If left empty then Verity will attempt to use it's own endorser, otherwise it will return a transaction for manual endorsement</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] createMsgPacked(Context context, string ledgerPrefix, string endorser)
        {
            return packMsg(context, createMsg(context, ledgerPrefix, endorser));
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
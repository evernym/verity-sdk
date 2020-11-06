using System;
using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// An abstract class for controlling a 0.6 WriteSchema protocol.
    /// </summary>
    public abstract class WriteSchemaV0_6 : AbstractProtocol
    {
        /// <summary>
        /// Constructor
        /// </summary>
        public WriteSchemaV0_6() { }

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="threadId">the thread id of the already started protocol</param>
        public WriteSchemaV0_6(string threadId) : base(threadId) { }

        /// <summary>
        /// The qualifier for the message family. Uses Evernym's qualifier.
        /// </summary>
        string QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
        
        /// <summary>
        /// The name for the message family.
        /// </summary>
        string FAMILY = "write-schema";

        /// <summary>
        /// The version for the message family.
        /// </summary>
        string VERSION = "0.6";

        /// <see cref="MessageFamily.qualifier"/> 
        public override string qualifier() { return QUALIFIER; }

        /// <see cref="MessageFamily.family"/> 
        public override string family() { return FAMILY; }

        /// <see cref="MessageFamily.version"/> 
        public override string version() { return VERSION; }

        /// <summary>
        /// Name for 'write' control message
        /// </summary>
        public string WRITE_SCHEMA = "write";

        /// <summary>
        /// Directs verity-application to write the specified Schema to the Ledger 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void write(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="write(Context)"/>
        public abstract JsonObject writeMsg(Context context);

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public abstract byte[] writeMsgPacked(Context context);
    }
}
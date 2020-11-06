using System;
using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// An interface for controlling a 0.6 UpdateConfigs protocol.
    /// </summary>
    public abstract class UpdateConfigsV0_6 : AbstractProtocol
    {
        /// <summary>
        /// Constructor UpdateConfigsV0_6 object
        /// </summary>
        public UpdateConfigsV0_6() { }

        /// <summary>
        /// Constructor UpdateConfigsV0_6 object
        /// </summary>
        /// <param name="threadId">the thread id of the already started protocol</param>
        public UpdateConfigsV0_6(string threadId) : base(threadId) { }

        /// <summary>
        /// The qualifier for the message family. Uses Evernym's qualifier.
        /// </summary>
        string QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;

        /// <summary>
        /// The name for the message family.
        /// </summary>
        string FAMILY = "update-configs";

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
        /// Directs verity-application to update the configuration register with the verity-application 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void update(Context context);

        /// <summary>
        /// Directs verity-application to update the configuration register with the verity-application 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="updateMsg(Context)"/>
        public abstract JsonObject updateMsg(Context context);

        /// <summary>
        /// Directs verity-application to update the configuration register with the verity-application 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="updateMsg(Context)"/>
        public abstract byte[] updateMsgPacked(Context context);

        /// <summary>
        /// Ask for status from the verity-application agent 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void status(Context context);

        /// <summary>
        /// Ask for status from the verity-application agent 
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
    }
}
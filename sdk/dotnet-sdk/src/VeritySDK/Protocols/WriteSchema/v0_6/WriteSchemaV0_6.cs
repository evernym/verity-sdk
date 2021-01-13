using System;
using System.Json;
using VeritySDK.Protocols;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.WriteSchema
{
    /// <summary>
    /// A class for controlling a 0.6 WriteSchema protocol.
    /// </summary>
    public class WriteSchemaV0_6 : AbstractProtocol
    {
        #region Protocol identificator

        /// <summary>
        /// The qualifier for the message family. Uses Evernym's qualifier.
        /// </summary>
        public override string qualifier() { return Util.EVERNYM_MSG_QUALIFIER; }

        /// <summary>
        /// The name for the message family.
        /// </summary>
        public override string family() { return "write-schema"; }

        /// <summary>
        /// The version for the message family.
        /// </summary>
        public override string version() { return "0.6"; }

        #endregion 

        #region Constructors

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
        /// Constructor
        /// </summary>
        public WriteSchemaV0_6(string name, string version, params string[] attrs)
        {
            Utils.DbcUtil.requireStringNotNullOrEmpty(name, "name");
            Utils.DbcUtil.requireStringNotNullOrEmpty(version, "version");
            Utils.DbcUtil.requireArrayNotContainNull(attrs, "attrs");

            this.name = name;
            this.ver = version;
            this.attrs = attrs;
        }

        #endregion 

        // Name for 'write' control message
        string WRITE_SCHEMA = "write";

        string name;
        string ver;
        string[] attrs;


        /// <summary>
        /// Directs verity-application to write the specified Schema to the Ledger 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void write(Context context)
        {
            send(context, writeMsg(context));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject writeMsg(Context context)
        {
            JsonObject message = new JsonObject();
            message.Add("@type", messageType(WRITE_SCHEMA));
            message.Add("@id", getNewId());
            addThread(message);
            message.Add("name", this.name);
            message.Add("version", this.ver);

            var arr = new JsonArray();
            foreach (var a in attrs)
                arr.Add(a);
            message.Add("attrNames", arr);

            return message;
        }

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] writeMsgPacked(Context context)
        {
            return packMsg(context, writeMsg(context));
        }
    }
}
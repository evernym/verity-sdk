using System;
using System.Json;

namespace VeritySDK
{
    /**
     * An interface for controlling a 0.6 WriteSchema protocol.
     */
    public abstract class WriteSchemaV0_6 : AbstractProtocol
    {
        public WriteSchemaV0_6() { }
        public WriteSchemaV0_6(string threadId) : base(threadId) { }

        /**
         * The qualifier for the message family. Uses Evernym's qualifier.
         */
        string QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
        /**
         * The name for the message family.
         */
        string FAMILY = "write-schema";
        /**
         * The version for the message family.
         */
        string VERSION = "0.6";


        /**
         * @see MessageFamily#qualifier()
         */
        public override string qualifier() { return QUALIFIER; }
        /**
         * @see MessageFamily#family()
         */
        public override string family() { return FAMILY; }
        /**
         * @see MessageFamily#version()
         */
        public override string version() { return VERSION; }

        /**
         Name for 'write' control message
         */
        public string WRITE_SCHEMA = "write";


        /**
         * Directs verity-application to write the specified Schema to the Ledger
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         */
        public abstract void write(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #write
         */
        public abstract JsonObject writeMsg(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         *
         * @see #write
         */
        public abstract byte[] writeMsgPacked(Context context);
    }
}
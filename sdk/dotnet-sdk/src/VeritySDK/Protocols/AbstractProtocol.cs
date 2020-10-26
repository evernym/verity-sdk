using System;
using System.Json;

namespace VeritySDK
{
    /**
     * The base class for all protocols
     */
    public abstract class AbstractProtocol : MessageFamily
    {
        /**
         * Constructs a Protocol with a given threadId. A threadId is NOT generated with this constructor.
         *
         * @param threadId given ID used for the thread. MUST not be null.
         */
        public AbstractProtocol(string threadId)
        {
            if (!String.IsNullOrWhiteSpace(threadId))
            {
                //DbcUtil.requireNotNull(threadId);

                this.threadId = threadId;
            }
            else
            {
                this.threadId = Guid.NewGuid().ToString();
            }
        }

        /**
         * Constructs a Protocol. The threadId is generated (randomly).
         */
        public AbstractProtocol() : this(Guid.NewGuid().ToString()) { }

        /**
         * The thread identifier
         * @return the threadId
         */
        public string getThreadId()
        {
            return threadId;
        }

        private string threadId;

        /**
         * Attaches the thread block (including the thid) for a protocol to the given message object (JSON)
         *
         * @param msg with the thread block attached
         */
        protected void addThread(JsonObject msg)
        {
            JsonObject threadBlock = new JsonObject();
            threadBlock.Add("thid", threadId);
            msg.Add("~thread", threadBlock);
        }

        /**
         * Encrypts and sends a specified message to Verity
         * @param context an instance of the Context object initialized to a verity-application agent
         * @param message the message to send to Verity
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws WalletException when there are issues with encryption and decryption
         * @throws UndefinedContextException when the context don't have enough information for this operation
         */
        protected void send(Context context, JsonObject message)
        {
            if (context == null)
            {
                throw new UndefinedContextException("Context cannot be NULL");
            }

            byte[] messageToSend = packMsg(context, message);

            Transport transport = new Transport();

            transport.sendMessage(context.VerityUrl(), messageToSend);
        }

        /**
         * Packs the connection message for the verity-application
         * @param context an instance of Context that has been initialized with your wallet and key details
         * @param message the message to be packed for the verity-application
         * @return Encrypted connection message ready to be sent to the verity
         * @throws WalletException when there are issues with encryption and decryption
         * @throws UndefinedContextException when the context don't have enough information for this operation
         */
        protected static byte[] packMsg(Context context, JsonObject message)
        {
            return Util.packMessageForVerity(context, message);
        }

        /**
         * Generates a new and unique id for a message.
         * @return new message id
         */
        public static string getNewId()
        {
            return Guid.NewGuid().ToString();
        }
    }
}

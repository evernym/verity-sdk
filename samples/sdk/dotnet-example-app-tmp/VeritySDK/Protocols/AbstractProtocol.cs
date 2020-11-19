using System;
using System.Json;
using VeritySDK.Exceptions;
using VeritySDK.Transports;
using VeritySDK.Utils;

namespace VeritySDK.Protocols
{
    /// <summary>
    /// The base class for all protocols
    /// </summary>
    public abstract class AbstractProtocol : MessageFamily
    {
        /// <summary>
        /// Constructs a Protocol with a given threadId. A threadId is NOT generated with this constructor. 
        /// </summary>
        /// <param name="threadId">given ID used for the thread. MUST not be null.</param>
        public AbstractProtocol(string threadId)
        {
            if (!String.IsNullOrWhiteSpace(threadId))
            {
                this.threadId = threadId;
            }
            else
            {
                this.threadId = Guid.NewGuid().ToString();
            }
        }

        /// <summary>
        /// Constructs a Protocol. The threadId is generated (randomly)
        /// </summary>
        public AbstractProtocol() : this(Guid.NewGuid().ToString()) { }


        private string threadId;

        /// <summary>
        /// The thread identifier 
        /// </summary>
        /// <returns>the threadId</returns>
        public string getThreadId()
        {
            return threadId;
        }

        /// <summary>
        /// Attaches the thread block (including the thid) for a protocol to the given message object (JSON) 
        /// </summary>
        /// <param name="msg">with the thread block attached</param>
        protected void addThread(JsonObject msg)
        {
            JsonObject threadBlock = new JsonObject();
            threadBlock.Add("thid", threadId);
            msg.Add("~thread", threadBlock);
        }

        /// <summary>
        /// Encrypts and sends a specified message to Verity 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="message">the message to send to Verity</param>
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

        /// <summary>
        /// Packs the connection message for the verity-application 
        /// </summary>
        /// <param name="context">an instance of Context that has been initialized with your wallet and key details</param>
        /// <param name="message">the message to be packed for the verity-application</param>
        /// <returns>Encrypted connection message ready to be sent to the verity</returns>
        public static byte[] packMsg(Context context, JsonObject message)
        {
            return Util.packMessageForVerity(context, message);
        }

        /// <summary>
        /// Generates a new and unique id for a message. 
        /// </summary>
        /// <returns>new message id</returns>
        public static string getNewId()
        {
            return Guid.NewGuid().ToString();
        }
    }
}

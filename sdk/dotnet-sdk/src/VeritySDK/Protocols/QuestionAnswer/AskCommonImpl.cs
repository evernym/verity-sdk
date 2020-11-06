using System;
using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// An abstract class for controlling common CommittedAnswer protocol.
    /// </summary>
    public abstract class AskCommonImpl : AbstractProtocol
    {
        string forRelationship;
        string questionText;
        string questionDetail;
        string[] validResponses;
        string response;
        private bool signatureRequired;

        public static string ASK_QUESTION = "ask-question";
        public static string ANSWER_QUESTION = "answer-question";
        public static string GET_STATUS = "get-status";

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="questionText">The main text of the question (included in the message the Connect.Me user signs with their private key)</param>
        /// <param name="questionDetail">Any additional information about the question</param>
        /// <param name="validResponses">The given possible responses.</param>
        public AskCommonImpl(string forRelationship,
                             string questionText,
                             string questionDetail,
                             string[] validResponses) : this(forRelationship, questionText, questionDetail, validResponses, true)
        {
        }

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="questionText">The main text of the question (included in the message the Connect.Me user signs with their private key)</param>
        /// <param name="questionDetail">Any additional information about the question</param>
        /// <param name="validResponses">The given possible responses.</param>
        /// <param name="signatureRequired">if a signature must be included with the answer</param>
        public AskCommonImpl(string forRelationship,
                             string questionText,
                             string questionDetail,
                             string[] validResponses,
                             bool signatureRequired)
        {
            this.forRelationship = forRelationship;
            this.questionText = questionText;
            this.questionDetail = questionDetail;
            this.validResponses = validResponses;
            this.signatureRequired = signatureRequired;
        }

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="threadId">the thread id of the already started protocol</param>
        /// <param name="answer">The given answer from the list of valid responses given in the question</param>
        public AskCommonImpl(string forRelationship, string threadId, string answer) : base(threadId)
        {
            this.forRelationship = forRelationship;
            this.response = answer;
        }

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="threadId">the thread id of the already started protocol</param>
        public AskCommonImpl(string forRelationship, string threadId) : base(threadId)
        {
            this.forRelationship = forRelationship;
        }

        /// <summary>
        /// Directs verity-application to ask the question 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void ask(Context context)
        {
            send(context, askMsg(context));
        }

        /// <summary>
        /// Directs verity-application to ask the question 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="ask(Context)"/>
        public JsonObject askMsg(Context context)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(ASK_QUESTION));
            msg.Add("@id", getNewId());
            addThread(msg);
            msg.Add("~for_relationship", this.forRelationship);
            msg.Add("text", this.questionText);
            msg.Add("detail", this.questionDetail);

            var resp = new JsonArray();
            foreach (var a in this.validResponses)
                resp.Add(a);
            msg.Add("valid_responses", resp);

            msg.Add("signature_required", this.signatureRequired);
            return msg;
        }

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="ask(Context)"/>
        public byte[] askMsgPacked(Context context)
        {
            return packMsg(context, askMsg(context));
        }

        /// <summary>
        /// Create and send the control message 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="answer(Context)"/>
        public void answer(Context context)
        {
            send(context, answerMsg(context));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="answer(Context)"/>
        public JsonObject answerMsg(Context context)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(ANSWER_QUESTION));
            msg.Add("@id", getNewId());
            addThread(msg);
            msg.Add("~for_relationship", this.forRelationship);
            msg.Add("response", this.response);
            return msg;
        }

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="answer(Context)"/>
        public byte[] answerMsgPacked(Context context)
        {
            return packMsg(context, answerMsg(context));
        }

        /// <summary>
        /// Ask for status from the verity-application agent 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void status(Context context)
        {
            send(context, statusMsg(context));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="status(Context)"/>
        public JsonObject statusMsg(Context context)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(GET_STATUS));
            msg.Add("@id", getNewId());
            addThread(msg);
            msg.Add("~for_relationship", this.forRelationship);
            return msg;
        }

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="status(Context)"/>
        public byte[] statusMsgPacked(Context context)
        {
            return packMsg(context, statusMsg(context));
        }
    }
}
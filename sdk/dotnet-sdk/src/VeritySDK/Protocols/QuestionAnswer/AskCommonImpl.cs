using System;
using System.Json;

namespace VeritySDK
{
    /*
     * NON_VISIBLE
     */
    public abstract class AskCommonImpl : AbstractProtocol
    {
        string forRelationship;
        string questionText;
        string questionDetail;
        string[] validResponses;
        string answer;
        private bool signatureRequired;

        public static string ASK_QUESTION = "ask-question";
        public static string ANSWER_QUESTION = "answer-question";
        public static string GET_STATUS = "get-status";


        public AskCommonImpl(string forRelationship,
                             string questionText,
                             string questionDetail,
                             string[] validResponses) : this(forRelationship, questionText, questionDetail, validResponses, true)
        {
        }

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

        public AskCommonImpl(string forRelationship, string threadId, string answer) : base(threadId)
        {
            this.forRelationship = forRelationship;
            this.answer = answer;
        }

        public AskCommonImpl(string forRelationship, string threadId) : base(threadId)
        {
            this.forRelationship = forRelationship;
        }

        public void ask(Context context)
        {
            send(context, askMsg(context));
        }

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

        public byte[] askMsgPacked(Context context)
        {
            return packMsg(context, askMsg(context));
        }


        public void Answer(Context context)
        {
            send(context, answerMsg(context));
        }

        public JsonObject answerMsg(Context context)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(ANSWER_QUESTION));
            msg.Add("@id", getNewId());
            addThread(msg);
            msg.Add("~for_relationship", this.forRelationship);
            msg.Add("response", this.answer);
            return msg;
        }

        public byte[] answerMsgPacked(Context context)
        {
            return packMsg(context, answerMsg(context));
        }

        public void status(Context context)
        {
            send(context, statusMsg(context));
        }

        public JsonObject statusMsg(Context context)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(GET_STATUS));
            msg.Add("@id", getNewId());
            addThread(msg);
            msg.Add("~for_relationship", this.forRelationship);
            return msg;
        }

        public byte[] statusMsgPacked(Context context)
        {
            return packMsg(context, statusMsg(context));
        }
    }
}
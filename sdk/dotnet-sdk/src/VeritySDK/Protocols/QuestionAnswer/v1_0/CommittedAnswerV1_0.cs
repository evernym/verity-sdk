using System;
using System.Json;

namespace VeritySDK
{
    /**
     * An interface for controlling a 1.0 CommittedAnswer protocol.
     */

    public class CommittedAnswerV1_0 : AskCommonImpl
    {
        public CommittedAnswerV1_0(string forRelationship,
                                   string questionText,
                                   string questionDetail,
                                   string[] validResponses,
                                   bool signatureRequired) : base(forRelationship, questionText, questionDetail, validResponses, signatureRequired) { }

        public CommittedAnswerV1_0(string forRelationship, string threadId, string answer) : base(forRelationship, threadId, answer) { }

        public CommittedAnswerV1_0(string forRelationship, string threadId) : base(forRelationship, threadId) { }

        /**
         * The qualifier for the message family. Uses Evernym's qualifier.
         */
        string QUALIFIER = Util.COMMUNITY_MSG_QUALIFIER;
        /**
         * The name for the message family.
         */
        string FAMILY = "committedanswer";
        /**
         * The version for the message family.
         */
        string VERSION = "1.0";


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


        ///**
        // * Directs verity-application to ask the question
        // *
        // * @param context an instance of the Context object initialized to a verity-application agent
        // * @throws IOException when the HTTP library fails to post to the agency endpoint
        // * @ when wallet operations fails or given invalid context
        // */
        //public abstract void ask(Context context);

        ///**
        // * Creates the control message without packaging and sending it.
        // *
        // * @param context an instance of the Context object initialized to a verity-application agent
        // * @return the constructed message (JSON object)
        // * @ when given invalid context
        // *
        // * @see #ask
        // */
        //public abstract JsonObject askMsg(Context context);

        ///**
        // * Creates and packages message without sending it.
        // *
        // * @param context an instance of the Context object initialized to a verity-application agent
        // * @return the byte array ready for transport
        // * @ when wallet operations fails or given invalid context
        // *
        // * @see #ask
        // */
        //public abstract byte[] askMsgPacked(Context context);

        ///**
        // * Directs verity-application to answer the question
        // *
        // * @param context an instance of the Context object initialized to a verity-application agent
        // * @throws IOException when the HTTP library fails to post to the agency endpoint
        // * @ when wallet operations fails or given invalid context
        // */
        //public abstract void answer(Context context);

        ///**
        // * Creates the control message without packaging and sending it.
        // *
        // * @param context an instance of the Context object initialized to a verity-application agent
        // * @return the constructed message (JSON object)
        // * @ when given invalid context
        // *
        // * @see #answer
        // */
        //public abstract JsonObject answerMsg(Context context);
        ///**
        // * Creates and packages message without sending it.
        // *
        // * @param context an instance of the Context object initialized to a verity-application agent
        // * @return the byte array ready for transport
        // * @ when wallet operations fails or given invalid context
        // *
        // * @see #answer
        // */
        //public abstract byte[] answerMsgPacked(Context context);

        ///**
        // * Ask for status from the verity-application agent
        // *
        // * @param context an instance of the Context object initialized to a verity-application agent
        // * @throws IOException when the HTTP library fails to post to the agency endpoint
        // * @ when wallet operations fails or given invalid context
        // */
        //public abstract void status(Context context);

        ///**
        // * Creates the control message without packaging and sending it.
        // *
        // * @param context an instance of the Context object initialized to a verity-application agent
        // * @return the constructed message (JSON object)
        // * @ when given invalid context
        // *
        // * @see #status
        // */
        //public abstract JsonObject statusMsg(Context context);

        ///**
        // * Creates and packages message without sending it.
        // *
        // * @param context an instance of the Context object initialized to a verity-application agent
        // * @return the byte array ready for transport
        // * @ when wallet operations fails or given invalid context
        // *
        // * @see #status
        // */
        //public abstract byte[] statusMsgPacked(Context context);
    }
}
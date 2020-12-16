using System;
using System.Json;
using VeritySDK.Protocols;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.QuestionAnswer
{
    /// <summary>
    /// A class for controlling a 1.0 FCommittedAnswer protocol.
    /// </summary>
    public class CommittedAnswerV1_0 : AskCommonImpl
    {
        #region Protocol identificator

        /// <summary>
        /// The qualifier for the message family. Uses Evernym's qualifier.
        /// </summary>
        public override string qualifier() { return Util.COMMUNITY_MSG_QUALIFIER; }

        /// <summary>
        /// The name for the message family.
        /// </summary>
        public override string family() { return "committedanswer"; }

        /// <summary>
        /// The version for the message family.
        /// </summary>
        public override string version() { return "1.0"; }

        #endregion 

        /// <summary>
        /// Constructor for the 1.0 CommittedAnswerV1_0 object
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="questionText">The main text of the question (included in the message the Connect.Me user signs with their private key)</param>
        /// <param name="questionDetail">Any additional information about the question</param>
        /// <param name="validResponses">The given possible responses.</param>
        /// <param name="signatureRequired">if a signature must be included with the answer</param>
        public CommittedAnswerV1_0(string forRelationship,
                                   string questionText,
                                   string questionDetail,
                                   string[] validResponses,
                                   bool signatureRequired) : base(forRelationship, questionText, questionDetail, validResponses, signatureRequired) { }

        /// <summary>
        /// Constructor for the 1.0 CommittedAnswerV1_0 object
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="threadId">the thread id of the already started protocol</param>
        /// <param name="answer">The given answer from the list of valid responses given in the question</param>
        public CommittedAnswerV1_0(string forRelationship, string threadId, string answer) : base(forRelationship, threadId, answer) { }

        /// <summary>
        /// Constructor for the 1.0 CommittedAnswerV1_0 object
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="threadId">the thread id of the already started protocol</param>
        public CommittedAnswerV1_0(string forRelationship, string threadId) : base(forRelationship, threadId) { }
    }
}
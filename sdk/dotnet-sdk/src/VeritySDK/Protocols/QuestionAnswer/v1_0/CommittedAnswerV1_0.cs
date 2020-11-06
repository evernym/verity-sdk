using System;
using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// An class for controlling a 1.0 CommittedAnswer protocol.
    /// </summary>
    public class CommittedAnswerV1_0 : AskCommonImpl
    {
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

        /// <summary>
        /// The qualifier for the message family. Uses Evernym's qualifier.
        /// </summary>
        string QUALIFIER = Util.COMMUNITY_MSG_QUALIFIER;

        /// <summary>
        /// The name for the message family.
        /// </summary>
        string FAMILY = "committedanswer";

        /// <summary>
        /// The version for the message family.
        /// </summary>
        string VERSION = "1.0";

        /// <see cref="MessageFamily.qualifier"/>
        public override string qualifier() { return QUALIFIER; }

        /// <see cref="MessageFamily.family"/>
        public override string family() { return FAMILY; }

        /// <see cref="MessageFamily.version"/>
        public override string version() { return VERSION; }
    }
}
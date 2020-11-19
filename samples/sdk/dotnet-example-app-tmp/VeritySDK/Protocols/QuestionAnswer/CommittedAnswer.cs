using System;
using System.Json;

namespace VeritySDK.Protocols.QuestionAnswer
{
    /// <summary>
    /// Factory for CommittedAnswer protocol objects.
    /// 
    /// The CommittedAnswer protocol allows one self-sovereign party ask another self-sovereign a question and get a answer.
    /// The answer can be signed (not required) and must be one of a specified list of responses
    /// </summary>
    public class CommittedAnswer
    {
        private CommittedAnswer() { }

        /// <summary>
        /// Constructor for the 1.0 CommittedAnswer object. This constructor creates an object that is ready to ask the given question 
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="questionText">The main text of the question (included in the message the Connect.Me user signs with their private key)</param>
        /// <param name="questionDetail">Any additional information about the question</param>
        /// <param name="validResponses">The given possible responses.</param>
        /// <param name="signatureRequired">if a signature must be included with the answer</param>
        /// <returns>1.0 CommittedAnswer object</returns>
        public static CommittedAnswerV1_0 v1_0(string forRelationship,
                                               string questionText,
                                               string questionDetail,
                                               string[] validResponses,
                                               bool signatureRequired)
        {
            return new CommittedAnswerV1_0(forRelationship, questionText, questionDetail, validResponses, signatureRequired);
        }

        /// <summary>
        /// Constructor for the 1.0 CommittedAnswer object. This constructor creates an object that is ready to answer the the asked question 
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="threadId">the thread id of the already started protocol</param>
        /// <param name="answer">The given answer from the list of valid responses given in the question</param>
        /// <returns>1.0 CommittedAnswer object</returns>
        public static CommittedAnswerV1_0 v1_0(string forRelationship,
                                               string threadId,
                                               string answer)
        {
            return new CommittedAnswerV1_0(forRelationship, threadId, answer);
        }

        /// <summary>
        /// Constructor for the 1.0 CommittedAnswer object. This constructor re-creates an object from a known relationship and
        /// threadId.This object can only check status of the protocol.
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="threadId">the thread id of the already started protocol</param>
        /// <returns>1.0 CommittedAnswer object</returns>
        public static CommittedAnswerV1_0 v1_0(string forRelationship,
                                               string threadId)
        {
            return new CommittedAnswerV1_0(forRelationship, threadId);
        }
    }
}
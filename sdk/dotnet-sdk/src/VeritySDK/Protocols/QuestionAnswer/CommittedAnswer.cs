using System;
using System.Json;

namespace VeritySDK
{
    /**
     * Factory for CommittedAnswer protocol objects.
     * <p/>
     *
     * The CommittedAnswer protocol allows one self-sovereign party ask another self-sovereign a question and get a answer.
     * The answer can be signed (not required) and must be one of a specified list of responses
     *
     */
    public class CommittedAnswer
    {
        private CommittedAnswer() { }
        /**
         * Constructor for the 1.0 CommittedAnswer object. This constructor creates an object that is ready to ask
         * the given question
         *
         * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
         * @param questionText The main text of the question (included in the message the Connect.Me user signs with their private key)
         * @param questionDetail Any additional information about the question
         * @param validResponses The given possible responses.
         * @param signatureRequired if a signature must be included with the answer
         * @return 1.0 CommittedAnswer object
         */
        public static CommittedAnswerV1_0 v1_0(string forRelationship,
                                               string questionText,
                                               string questionDetail,
                                               string[] validResponses,
                                               bool signatureRequired)
        {
            return new CommittedAnswerImplV1_0(forRelationship, questionText, questionDetail, validResponses, signatureRequired);
        }

        /**
         * Constructor for the 1.0 CommittedAnswer object. This constructor creates an object that is ready to answer the
         * the asked question
         *
         * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
         * @param threadId the thread id of the already started protocol
         * @param answer The given answer from the list of valid responses given in the question
         * @return 1.0 CommittedAnswer object
         */
        public static CommittedAnswerV1_0 v1_0(string forRelationship,
                                               string threadId,
                                               string answer)
        {
            return new CommittedAnswerImplV1_0(forRelationship, threadId, answer);
        }


        /**
         * Constructor for the 1.0 CommittedAnswer object. This constructor re-creates an object from a known relationship and
         * threadId. This object can only check status of the protocol.
         *
         * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
         * @param threadId the thread id of the already started protocol
         * @return 1.0 CommittedAnswer object
         */
        public static CommittedAnswerV1_0 v1_0(string forRelationship,
                                               string threadId)
        {
            return new CommittedAnswerImplV1_0(forRelationship, threadId);
        }
    }
}
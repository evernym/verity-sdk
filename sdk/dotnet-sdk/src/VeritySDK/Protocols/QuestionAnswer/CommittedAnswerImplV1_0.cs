using System;
using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// This is an implementation of CommittedAnswerImplV1_0 but is not viable to user of Verity SDK. Created using the static CommittedAnswer class 
    /// </summary>
    public class CommittedAnswerImplV1_0 : CommittedAnswerV1_0
    {
        public CommittedAnswerImplV1_0(string forRelationship,
                                   string questionText,
                                   string questionDetail,
                                   string[] validResponses,
                                   bool signatureRequired) : base(forRelationship, questionText, questionDetail, validResponses, signatureRequired) { }

        public CommittedAnswerImplV1_0(string forRelationship, string threadId, string answer) : base(forRelationship, threadId, answer) { }

        public CommittedAnswerImplV1_0(string forRelationship, string threadId) : base(forRelationship, threadId) { }
    }
}
using System;
using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// This is an implementation of QuestionAnswerImplV1_0 but is not viable to user of Verity SDK. Created using the static QuestionAnswer class
    /// </summary>
    public class QuestionAnswerImplV1_0 : QuestionAnswerV1_0
    {

        public QuestionAnswerImplV1_0(String forRelationship,
                                  String questionText,
                                  String questionDetail,
                                  String[] validResponses,
                                  Boolean signatureRequired) : base(forRelationship, questionText, questionDetail, validResponses, signatureRequired) { }

        public QuestionAnswerImplV1_0(String forRelationship,
                                      String threadId,
                                      String answer) : base(forRelationship, threadId, answer) { }

        public QuestionAnswerImplV1_0(String forRelationship,
                               String threadId) : base(forRelationship, threadId) { }

    }
}
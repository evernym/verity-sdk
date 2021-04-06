using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Json;
using VeritySDK.Protocols.QuestionAnswer;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class QuestionAnswerTest : TestBase
    {
        private string forRelationship = "abcd12345";
        private string questionText = "Question text";
        private string questionDetail = "Optional question detail";
        private string[] validResponses = { "Yes", "No" };
        private bool requireSignature = true;

        [TestMethod]
        public void testGetMessageType()
        {
            QuestionAnswerV1_0 questionAnswer = QuestionAnswer.v1_0(
                    forRelationship,
                    questionText,
                    questionDetail,
                    validResponses,
                    requireSignature);

            string msgName = "msg name";
            string msgType = Util.getMessageType(
                    Util.COMMUNITY_MSG_QUALIFIER,
                    questionAnswer.family(),
                    questionAnswer.version(),
                    msgName);
            Assert.AreEqual(msgType, questionAnswer.messageType(msgName));
        }

        [TestMethod]
        public void testConstructor()
        {
            Context context = TestHelpers.getContext();
            QuestionAnswerV1_0 questionAnswer = QuestionAnswer.v1_0(
                    forRelationship,
                    questionText,
                    questionDetail,
                    validResponses,
                    requireSignature
            );
            JsonObject msg = questionAnswer.askMsg(context);
            testAskMessages(msg);
        }

        private void testAskMessages(JsonObject msg)
        {

            Assert.IsNotNull(msg.getAsString("@id"));
            Assert.AreEqual(forRelationship, msg.getAsString("~for_relationship"));
            Assert.AreEqual(questionText, msg.getAsString("text"));
            Assert.AreEqual(questionDetail, msg.getAsString("detail"));
            Assert.AreEqual(validResponses[0], msg.getAsJsonArray("valid_responses")[0].ToString().Trim('"'));
            Assert.AreEqual(requireSignature, msg.getAsBoolean("signature_required"));
            Assert.IsNotNull(msg.getAsJsonArray("valid_responses"));
        }

        [TestMethod]
        public void testAsk()
        {
            withContext(context => {
                QuestionAnswerV1_0 questionAnswer = QuestionAnswer.v1_0(
                        forRelationship,
                        questionText,
                        questionDetail,
                        validResponses,
                        requireSignature);

                byte[] message = questionAnswer.askMsgPacked(context);
                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, message);
                Assert.AreEqual(
                        Util.COMMUNITY_MSG_QUALIFIER + "/questionanswer/1.0/ask-question",
                        unpackedMessage.getAsString("@type")
                );
            });
        }
    }
}

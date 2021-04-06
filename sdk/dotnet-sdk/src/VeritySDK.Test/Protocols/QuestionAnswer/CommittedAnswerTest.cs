using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Json;
using VeritySDK.Protocols.QuestionAnswer;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class CommittedAnswerTest : TestBase
    {
        private string forRelationship = "abcd12345";
        private string questionText = "Question text";
        private string questionDetail = "Optional question detail";
        private string[] validResponses = { "Yes", "No" };
        private bool requireSignature = true;

        [TestMethod]
        public void testGetMessageType()
        {
            CommittedAnswerV1_0 testProtocol = CommittedAnswer.v1_0(
                    forRelationship,
                    questionText,
                    questionDetail,
                    validResponses,
                    requireSignature);

            string msgName = "msg name";
            string msgType = Util.getMessageType(
                    Util.COMMUNITY_MSG_QUALIFIER,
                    testProtocol.family(),
                    testProtocol.version(),
                    msgName);
            Assert.AreEqual(msgType, testProtocol.messageType(msgName));
        }

        [TestMethod]
        public void testGetThreadId()
        {
            CommittedAnswerV1_0 testProtocol = CommittedAnswer.v1_0(
                    forRelationship,
                    questionText,
                    questionDetail,
                    validResponses,
                    requireSignature);
            Assert.IsNotNull(testProtocol.getThreadId());
        }

        [TestMethod]
        public void testConstructor()
        {
            Context context = TestHelpers.getContext();
            CommittedAnswerV1_0 testProtocol = CommittedAnswer.v1_0(
                    forRelationship,
                    questionText,
                    questionDetail,
                    validResponses,
                    requireSignature
            );
            JsonObject msg = testProtocol.askMsg(context);
            testAskMessages(msg);
        }

        private void testAskMessages(JsonObject msg)
        {
            testBaseMessage(msg);
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
                CommittedAnswerV1_0 testProtocol = CommittedAnswer.v1_0(
                        forRelationship,
                        questionText,
                        questionDetail,
                        validResponses,
                        requireSignature);

                byte[] message = testProtocol.askMsgPacked(context);
                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, message);
                testBaseMessage(unpackedMessage);
                Assert.AreEqual(
                        Util.COMMUNITY_MSG_QUALIFIER + "/committedanswer/1.0/ask-question",
                        unpackedMessage.getAsString("@type")
                );
            });
        }

        private void testBaseMessage(JsonObject msg)
        {
            Assert.IsNotNull(msg.getAsString("@type"));
            Assert.IsNotNull(msg.getAsString("@id"));
            Assert.IsNotNull(msg.getAsJsonObject("~thread").getAsString("thid"));
        }
    }
}

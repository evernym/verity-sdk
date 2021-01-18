using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Json;
using VeritySDK.Protocols.BasicMessage;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class BasicMessageTest : TestBase
    {
        private string forRelationship = "abcd12345";
        private string content = "Hello, World!";
        private string sentTime = "2018-12-13T17:29:34+0000";
        private string localization = "en";

        [TestMethod]
        public void testGetMessageType()
        {
            BasicMessageV1_0 testProtocol = BasicMessage.v1_0(
                    forRelationship,
                    content,
                    sentTime,
                    localization);

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
            BasicMessageV1_0 testProtocol = BasicMessage.v1_0(
                    forRelationship,
                    content,
                    sentTime,
                    localization);
            Assert.IsNotNull(testProtocol.getThreadId());
        }

        [TestMethod]
        public void testConstructor()
        {
            Context context = TestHelpers.getContext();
            BasicMessageV1_0 testProtocol = BasicMessage.v1_0(
                    forRelationship,
                    content,
                    sentTime,
                    localization
            );
            JsonObject msg = testProtocol.messageMsg(context);
            testSendMessages(msg);
        }

        private void testSendMessages(JsonObject msg)
        {
            testBaseMessage(msg);
            Assert.AreEqual(forRelationship, msg.getAsString("~for_relationship"));
            Assert.AreEqual(content, msg.getAsString("content"));
            Assert.AreEqual(sentTime, msg.getAsString("sent_time"));
            Assert.AreEqual(localization, msg.getAsJsonObject("~l10n").getAsString("locale"));
        }

        [TestMethod]
        public void testSend()
        {
            withContext(context => {
                BasicMessageV1_0 testProtocol = BasicMessage.v1_0(
                    forRelationship,
                    content,
                    sentTime,
                    localization);

                byte[] message = testProtocol.messageMsgPacked(context);
                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, message);
                testBaseMessage(unpackedMessage);
                Assert.AreEqual(
                        "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/basicmessage/1.0/send-message",
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

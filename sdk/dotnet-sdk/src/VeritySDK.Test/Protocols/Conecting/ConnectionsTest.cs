using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Json;
using VeritySDK.Protocols.Conecting;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class ConnectionsTest : TestBase
    {
        string label = "Alice";
        string base64Url = "<TBD>";

        [TestMethod]
        public void testGetMessageType()
        {
            ConnectionsV1_0 connecting = Connecting.v1_0(label, base64Url);
            string msgName = "msg name";
            Assert.AreEqual(
                Util.getMessageType(
                        Util.COMMUNITY_MSG_QUALIFIER,
                        "connections",
                        "1.0",
                        msgName
                ),
                connecting.messageType(msgName)
            );
        }

        [TestMethod]
        public void testGetThreadId()
        {
            ConnectionsV1_0 connecting = Connecting.v1_0(label, base64Url);
            Assert.IsNotNull(connecting.getThreadId());
        }

        [TestMethod]
        public void testAcceptInvitation()
        {
            withContext(context =>
            {
                ConnectionsV1_0 testProtocol = Connecting.v1_0(label, base64Url);
                byte[] msg = testProtocol.acceptMsgPacked(context);
                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, msg);
                testAcceptInviteMessage(unpackedMessage);
            });
        }

        [TestMethod]
        public void testStatus()
        {
            withContext(context =>
            {
                ConnectionsV1_0 testProtocol = Connecting.v1_0(label, base64Url);
                byte[] msg = testProtocol.statusMsgPacked(context);
                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, msg);
                testStatusInviteMessage(unpackedMessage);
            });
        }

        private void testAcceptInviteMessage(JsonObject msg)
        {
            testBaseMessage(msg);
            Assert.AreEqual("\"did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/accept\"", msg["@type"].ToString());
        }

        private void testStatusInviteMessage(JsonObject msg)
        {
            testBaseMessage(msg);
            Assert.AreEqual("\"did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/status\"", msg["@type"].ToString());
        }

        private void testBaseMessage(JsonObject msg)
        {
            Assert.IsNotNull(msg["@type"].ToString());
            Assert.IsNotNull(msg["@id"].ToString());
            Assert.IsNotNull((msg["~thread"] as JsonObject)["thid"].ToString());
        }

    }
}

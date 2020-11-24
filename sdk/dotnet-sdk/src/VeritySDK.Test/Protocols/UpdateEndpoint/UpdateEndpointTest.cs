using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;
using System.Json;
using System.Linq;
using VeritySDK.Protocols.UpdateEndpoint;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class UpdateEndpointTest : TestBase
    {

        [TestMethod]
        public void testGetMessageType()
        {
            UpdateEndpointV0_6 testProtocol = UpdateEndpoint.v0_6();
            string msgName = "msg name";
            Assert.AreEqual(
                    Util.getMessageType(Util.EVERNYM_MSG_QUALIFIER, testProtocol.family(), testProtocol.version(), msgName),
                    testProtocol.messageType(msgName)
            );
        }

        [TestMethod]
        public void testGetThreadId()
        {
            UpdateEndpointV0_6 testProtocol = UpdateEndpoint.v0_6();
            Assert.IsNotNull(testProtocol.getThreadId());
        }

        [TestMethod]
        public void testUpdateMsg()
        {
            withContext(context =>
            {
                UpdateEndpointV0_6 testProtocol = UpdateEndpoint.v0_6();
                JsonObject msg = testProtocol.updateMsg(context);

                Assert.AreEqual(
                        msg.getAsJsonObject("comMethod").getAsString("value"),
                        context.EndpointUrl()
                );
                Assert.AreEqual(
                        "did:sov:123456789abcdefghi1234;spec/configs/0.6/UPDATE_COM_METHOD",
                        msg.getAsString("@type")
                );
                Assert.IsNotNull(msg.getAsString("@id"));
                Assert.AreEqual("webhook", msg.getAsJsonObject("comMethod").getAsString("id"));
                Assert.AreEqual(2, msg.getAsJsonObject("comMethod").getAsInteger("type"));
                Assert.AreEqual(
                        "1.0",
                        msg.getAsJsonObject("comMethod").getAsJsonObject("packaging").getAsString("pkgType")
                );
                List<string> expectedReceipientKeys = new List<string>();
                expectedReceipientKeys.Add(context.SdkVerKey());

                var rcp = msg.getAsJsonObject("comMethod").getAsJsonObject("packaging").getAsJsonArray("recipientKeys").Select(s => s.ToString().Trim('"')).ToList();
                Assert.IsTrue(expectedReceipientKeys.EquivalentTo(rcp));
            });
        }

        [TestMethod]
        public void testUpdate()
        {
            withContext(context =>
            {
                UpdateEndpointV0_6 testProtocol = UpdateEndpoint.v0_6();
                byte[] message = testProtocol.updateMsgPacked(context);
                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, message);
                Assert.AreEqual(
                        "did:sov:123456789abcdefghi1234;spec/configs/0.6/UPDATE_COM_METHOD",
                        unpackedMessage.getAsString("@type")
                );
            });
        }
    }
}

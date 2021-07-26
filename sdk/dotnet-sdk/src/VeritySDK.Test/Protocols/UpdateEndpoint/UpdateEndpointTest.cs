using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;
using System.Json;
using System.Linq;
using System.Security.Policy;
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
                        Util.EVERNYM_MSG_QUALIFIER + "/configs/0.6/UPDATE_COM_METHOD",
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
        public void testUpdateMsgWithOAuth2V1Authentication()
        {
            withContext(context =>
            {
                Url tokenUrl = new Url("https://auth.url/token");
                Dictionary<string, string> authData = new Dictionary<string, string>();
                authData.Add("grant_type", "client_credentials");
                authData.Add("client_id", "ajeyKDizsDkwYeEmRmHU78gf7W3VIEoA");
                authData.Add("client_secret", "aaGxxcGi6kb6AxIe");

                UpdateEndpointV0_6 testProtocol = UpdateEndpoint.v0_6(
                    AuthenticationFactory.CreateOAuth2V1(tokenUrl, authData)
                );
                JsonObject msg = testProtocol.updateMsg(context);

                Assert.AreEqual(
                        msg.getAsJsonObject("comMethod").getAsString("value"),
                        context.EndpointUrl()
                );
                Assert.AreEqual(
                        Util.EVERNYM_MSG_QUALIFIER + "/configs/0.6/UPDATE_COM_METHOD",
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
                
                // authentication
                Assert.AreEqual(
                        "OAuth2",
                        msg.getAsJsonObject("comMethod").getAsJsonObject("authentication").getAsString("type")
                );
                Assert.AreEqual(
                        "v1",
                        msg.getAsJsonObject("comMethod").getAsJsonObject("authentication").getAsString("version")
                );
                Assert.AreEqual(
                        tokenUrl.ToString(),
                        msg.getAsJsonObject("comMethod").getAsJsonObject("authentication").getAsJsonObject("data").getAsString("url")
                );
                Assert.AreEqual(
                        "client_credentials",
                        msg.getAsJsonObject("comMethod").getAsJsonObject("authentication").getAsJsonObject("data").getAsString("grant_type")
                );
                Assert.AreEqual(
                        "ajeyKDizsDkwYeEmRmHU78gf7W3VIEoA",
                        msg.getAsJsonObject("comMethod").getAsJsonObject("authentication").getAsJsonObject("data").getAsString("client_id")
                );
                Assert.AreEqual(
                        "aaGxxcGi6kb6AxIe",
                        msg.getAsJsonObject("comMethod").getAsJsonObject("authentication").getAsJsonObject("data").getAsString("client_secret")
                );
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
                        Util.EVERNYM_MSG_QUALIFIER + "/configs/0.6/UPDATE_COM_METHOD",
                        unpackedMessage.getAsString("@type")
                );
            });
        }
    }
}

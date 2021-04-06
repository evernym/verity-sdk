using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Json;
using VeritySDK.Protocols.OutOfBand;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class OutOfBandTest
    {

        [TestMethod]
        public void testGetMessageType()
        {
            OutOfBandV1_0 outofbandProvisioning =
                    OutOfBand.v1_0("testForRelationship", "testInviteUrl");
            string msgName = "msg name";
            Assert.AreEqual(
                    Util.getMessageType(
                            Util.COMMUNITY_MSG_QUALIFIER,
                            "out-of-band",
                            "1.0",
                            msgName
                    ),
                    Util.getMessageType(outofbandProvisioning, msgName)
            );
        }

        [TestMethod]
        public void testGetThreadId()
        {
            OutOfBandV1_0 testProtocol = OutOfBand.v1_0("testForRelationship", "testInviteUrl");
            Assert.IsNotNull(testProtocol.getThreadId());
        }

        [TestMethod]
        public void testHandshakeReuseMsg()
        {
            OutOfBandV1_0 outofband = OutOfBand.v1_0("testForRelationship", "testInviteUrl");
            JsonObject msg = outofband.handshakeReuseMsg(TestHelpers.getContext());
            testHandshakeReuseMsg(msg);
        }

        private void testHandshakeReuseMsg(JsonObject msg)
        {
            testBaseMessage(msg);
            Assert.AreEqual(Util.COMMUNITY_MSG_QUALIFIER + "/out-of-band/1.0/reuse", msg.getAsString("@type"));
            Assert.AreEqual("testForRelationship", msg.getAsString("~for_relationship"));
            Assert.AreEqual("testInviteUrl", msg.getAsString("inviteUrl"));
        }

        private void testBaseMessage(JsonObject msg)
        {
            Assert.IsNotNull(msg.getAsString("@type"));
            Assert.IsNotNull(msg.getAsString("@id"));
            Assert.IsNotNull(msg.getAsJsonObject("~thread").getAsString("thid"));
        }
    }
}

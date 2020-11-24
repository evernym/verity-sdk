using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;
using System.Json;
using System.Linq;
using VeritySDK.Protocols.UpdateConfigs;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class UpdateConfigsTest : TestBase
    {
        private string name = "Name1";
        private string logoUrl = "http://logo.url";

        [TestMethod]
        public void testGetMessageType()
        {
            UpdateConfigsV0_6 testProtocol = UpdateConfigs.v0_6(name, logoUrl);
            string msgName = "msg name";
            Assert.AreEqual(Util.getMessageType(
                    Util.EVERNYM_MSG_QUALIFIER,
                    testProtocol.family(),
                    testProtocol.version(),
                    msgName
            ), testProtocol.messageType(msgName));
        }

        [TestMethod]
        public void testGetThreadId()
        {
            UpdateConfigsV0_6 testProtocol = UpdateConfigs.v0_6(name, logoUrl);
            Assert.IsNotNull(testProtocol.getThreadId());
        }

        [TestMethod]
        public void testConstructorWithAttr()
        {
            Context context = TestHelpers.getContext();
            UpdateConfigsV0_6 testProtocol = UpdateConfigs.v0_6(name, logoUrl);

            JsonObject msg = testProtocol.updateMsg(context);
            testUpdateMsgMessages(msg);

            JsonObject msg2 = testProtocol.statusMsg(context);
            testStatusMsg(msg2);
        }

        [TestMethod]
        public void testConstructorWithoutAttr()
        {
            Context context = TestHelpers.getContext();
            UpdateConfigsV0_6 testProtocol = UpdateConfigs.v0_6();

            JsonObject msg2 = testProtocol.statusMsg(context);
            testStatusMsg(msg2);
        }

        private void testUpdateMsgMessages(JsonObject requestMsg)
        {
            Dictionary<string, string> expectedConfigs = new Dictionary<string, string>();
            expectedConfigs.Add("name", this.name);
            expectedConfigs.Add("logoUrl", this.logoUrl);

            Assert.AreEqual(
                    "did:sov:123456789abcdefghi1234;spec/update-configs/0.6/update",
                    requestMsg.getAsString("@type")
            );
            Assert.IsNotNull(requestMsg.getAsString("@id"));
            Assert.IsTrue(parseConfigs(requestMsg.getAsJsonArray("configs")).EquivalentTo(expectedConfigs));
        }

        private void testStatusMsg(JsonObject statusMsg)
        {
            Assert.AreEqual(
                    "did:sov:123456789abcdefghi1234;spec/update-configs/0.6/get-status",
                    statusMsg.getAsString("@type"));
            Assert.IsNotNull(statusMsg.getAsString("@id"));
        }

        [TestMethod]
        public void testUpdate()
        {
            withContext(context =>
            {
                UpdateConfigsV0_6 testProtocol = UpdateConfigs.v0_6(name, logoUrl);
                byte[] message = testProtocol.updateMsgPacked(context);
                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, message);
                testUpdateMsgMessages(unpackedMessage);
            });
        }

        [TestMethod]
        public void testGetStatus()
        {
            withContext(context =>
            {
                UpdateConfigsV0_6 testProtocol = UpdateConfigs.v0_6();
                byte[] message = testProtocol.statusMsgPacked(context);
                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, message);
                testStatusMsg(unpackedMessage);
            });
        }

        private Dictionary<string, string> parseConfigs(JsonArray configsJson)
        {
            Dictionary<string, string> configs = new Dictionary<string, string>();
            for (int i = 0; i < configsJson.Count; i++)
            {
                JsonObject c = configsJson.ElementAt(i) as JsonObject;
                configs.Add(c.getAsString("name"), c.getAsString("value"));
            }

            return configs;
        }
    }
}

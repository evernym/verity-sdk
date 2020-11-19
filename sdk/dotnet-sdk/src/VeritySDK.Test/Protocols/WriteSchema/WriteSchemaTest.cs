using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;
using System.Json;
using System.Linq;
using VeritySDK.Protocols.WriteSchema;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class WriteSchemaTest : TestBase
    {

        private string schemaName = "test schema";
        private string schemaVersion = "0.0.1";
        private string attr1 = "name";
        private string attr2 = "degree";

        [TestMethod]
        public void testGetMessageType()
        {
            WriteSchemaV0_6 testProtocol = WriteSchema.v0_6(schemaName, schemaVersion, attr1);
            string msgName = "msg name";
            Assert.AreEqual(
                    Util.getMessageType(Util.EVERNYM_MSG_QUALIFIER, testProtocol.family(), testProtocol.version(), msgName),
                    testProtocol.messageType(msgName));
        }

        [TestMethod]
        public void testGetThreadId()
        {
            WriteSchemaV0_6 testProtocol = WriteSchema.v0_6(schemaName, schemaVersion, attr1);
            Assert.IsNotNull(testProtocol.getThreadId());
        }

        [TestMethod]
        public void testConstructor()
        {
            Context context = TestHelpers.getContext();
            WriteSchemaV0_6 writeSchema = WriteSchema.v0_6(schemaName, schemaVersion, attr1, attr2);
            JsonObject msg = writeSchema.writeMsg(context);

            Assert.AreEqual(schemaName, msg.getAsString("name"));
            Assert.AreEqual(schemaVersion, msg.getAsString("version"));

            var attrs = (new List<string>() { attr1, attr2 });
            var msg_attrs = msg.getAsJsonArray("attrNames").Select(s => s.ToString().Trim('"')).ToList();

            Assert.IsTrue(attrs.EquivalentTo(msg_attrs));
        }

        [TestMethod]
        public void testWrite()
        {
            withContext(context =>
            {
                WriteSchemaV0_6 testProtocol = WriteSchema.v0_6(schemaName, schemaVersion, attr1, attr2);
                byte[] message = testProtocol.writeMsgPacked(context);
                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, message);
                testBaseMessage(unpackedMessage);
                Assert.AreEqual(
                        "did:sov:123456789abcdefghi1234;spec/write-schema/0.6/write",
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

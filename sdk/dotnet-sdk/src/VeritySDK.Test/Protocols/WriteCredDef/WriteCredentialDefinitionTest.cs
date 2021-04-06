using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Json;
using VeritySDK.Protocols.WriteCredDef;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class WriteCredentialDefinitionTest : TestBase
    {
        private string name = "cred def name";
        private string schemaId = "...someSchemaId...";
        private string tag = "latest";
        private RevocationRegistryConfig revocationDetails = WriteCredentialDefinitionV0_6.disabledRegistryConfig();

        [TestMethod]
        public void testGetMessageType()
        {
            WriteCredentialDefinitionV0_6 writeCredDef = WriteCredentialDefinition.v0_6(name, schemaId, tag, revocationDetails);
            string msgName = "msg name";
            Assert.AreEqual(
                    Util.getMessageType(Util.EVERNYM_MSG_QUALIFIER, writeCredDef.family(), writeCredDef.version(), msgName),
                    writeCredDef.messageType(msgName)
            );
        }

        [TestMethod]
        public void testGetThreadId()
        {
            WriteCredentialDefinitionV0_6 testProtocol = WriteCredentialDefinition.v0_6(name, schemaId, tag, revocationDetails);
            Assert.IsNotNull(testProtocol.getThreadId());
        }

        [TestMethod]
        public void testConstructorWithNameAndSchemaId()
        {
            Context context = TestHelpers.getContext();
            WriteCredentialDefinitionV0_6 testProtocol = WriteCredentialDefinition.v0_6(name, schemaId);
            JsonObject msg = testProtocol.writeMsg(context);
            Assert.AreEqual(name, msg.getAsString("name"));
            Assert.AreEqual(schemaId, msg.getAsString("schemaId"));
            Assert.IsNull(msg.GetValue("tag"));
            Assert.IsNull(msg.GetValue("revocationDetails"));
        }

        [TestMethod]
        public void testConstructorWithNameAndSchemaIdAndTag()
        {
            Context context = TestHelpers.getContext();
            WriteCredentialDefinitionV0_6 testProtocol = WriteCredentialDefinition.v0_6(name, schemaId, tag);
            JsonObject msg = testProtocol.writeMsg(context);
            Assert.AreEqual(name, msg.getAsString("name"));
            Assert.AreEqual(schemaId, msg.getAsString("schemaId"));
            Assert.AreEqual(tag, msg.getAsString("tag"));
            Assert.IsNull(msg.GetValue("revocationDetails"));
        }

        [TestMethod]
        public void testConstructorWithNameAndSchemaIdAndRevDetails()
        {
            Context context = TestHelpers.getContext();
            WriteCredentialDefinitionV0_6 testProtocol = WriteCredentialDefinition.v0_6(name, schemaId, revocationDetails);
            JsonObject msg = testProtocol.writeMsg(context);
            Assert.AreEqual(name, msg.getAsString("name"));
            Assert.AreEqual(schemaId, msg.getAsString("schemaId"));
            Assert.IsNull(msg.GetValue("tag"));
            Assert.IsNotNull(msg.getAsJsonObject("revocationDetails"));
        }


        [TestMethod]
        public void testFullConstructor()
        {
            Context context = TestHelpers.getContext();
            WriteCredentialDefinitionV0_6 testProtocol = WriteCredentialDefinition.v0_6(name, schemaId, tag, revocationDetails);
            JsonObject msg = testProtocol.writeMsg(context);
            Assert.AreEqual(name, msg.getAsString("name"));
            Assert.AreEqual(schemaId, msg.getAsString("schemaId"));
            Assert.AreEqual(tag, msg.getAsString("tag"));
            Assert.IsNotNull(msg.getAsJsonObject("revocationDetails"));
        }

        [TestMethod]
        public void testWrite()
        {
            withContext(context =>
            {
                WriteCredentialDefinitionV0_6 testProtocol = WriteCredentialDefinition.v0_6(name, schemaId, tag, revocationDetails);
                byte[] message = testProtocol.writeMsgPacked(context);
                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, message);
                testBaseMessage(unpackedMessage);
                Assert.AreEqual(Util.EVERNYM_MSG_QUALIFIER + "/write-cred-def/0.6/write", unpackedMessage.getAsString("@type"));
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

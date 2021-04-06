using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Json;
using VeritySDK.Protocols.IssuerSetup;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class IssuerSetupTest : TestBase
    {

        [TestMethod]
        public void testGetMessageType()
        {
            string msgName = "msg name";

            IssuerSetupV0_6 t = IssuerSetup.v0_6();

            string expectedType = Util.getMessageType(
                    Util.EVERNYM_MSG_QUALIFIER,
                    t.family(),
                    t.version(),
                    msgName);

            Assert.AreEqual(expectedType, t.messageType(msgName));
        }

        [TestMethod]
        public void testGetThreadId()
        {
            IssuerSetupV0_6 testProtocol = IssuerSetup.v0_6();
            Assert.IsNotNull(testProtocol.getThreadId());
        }

        [TestMethod]
        public void testCreateMessages()
        {
            Context context = TestHelpers.getContext();
            IssuerSetupV0_6 p = IssuerSetup.v0_6();
            JsonObject msg = p.createMsg(context);
            Assert.AreEqual(
                    Util.EVERNYM_MSG_QUALIFIER + "/issuer-setup/0.6/create",
                    msg.getAsString("@type")
            );
            Assert.IsNotNull(msg.getAsString("@id"));
        }

        [TestMethod]
        public void testCreate()
        {
            withContext(context =>
            {
                IssuerSetupV0_6 testProtocol = IssuerSetup.v0_6();
                byte[] message = testProtocol.createMsgPacked(context);
                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, message);
                Assert.AreEqual(
                        Util.EVERNYM_MSG_QUALIFIER + "/issuer-setup/0.6/create",
                        unpackedMessage.getAsString("@type"));
            });
        }
    }
}

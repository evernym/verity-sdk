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

            IssuerSetupV0_7 t = IssuerSetup.v0_7();

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
            IssuerSetupV0_7 testProtocol = IssuerSetup.v0_7();
            Assert.IsNotNull(testProtocol.getThreadId());
        }

        [TestMethod]
        public void testCreateMessages()
        {
            Context context = TestHelpers.getContext();
            IssuerSetupV0_7 p = IssuerSetup.v0_7();
            JsonObject msg = p.createMsg(context, "did:indy:sovrin:builder");
            Assert.AreEqual(
                    Util.EVERNYM_MSG_QUALIFIER + "/issuer-setup/0.7/create",
                    msg.getAsString("@type")
            );
            Assert.AreEqual(
                msg.getAsString("ledgerPrefix"),
                "did:indy:sovrin:builder"
            );
            Assert.IsNotNull(msg.getAsString("@id"));
        }

        public void testCreateMessagesWithEndorser()
        {
            Context context = TestHelpers.getContext();
            IssuerSetupV0_7 p = IssuerSetup.v0_7();
            JsonObject msg = p.createMsg(context, "did:indy:sovrin:builder", "someEndorser");
            Assert.AreEqual(
                    Util.EVERNYM_MSG_QUALIFIER + "/issuer-setup/0.7/create",
                    msg.getAsString("@type")
            );
            Assert.AreEqual(
                msg.getAsString("ledgerPrefix"),
                "did:indy:sovrin:builder"
            );
            Assert.AreEqual(
                 msg.getAsString("endorser"),
                 "someEndorser"
             );
            Assert.IsNotNull(msg.getAsString("@id"));
        }

        [TestMethod]
        public void testCreate()
        {
            withContext(context =>
            {
                IssuerSetupV0_7 testProtocol = IssuerSetup.v0_7();
                byte[] message = testProtocol.createMsgPacked(context, "did:indy:sovrin:builder");
                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, message);
                Assert.AreEqual(
                        Util.EVERNYM_MSG_QUALIFIER + "/issuer-setup/0.7/create",
                        unpackedMessage.getAsString("@type"));
            });
        }
    }
}

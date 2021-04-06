using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Json;
using VeritySDK.Transports;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class UtilTest
    {

        [TestMethod]
        public void testPackMessageForVerityAndUnpackForward()
        {
            Context context = null;
            try
            {
                context = TestHelpers.getContext();

                JsonObject testMessage = new JsonObject();
                testMessage.Add("hello", "world");
                byte[] packedMessage = Util.packMessageForVerity(context, testMessage);

                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, packedMessage);
                Assert.AreEqual(testMessage.ToString(), unpackedMessage.ToString());

                context.CloseWallet();
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
            finally
            {
                TestHelpers.cleanup(context);
            }
        }

        [TestMethod]
        public void testGetMessageTypeComplete()
        {
            String msgType = Util.EVERNYM_MSG_QUALIFIER + "/credential/0.1/status";
            Assert.AreEqual(msgType, Util.getMessageType(Util.EVERNYM_MSG_QUALIFIER, "credential", "0.1", "status"));
        }

        [TestMethod]
        public void noop()
        {
            JsonObject.Parse("{}");
        }
    }
}

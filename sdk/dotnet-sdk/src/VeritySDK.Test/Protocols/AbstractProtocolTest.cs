using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Json;
using VeritySDK.Protocols;
using VeritySDK.Transports;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class AbstractProtocolTest
    {
        [TestMethod]
        public void testGetMessage()
        {
            JsonObject message = JsonObject.Parse("{\"hello\": \"world\"}") as JsonObject;

            Context context = TestHelpers.getContext();
            byte[] packedMessage = AbstractProtocol.packMsg(context, message);

            JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, packedMessage);

            Assert.AreEqual(unpackedMessage["hello"].ToString(), "\"world\"");

            TestHelpers.cleanup(context);
        }

    }
}

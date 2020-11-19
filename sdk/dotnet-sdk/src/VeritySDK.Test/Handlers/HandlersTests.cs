using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Json;
using System.Text;
using System.Threading;
using VeritySDK.Handler;
using VeritySDK.Protocols;
using VeritySDK.Protocols.IssuerSetup;
using VeritySDK.Transports;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class HandlersTests : TestBase
    {
        private bool defHandlerFired = false;

        Handlers handlers = null;

        public HandlersTests()
        {
            ConfigureHandler();
        }

        private void ConfigureHandler()
        {
            handlers = new Handlers();

            handlers.addDefaultHandler(message =>
            {
                var msgType = message.getAsString("@type");
                defHandlerFired = msgType.StartsWith("did:sov:123456789abcdefghi1234;spec");
            });
        }

        /// <summary>
        /// Wait handle process
        /// </summary>
        /// <param name="flag">Done flag</param>
        /// <param name="timeout">Timeout in sec</param>
        private void Wait(ref bool flag1, int timeout = 5)
        {
            var tick_cnt = 0;
            while (true)
            {
                if (flag1 || tick_cnt > timeout) return;

                try { Thread.Sleep(1000); } catch { };

                tick_cnt += 1;
            }
        }

        [TestMethod]
        public void DefaultHandlerIsOne()
        {
            withContext(context =>
            {
                defHandlerFired = false;

                var json = @"{""@id"": ""bd2338f4-c830-4c8a-934e-fc80763ccade"", ""@type"": ""did:sov:123456789abcdefghi1234;spec/testing""}";
                var mess = JsonObject.Parse(json) as JsonObject;
                var packed_mess = Util.packMessageForVerity(context, mess);
                handlers.handleMessage(context, packed_mess);

                Wait(ref defHandlerFired);

                Assert.IsTrue(defHandlerFired);
            });
        }
    }
}

using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Json;
using VeritySDK.Exceptions;
using VeritySDK.Protocols.Provision;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class ProvisionTestV0_7
    {

        [TestMethod]
        public void createMessage()
        {
            Context context = TestHelpers.getContext();

            ProvisionV0_7 p = Provision.v0_7();
            JsonObject msg = p.provisionMsg(context);
            Assert.AreEqual(msg.getAsString("requesterVk"), context.SdkVerKey());


            string goodToken = "{\"sponseeId\": \"myId\", \"sponsorId\": \"evernym-test-sponsorabc123\", \"nonce\": \"123\", \"timestamp\": \"2020-06-05T21:33:36.085Z\", \"sig\": \"ZkejifRr3txh7NrKokC5l2D2YcABUlGlAoFHZD9RapHHBfVtNnHgYux1RCAiEh4Q31VJE3C92T1ZnqDm1WlEAA==\", \"sponsorVerKey\": \"GJ1SzoWzavQYfNL9XkaJdrQejfztN4XqdsiV4ct3LXKL\"}";
            ProvisionV0_7 p2 = Provision.v0_7(goodToken);

            JsonObject msg2 = p2.provisionMsg(context);
            Assert.IsTrue(msg2.ContainsKey("provisionToken"));
        }

        internal class ProvisionV0_7_Test : ProvisionV0_7
        {
            protected override JsonObject sendToVerity(Context context, byte[] packedMessage)
            {
                JsonObject returnMessage = new JsonObject();
                returnMessage.Add("selfDID", "4ut8tgCBdUMCYZrJh5JS4o");
                returnMessage.Add("agentVerKey", "38fBEUX2sUQF7ZsY6epz1cbCvfBfDbQghUZUzFP6JChE");

                return returnMessage;
            }
        }

        ProvisionV0_7_Test p = new ProvisionV0_7_Test();

        [TestMethod]
        public void processMessage()
        {
            Context context = TestHelpers.getContext();
            Context context2 = p.provision(context);

            Assert.AreEqual("4ut8tgCBdUMCYZrJh5JS4o", context2.DomainDID());
            Assert.AreEqual("38fBEUX2sUQF7ZsY6epz1cbCvfBfDbQghUZUzFP6JChE", context2.VerityAgentVerKey());
        }

        [TestMethod]
        public void validateToken()
        {
            string testToken = "{\"sponseeId\": \"myId\", \"sponsorId\": \"evernym-test-sponsorabc123\", \"nonce\": \"123\", \"timestamp\": \"2020-06-05T21:33:36.085Z\", \"sig\": \"ZkejifRr3txh7NrKokC5l2D2YcABUlGlAoFHZD9RapHHBfVtNnHgYux1RCAiEh4Q31VJE3C92T1ZnqDm1WlEAA==\", \"sponsorVerKey\": \"GJ1SzoWzavQYfNL9XkaJdrQejfztN4XqdsiV4ct3LXKL\"}";
            new ProvisionV0_7().validateToken(testToken);

            try
            {
                string testToken2 = "{\"sponseeId\": \"myId\", \"sponsorId\": \"evernym-test-sponsorabc123\", \"nonce\": \"123\", \"timestamp\": \"2020-06-05T21:33:36.085Z\", \"sig\": \"AkejifRr3txh7NrKokC5l2D2YcABUlGlAoFHZD9RapHHBfVtNnHgYux1RCAiEh4Q31VJE3C92T1ZnqDm1WlEAA==\", \"sponsorVerKey\": \"GJ1SzoWzavQYfNL9XkaJdrQejfztN4XqdsiV4ct3LXKL\"}";
                new ProvisionV0_7().validateToken(testToken2);
                Assert.Fail();
            }
            catch (VerityException ignored) {
                var s = ignored.Message; // Fix build warning
            }
        }

        [TestMethod]
        public void testGetThreadId()
        {
            ProvisionV0_7 testProtocol = Provision.v0_7();
            Assert.IsNotNull(testProtocol.getThreadId());
        }
    }
}

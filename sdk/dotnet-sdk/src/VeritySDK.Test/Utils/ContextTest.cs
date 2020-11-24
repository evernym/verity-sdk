using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Json;
using System.Linq;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class ContextTest
    {
        private JsonObject withNewKeys(JsonObject jsonObject)
        {
            JsonObject config = new JsonObject();
            foreach (var itm in jsonObject)
            {
                config.Add(getNewKey(itm.Key), itm.Value);
            }
            return config;
        }

        private string getNewKey(string key)
        {
            switch (key)
            {
                case "verityPublicVerkey": return "verityPublicVerKey";
                case "verityPairwiseDID": return "domainDID";
                case "verityPairwiseVerkey": return "verityAgentVerKey";
                case "sdkPairwiseVerkey": return "sdkVerKey";
                case "sdkPairwiseDID": return "sdkVerKeyId";
                default: return key;
            }
        }

        [TestMethod]
        public void toJson_0_1()
        {
            var env = new Env();

            Context context;
            using (TestWallet testWallet = new TestWallet(env.walletName, env.walletKey))
            {
                try
                {
                    JsonObject config = new JsonObject();
                    config.Add("walletName", env.walletName);
                    config.Add("walletKey", env.walletKey);
                    config.Add("verityUrl", env.verityUrl);
                    config.Add("verityPublicDID", testWallet.getVerityPublicDID());
                    config.Add("verityPublicVerkey", testWallet.getVerityPublicVerkey());
                    config.Add("verityPairwiseDID", testWallet.getVerityPairwiseDID());
                    config.Add("verityPairwiseVerkey", testWallet.getVerityPairwiseVerkey());
                    config.Add("sdkPairwiseDID", testWallet.getSdkPairwiseVerkey());
                    config.Add("sdkPairwiseVerkey", testWallet.getSdkPairwiseVerkey());
                    config.Add("endpointUrl", env.endpointUrl);

                    context = ContextBuilder.fromJson(config).build();

                    config.Add("version", "0.2");

                    TestHelpers.assertEqualsJSONObject(withNewKeys(config), context.toJson());

                    context.CloseWallet();
                }
                catch (Exception e)
                {
                    Assert.Fail(e.Message);
                }
            }
        }

        [TestMethod]
        public void toJson_0_2()
        {
            var env = new Env();

            Context context;
            using (TestWallet testWallet = new TestWallet(env.walletName, env.walletKey))
            {
                try
                {
                    JsonObject config = new JsonObject();
                    config.Add("walletName", env.walletName);
                    config.Add("walletKey", env.walletKey);
                    config.Add("verityUrl", env.verityUrl);
                    config.Add("verityPublicDID", testWallet.getVerityPublicDID());
                    config.Add("verityPublicVerKey", testWallet.getVerityPublicVerkey());
                    config.Add("domainDID", testWallet.getVerityPairwiseDID());
                    config.Add("verityAgentVerKey", testWallet.getVerityPairwiseVerkey());
                    config.Add("sdkVerKeyId", testWallet.getSdkPairwiseVerkey());
                    config.Add("sdkVerKey", testWallet.getSdkPairwiseVerkey());
                    config.Add("endpointUrl", env.endpointUrl);
                    config.Add("version", "0.2");
                    context = ContextBuilder.fromJson(config).build();
                    TestHelpers.assertEqualsJSONObject(withNewKeys(config), context.toJson());

                    context.CloseWallet();
                }
                catch (Exception e)
                {
                    Assert.Fail(e.Message);
                }
            }
        }

        [TestMethod]
        public void restApiToken()
        {
            Context c = TestHelpers.getContext("000000000000000000000000Team1VAS");
            string t = c.RestApiToken();
            string e = "HZ3Ak6pj9ryFASKbA9fpwqjVh42F35UDiCLQ13J58Xoh" +                    
                       ":4Wf6JtGy9enwwXVKcUgADPq7Pnf9T2YZ8LupMEVxcQQf98uuRYxWGHLAwXWp8DtaEYHo4cUeExDjApMfvLJQ48Kp";
            Assert.AreEqual(e, t);
        }
    }
}

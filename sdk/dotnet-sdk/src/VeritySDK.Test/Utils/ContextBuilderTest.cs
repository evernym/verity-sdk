using Hyperledger.Indy.WalletApi;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Json;
using VeritySDK.Exceptions;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class ContextBuilderTest
    {
        [TestMethod]
        public void simpleBuild()
        {
            var env = new Env();

            using (TestWallet testWallet = new TestWallet(env.walletName, env.walletKey))
            {
                Context c = ContextBuilder
                        .blank()
                        .walletConfig(testWallet)
                        .sdkVerKey(env.verkey1)
                        .build();

                c.CloseWallet();

                Assert.AreEqual(env.verkey1, c.SdkVerKey());
            }
        }

        [TestMethod]
        public void multiBuild()
        {
            var env = new Env();

            using (TestWallet testWallet = new TestWallet(env.walletName, env.walletKey))
            {
                Context c = ContextBuilder
                        .blank()
                        .walletConfig(testWallet)
                        .build();

                Wallet test = c.WalletHandle();

                Context c2 = c.ToContextBuilder()
                        .verityUrl("http://example.com")
                        .build();

                Assert.IsTrue(c.WalletHandle() == c2.WalletHandle()); // Same wallet handle
                c2.CloseWallet();
            }
        }

        [TestMethod]
        public void multiClosedBuild()
        {
            var env = new Env();

            using (TestWallet testWallet = new TestWallet(env.walletName, env.walletKey))
            {
                Context c = ContextBuilder
                        .blank()
                        .walletConfig(testWallet)
                        .build();

                Wallet test = c.WalletHandle();
                c.CloseWallet();

                Context c2 = c.ToContextBuilder()
                        .verityUrl("http://example.com")
                        .build();
                Assert.IsTrue(test != c2.WalletHandle()); // New wallet handle
                c2.CloseWallet();
            }
        }

        [TestMethod]
        public void fromScratch()
        {
            var env = new Env();

            using (TestWallet testWallet = new TestWallet(env.walletName, env.walletKey))
            {
                Did testDid = new Did("CV65RFpeCtPu82hNF9i61G", "7G3LhXFKXKTMv7XGx1Qc9wqkMbwcU2iLBHL8x1JXWWC2");

                Context c = ContextBuilder.scratchContext(testWallet, "http://wwww.example.com", testDid, null);

                c.SdkVerKeyId();
                c.SdkVerKey();

                try
                {
                    c.DomainDID();
                    Assert.Fail("Should throw UndefinedContextException");
                }
                catch (UndefinedContextException ignored) {
                    var s = ignored.Message; // Fix build warning
                }

                try
                {
                    c.VerityAgentVerKey();
                    Assert.Fail("Should throw UndefinedContextException");
                }
                catch (UndefinedContextException ignored) {
                    var s = ignored.Message; // Fix build warning
                }

                Assert.AreEqual("7G3LhXFKXKTMv7XGx1Qc9wqkMbwcU2iLBHL8x1JXWWC2", c.VerityPublicVerKey());

                c.CloseWallet();
            }
        }

        [TestMethod]
        public void shouldCorrectlyParseConfig_0_1()
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

                    var cfg_s = "{\"id\":\"" + env.walletName + "\"}";
                    var cfg = context.WalletConfig().config().Replace(" ", "");
                    Assert.AreEqual(cfg_s, cfg);

                    var cr_s = "{\"key\":\"" + env.walletKey + "\"}";
                    var cr = context.WalletConfig().credential().Replace(" ", "");
                    Assert.AreEqual(cr_s, cr);

                    Assert.AreEqual(env.verityUrl, context.VerityUrl());
                    Assert.AreEqual(env.endpointUrl, context.EndpointUrl());
                    Assert.AreEqual(ContextConstants.V_0_2, context.Version());
                    Assert.AreEqual(testWallet.getVerityPublicVerkey(), context.VerityPublicVerKey());
                    Assert.AreEqual(testWallet.getVerityPairwiseVerkey(), context.VerityAgentVerKey());
                    Assert.AreEqual(testWallet.getVerityPairwiseDID(), context.DomainDID());
                    Assert.AreEqual(testWallet.getSdkPairwiseVerkey(), context.SdkVerKey());
                    Assert.IsNotNull(context.WalletHandle());

                    context.CloseWallet();
                }
                catch (Exception e)
                {
                    Assert.Fail(e.Message);
                }
            }
        }

        [TestMethod]
        public void shouldCorrectlyParseConfig_0_2()
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
                    config.Add("endpointUrl", env.endpointUrl);
                    config.Add("verityUrl", env.verityUrl);
                    config.Add("verityPublicDID", testWallet.getVerityPublicDID());
                    config.Add("verityPublicVerKey", testWallet.getVerityPublicVerkey());
                    config.Add("domainDID", testWallet.getVerityPairwiseDID());
                    config.Add("verityAgentVerKey", testWallet.getVerityPairwiseVerkey());
                    config.Add("sdkVerKeyId", testWallet.getSdkPairwiseVerkey());
                    config.Add("sdkVerKey", testWallet.getSdkPairwiseVerkey());
                    config.Add("version", ContextConstants.V_0_2);
                    context = ContextBuilder.fromJson(config).build();

                    var cfg_s = "{\"id\":\"" + env.walletName + "\"}";
                    var cfg = context.WalletConfig().config().Replace(" ", "");
                    Assert.AreEqual(cfg_s, cfg);

                    var cr_s = "{\"key\":\"" + env.walletKey + "\"}";
                    var cr = context.WalletConfig().credential().Replace(" ", "");
                    Assert.AreEqual(cr_s, cr);

                    Assert.AreEqual(env.verityUrl, context.VerityUrl());
                    Assert.AreEqual(env.endpointUrl, context.EndpointUrl());
                    Assert.AreEqual(ContextConstants.V_0_2, context.Version());
                    Assert.AreEqual(testWallet.getVerityPublicVerkey(), context.VerityPublicVerKey());
                    Assert.AreEqual(testWallet.getVerityPairwiseVerkey(), context.VerityAgentVerKey());
                    Assert.AreEqual(testWallet.getVerityPairwiseDID(), context.DomainDID());
                    Assert.AreEqual(testWallet.getSdkPairwiseVerkey(), context.SdkVerKey());
                    Assert.IsNotNull(context.WalletHandle());

                    context.CloseWallet();
                }
                catch (Exception e)
                {
                    Assert.Fail(e.Message);
                }
            }
        }
    }
}

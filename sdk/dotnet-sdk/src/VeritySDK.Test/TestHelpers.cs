using Com.Evernym.Vdrtools.WalletApi;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;
using System.Json;
using System.Text;
using VeritySDK.Utils;
using VeritySDK.Wallets;

namespace VeritySDK.Test
{
    public class TestHelpers
    {
        protected TestHelpers()
        {
        }

        public static Context getContext()
        {
            return getContext(null);
        }

        public static Context getContext(string seed)
        {
            var env = new Env();

            TestWallet testWallet = new TestWallet(env.walletName, env.walletKey, seed);
            return ContextBuilder
                    .blank()
                    .walletConfig(testWallet)
                    .verityUrl(env.verityUrl)
                    .verityPublicDID(testWallet.getVerityPublicVerkey())
                    .verityPublicVerKey(testWallet.getVerityPublicVerkey())
                    .domainDID(testWallet.getVerityPairwiseDID())
                    .verityAgentVerKey(testWallet.getVerityPairwiseVerkey())
                    .sdkVerKeyId(testWallet.getSdkPairwiseDID())
                    .sdkVerKey(testWallet.getSdkPairwiseVerkey())
                    .endpointUrl(env.endpointUrl)
                    .build();
        }


        public static void assertEqualsJSONObject(JsonObject expected, JsonObject actual)
        {
            Assert.AreEqual(expected.Count, actual.Count);

            foreach (var exp in expected)
            {
                var act_v = actual[exp.Key].ToString();
                var exp_v = exp.Value.ToString();

                Assert.AreEqual(act_v, exp_v);
            }
        }

        public static void cleanup(Context context)
        {
            if (context != null)
            {
                if (!context.WalletIsClosed())
                {
                    context.CloseWallet();
                }
                WalletConfig config = context.WalletConfig();
                Wallet.DeleteWalletAsync(config.config(), config.credential()).GetAwaiter().GetResult();
            }
        }

        public static JsonObject unpackForwardMessage(Context context, byte[] message)
        {
            JsonObject unpackedOnceMessage = Util.unpackMessage(context, message);
            byte[] unpackedOnceMessageMessage = Encoding.UTF8.GetBytes(unpackedOnceMessage["@msg"].ToString());
            return Util.unpackMessage(context, unpackedOnceMessageMessage);
        }

    }
}

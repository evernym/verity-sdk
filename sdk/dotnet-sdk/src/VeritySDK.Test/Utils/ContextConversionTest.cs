using Microsoft.VisualStudio.TestTools.UnitTesting;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class ContextConversionTest
    {
        [TestMethod]
        public void V01toV02Test()
        {
            var env = new Env();

            using (TestWallet ignored = new TestWallet(env.walletName, env.walletKey))
            {
                string v01Str = "{\n" +
                        "  \"verityPublicVerkey\": \"ETLgZKeQEKxBW7gXA6FBn7nBwYhXFoogZLCCn5EeRSQV\",\n" +
                        "  \"verityPairwiseDID\": \"NTvSuSXzygyxWrF3scrhdc\",\n" +
                        "  \"verityUrl\": \"https://vas-team1.pdev.evernym.com\",\n" +
                        "  \"verityPairwiseVerkey\": \"ChXRWjQdrrLyksbPQZfaS3JekA4xLgD5Jg7GzXhc9zqE\",\n" +
                        "  \"walletName\": \"" + env.walletName + "\",\n" +
                        "  \"walletKey\": \"" + env.walletKey + "\",\n" +
                        "  \"sdkPairwiseVerkey\": \"HZ3Ak6pj9ryFASKbA9fpwqjVh42F35UDiCLQ13J58Xoh\",\n" +
                        "  \"verityPublicDID\": \"Rgj7LVEonrMzcRC1rhkx76\",\n" +
                        "  \"sdkPairwiseDID\": \"XNRkA8tboikwHD3x1Yh7Uz\"\n" +
                        "}";
                Context ctx = ContextBuilder.fromJson(v01Str).build();
                ctx.CloseWallet();

                Assert.AreEqual("NTvSuSXzygyxWrF3scrhdc", ctx.DomainDID());
                Assert.AreEqual("ChXRWjQdrrLyksbPQZfaS3JekA4xLgD5Jg7GzXhc9zqE", ctx.VerityAgentVerKey());
                Assert.AreEqual("HZ3Ak6pj9ryFASKbA9fpwqjVh42F35UDiCLQ13J58Xoh", ctx.SdkVerKey());
                Assert.AreEqual("XNRkA8tboikwHD3x1Yh7Uz", ctx.SdkVerKeyId());
                Assert.AreEqual("0.2", ctx.Version());
            }
        }
    }
}

package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ContextConversionTest {
    @Test
    public void V01toV02Test() throws UndefinedContextException, WalletException {
        String walletName = UUID.randomUUID().toString();
        String walletKey = UUID.randomUUID().toString();
        try(TestWallet ignored = new TestWallet(walletName, walletKey)) {
            String v01Str = "{\n" +
                    "  \"verityPublicVerkey\": \"ETLgZKeQEKxBW7gXA6FBn7nBwYhXFoogZLCCn5EeRSQV\",\n" +
                    "  \"verityPairwiseDID\": \"NTvSuSXzygyxWrF3scrhdc\",\n" +
                    "  \"verityUrl\": \"https://vas-team1.pdev.evernym.com\",\n" +
                    "  \"verityPairwiseVerkey\": \"ChXRWjQdrrLyksbPQZfaS3JekA4xLgD5Jg7GzXhc9zqE\",\n" +
                    "  \"walletName\": \""+walletName+"\",\n" +
                    "  \"walletKey\": \""+walletKey+"\",\n" +
                    "  \"sdkPairwiseVerkey\": \"HZ3Ak6pj9ryFASKbA9fpwqjVh42F35UDiCLQ13J58Xoh\",\n" +
                    "  \"verityPublicDID\": \"Rgj7LVEonrMzcRC1rhkx76\",\n" +
                    "  \"sdkPairwiseDID\": \"XNRkA8tboikwHD3x1Yh7Uz\"\n" +
                    "}";
            Context ctx = ContextBuilder.fromJson(v01Str).build();
            ctx.closeWallet();

            assertEquals("NTvSuSXzygyxWrF3scrhdc", ctx.domainDID());
            assertEquals("ChXRWjQdrrLyksbPQZfaS3JekA4xLgD5Jg7GzXhc9zqE", ctx.verityAgentVerKey());
            assertEquals("HZ3Ak6pj9ryFASKbA9fpwqjVh42F35UDiCLQ13J58Xoh", ctx.sdkVerKey());
            assertEquals("XNRkA8tboikwHD3x1Yh7Uz", ctx.sdkVerKeyId());
            assertEquals("0.2", ctx.version());

//            System.out.println(ctx.toJson().toString(2));

        }
    }

}

package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.TestWallet;
import org.json.JSONObject;
import org.junit.Test;

import java.util.UUID;

import static com.evernym.verity.sdk.utils.ContextConstants.V_0_5;
import static org.junit.Assert.*;

public class ContextTest {

    // TODO: Add more robust tests with bad configs.

    @Test
    public void toJson_0_5() {
        String walletName = UUID.randomUUID().toString();
        String walletKey = UUID.randomUUID().toString();
        String endpointUrl = "http://localhost:4000";
        String verityUrl = "http://localhost:3000";
        Context context;
        try(TestWallet testWallet = new TestWallet(walletName, walletKey)) {
            JSONObject config = new JSONObject();
            config.put("walletName", walletName);
            config.put("walletKey", walletKey);
            config.put("verityUrl", verityUrl);
            config.put("verityPublicDID", testWallet.getVerityPublicDID());
            config.put("verityPublicVerkey", testWallet.getVerityPublicVerkey());
            config.put("verityPairwiseDID", testWallet.getVerityPairwiseDID());
            config.put("verityPairwiseVerkey", testWallet.getVerityPairwiseVerkey());
            config.put("sdkPairwiseDID", testWallet.getSdkPairwiseVerkey());
            config.put("sdkPairwiseVerkey", testWallet.getSdkPairwiseVerkey());
            config.put("endpointUrl", endpointUrl);
            context = ContextBuilder.fromJson(config).build();
            assertEquals(context.toJson(), config);

            context.closeWallet();
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
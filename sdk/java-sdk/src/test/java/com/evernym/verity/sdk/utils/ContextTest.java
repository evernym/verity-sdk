package com.evernym.verity.sdk.utils;

public class ContextTest {

//    @Test
//    public void shouldCorrectlyParseConfig() throws Exception {
//        String walletName = UUID.randomUUID().toString();
//        String walletKey = "12345";
//        String endpointUrl = "http://localhost:4000";
//        String verityUrl = "http://localhost:3000";
//        Context context = null;
//        try(TestWallet testWallet = new TestWallet(walletName, walletKey)) {
//            JSONObject config = new JSONObject();
//            config.put("walletName", walletName);
//            config.put("walletKey", walletKey);
//            config.put("verityUrl", verityUrl);
//            config.put("verityPublicDID", testWallet.getVerityPublicDID());
//            config.put("verityPublicVerkey", testWallet.getVerityPublicVerkey());
//            config.put("verityPairwiseDID", testWallet.getVerityPairwiseDID());
//            config.put("verityPairwiseVerkey", testWallet.getVerityPairwiseVerkey());
//            config.put("sdkPairwiseDID", testWallet.getSdkPairwiseVerkey());
//            config.put("sdkPairwiseVerkey", testWallet.getSdkPairwiseVerkey());
//            config.put("endpointUrl", endpointUrl);
//            context = new Context(config.toString());
//            assertEquals(walletName, context.walletName());
//            assertEquals(walletKey, context.walletKey());
//            assertEquals(verityUrl, context.verityUrl());
//            assertEquals(testWallet.getVerityPublicVerkey(), context.verityPublicVerkey());
//            assertEquals(testWallet.getVerityPairwiseVerkey(), context.verityPairwiseVerkey());
//            assertEquals(testWallet.getVerityPairwiseDID(), context.verityPairwiseDID());
//            assertEquals(testWallet.getSdkPairwiseVerkey(), context.sdkPairwiseVerkey());
//            assertEquals(endpointUrl, context.endpointUrl());
//            assertNotNull(context.walletHandle());
//
//            context.closeWallet();
//        } catch(Exception e) {
//            e.printStackTrace();
//            fail();
//        }
//    }

    // TODO: Add more robust tests with bad configs.
}
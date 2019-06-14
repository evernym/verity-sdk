package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.TestWallet;

import org.hyperledger.indy.sdk.crypto.Crypto;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class ContextTest {

    @Test
    public void shouldCorrectlyParseConfig() throws Exception {
        String walletName = "java_test_wallet";
        String walletKey = "12345";
        String webhookUrl = "http://localhost:4000";
        String verityUrl = "http://localhost:3000";
        try {
            TestWallet testWallet = new TestWallet(walletName, walletKey);
            JSONObject config = new JSONObject();
            config.put("walletName", walletName);
            config.put("walletKey", walletKey);
            config.put("verityUrl", verityUrl);
            config.put("verityPublicVerkey", testWallet.getVerityPublicVerkey());
            config.put("verityPairwiseDID", testWallet.getVerityPairwiseDID());
            config.put("verityPairwiseVerkey", testWallet.getVerityPairwiseVerkey());
            config.put("sdkPairwiseVerkey", testWallet.getSdkPairwiseVerkey());
            config.put("webhookUrl", webhookUrl);
            Context context = new Context(config.toString());
            assertEquals(walletName, context.walletName);
            assertEquals(walletKey, context.walletKey);
            assertEquals(verityUrl, context.getVerityUrl());
            assertEquals(testWallet.getVerityPublicVerkey(), context.getVerityPublicVerkey());
            assertEquals(testWallet.getVerityPairwiseVerkey(), context.getVerityPairwiseVerkey());
            assertEquals(testWallet.getVerityPairwiseDID(), context.getVerityPairwiseDID());
            assertEquals(testWallet.getSdkPairwiseVerkey(), context.getSdkPairwiseVerkey());
            assertEquals(webhookUrl, context.webhookUrl);
            assertNotNull(context.getWalletHandle());

            context.closeWallet();
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            String walletConfig = new JSONObject().put("id", walletName).toString();
            String walletCredentials = new JSONObject().put("key", walletKey).toString();
            Wallet.deleteWallet(walletConfig, walletCredentials).get();
        }
    }

    @Test
    public void shouldGenerateCorrectUpdateWebhookMessage() throws Exception {
        String walletName = "java_test_wallet";
        String walletKey = "12345";
        String webhookUrl = "http://localhost:4000";
        String verityUrl = "http://localhost:3000";
        try {
            TestWallet testWallet = new TestWallet(walletName, walletKey);
            JSONObject config = new JSONObject();
            config.put("walletName", walletName);
            config.put("walletKey", walletKey);
            config.put("verityUrl", verityUrl);
            config.put("verityPublicVerkey", testWallet.getVerityPublicVerkey());
            config.put("verityPairwiseDID", testWallet.getVerityPairwiseDID());
            config.put("verityPairwiseVerkey", testWallet.getVerityPairwiseVerkey());
            config.put("sdkPairwiseVerkey", testWallet.getSdkPairwiseVerkey());
            config.put("webhookUrl", webhookUrl);
            Context context = new Context(config.toString());
            byte[] updateWebhookMessage = context.getUpdateWebhookMessage();
            byte[] partiallyUnpackedMessageJWE = Crypto.unpackMessage(context.getWalletHandle(), updateWebhookMessage).get();
            String partiallyUnpackedMessage = new JSONObject(new String(partiallyUnpackedMessageJWE)).getString("message");
            JSONObject unpackedMessage = MessagePackaging.unpackForwardMsg(context, new JSONObject(partiallyUnpackedMessage).getJSONObject("@msg"));
            assertEquals("did:sov:123456789abcdefghi1234;spec/configs/0.6/UPDATE_COM_METHOD", unpackedMessage.getString("@type"));
            assertEquals("webhook", unpackedMessage.getJSONObject("comMethod").getString("id"));
            assertEquals(2, unpackedMessage.getJSONObject("comMethod").getInt("type"));
            assertEquals(webhookUrl, unpackedMessage.getJSONObject("comMethod").getString("value"));
            context.closeWallet();
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            String walletConfig = new JSONObject().put("id", walletName).toString();
            String walletCredentials = new JSONObject().put("key", walletKey).toString();
            Wallet.deleteWallet(walletConfig, walletCredentials).get();
        }
    }

    // TODO: Add more robust tests with bad configs.
}
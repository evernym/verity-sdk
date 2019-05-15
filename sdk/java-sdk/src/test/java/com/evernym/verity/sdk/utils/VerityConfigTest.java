package com.evernym.verity.sdk.utils;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.hyperledger.indy.sdk.wallet.*;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.crypto.Crypto;
import org.hyperledger.indy.sdk.did.*;

import org.junit.Test;
import org.json.JSONObject;

public class VerityConfigTest {

    public class TestWallet {
        String agencyPublicVerkey;
        String agencyPairwiseVerkey;
        String sdkPairwiseVerkey;

        public TestWallet(String walletName, String walletKey) throws InterruptedException, ExecutionException, IndyException {
            String walletConfig = new JSONObject().put("id", walletName).toString();
            String walletCredentials = new JSONObject().put("key", walletKey).toString();
            Wallet.createWallet(walletConfig, walletCredentials).get();
            Wallet walletHandle = Wallet.openWallet(walletConfig, walletCredentials).get();
            
            DidResults.CreateAndStoreMyDidResult theirResult = Did.createAndStoreMyDid(walletHandle, "{}").get();
            this.agencyPublicVerkey = theirResult.getVerkey();
            DidResults.CreateAndStoreMyDidResult theirPairwiseResult = Did.createAndStoreMyDid(walletHandle, "{}").get();
            this.agencyPairwiseVerkey = theirPairwiseResult.getVerkey();
            DidResults.CreateAndStoreMyDidResult myPairwiseResult = Did.createAndStoreMyDid(walletHandle, "{}").get();
            this.sdkPairwiseVerkey = myPairwiseResult.getVerkey();

            walletHandle.closeWallet().get();
        }

        String getAgencyPublicVerkey() {
            return agencyPublicVerkey;
        }

        String getAgencyPairwiseVerkey() {
            return agencyPairwiseVerkey;
        }

        String getSdkPairwiseVerkey() {
            return sdkPairwiseVerkey;
        }
    }

    @Test
    public void shouldCorrectlyParseConfig() throws Exception {
        String walletName = "java_test_wallet";
        String walletKey = "12345";
        String webhookUrl = "http://localhost:4000";
        String agencyUrl = "http://localhost:3000";
        try {
            TestWallet testWallet = new TestWallet(walletName, walletKey);
            JSONObject config = new JSONObject();
            config.put("walletName", walletName);
            config.put("walletKey", walletKey);
            config.put("agencyUrl", agencyUrl);
            config.put("agencyPublicVerkey", testWallet.getAgencyPublicVerkey());
            config.put("agencyPairwiseVerkey", testWallet.getAgencyPairwiseVerkey());
            config.put("sdkPairwiseVerkey", testWallet.getSdkPairwiseVerkey());
            config.put("webhookUrl", webhookUrl);
            VerityConfig verityConfig = new VerityConfig(config.toString());
            assertEquals(walletName, verityConfig.walletName);
            assertEquals(walletKey, verityConfig.walletKey);
            assertEquals(agencyUrl, verityConfig.getAgencyUrl());
            assertEquals(testWallet.getAgencyPublicVerkey(), verityConfig.getAgencyPublicVerkey());
            assertEquals(testWallet.getAgencyPairwiseVerkey(), verityConfig.getAgencyPairwiseVerkey());
            assertEquals(testWallet.getSdkPairwiseVerkey(), verityConfig.getSdkPairwiseVerkey());
            assertEquals(webhookUrl, verityConfig.webhookUrl);
            assertNotNull(verityConfig.getWalletHandle());

            verityConfig.closeWallet();
        } catch(Exception e) {
            e.printStackTrace();
            assertTrue(false);
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
        String agencyUrl = "http://localhost:3000";
        try {
            TestWallet testWallet = new TestWallet(walletName, walletKey);
            JSONObject config = new JSONObject();
            config.put("walletName", walletName);
            config.put("walletKey", walletKey);
            config.put("agencyUrl", agencyUrl);
            config.put("agencyPublicVerkey", testWallet.getAgencyPublicVerkey());
            config.put("agencyPairwiseVerkey", testWallet.getAgencyPairwiseVerkey());
            config.put("sdkPairwiseVerkey", testWallet.getSdkPairwiseVerkey());
            config.put("webhookUrl", webhookUrl);
            VerityConfig verityConfig = new VerityConfig(config.toString());
            byte[] updateWebhookMessage = verityConfig.getUpdateWebhookMessage();
            byte[] partiallyUnpackedMessageJWE = Crypto.unpackMessage(verityConfig.getWalletHandle(), updateWebhookMessage).get();
            String partiallyUnpackedMessage = new JSONObject(new String(partiallyUnpackedMessageJWE)).getString("message");
            String unpackedMessageString = MessagePackaging.unpackMessageFromAgency(verityConfig, partiallyUnpackedMessage.getBytes());
            JSONObject unpackedMessage = new JSONObject(unpackedMessageString);
            assertEquals("vs.service/common/0.1/update_com_method", unpackedMessage.getString("@type"));
            assertEquals("webhook", unpackedMessage.getJSONObject("comMethod").getString("id"));
            assertEquals("webhook", unpackedMessage.getJSONObject("comMethod").getString("type"));
            assertEquals(webhookUrl, unpackedMessage.getJSONObject("comMethod").getString("value"));
            verityConfig.closeWallet();
        } catch(Exception e) {
            e.printStackTrace();
            assertTrue(false);
        } finally {
            String walletConfig = new JSONObject().put("id", walletName).toString();
            String walletCredentials = new JSONObject().put("key", walletKey).toString();
            Wallet.deleteWallet(walletConfig, walletCredentials).get();
        }
    }

    // TODO: Add more robust tests with bad configs.
}
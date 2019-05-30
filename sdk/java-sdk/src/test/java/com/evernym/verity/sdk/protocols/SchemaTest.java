package com.evernym.verity.sdk.protocols;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

import com.evernym.verity.sdk.utils.MessagePackaging;
import com.evernym.verity.sdk.utils.VerityConfig;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.crypto.Crypto;
import org.hyperledger.indy.sdk.did.*;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;
import org.junit.Test;

public class SchemaTest {
    public class TestWallet {
        String verityPublicVerkey;
        String verityPairwiseVerkey;
        String sdkPairwiseVerkey;

        public TestWallet(String walletName, String walletKey) throws InterruptedException, ExecutionException, IndyException {
            String walletConfig = new JSONObject().put("id", walletName).toString();
            String walletCredentials = new JSONObject().put("key", walletKey).toString();
            Wallet.createWallet(walletConfig, walletCredentials).get();
            Wallet walletHandle = Wallet.openWallet(walletConfig, walletCredentials).get();
            
            DidResults.CreateAndStoreMyDidResult theirResult = Did.createAndStoreMyDid(walletHandle, "{}").get();
            this.verityPublicVerkey = theirResult.getVerkey();
            DidResults.CreateAndStoreMyDidResult theirPairwiseResult = Did.createAndStoreMyDid(walletHandle, "{}").get();
            this.verityPairwiseVerkey = theirPairwiseResult.getVerkey();
            DidResults.CreateAndStoreMyDidResult myPairwiseResult = Did.createAndStoreMyDid(walletHandle, "{}").get();
            this.sdkPairwiseVerkey = myPairwiseResult.getVerkey();

            walletHandle.closeWallet().get();
        }

        String getVerityPublicVerkey() {
            return verityPublicVerkey;
        }

        String getVerityPairwiseVerkey() {
            return verityPairwiseVerkey;
        }

        String getSdkPairwiseVerkey() {
            return sdkPairwiseVerkey;
        }
    }

    VerityConfig getConfig() throws InterruptedException, ExecutionException, IndyException {
        String walletName = "java_test_wallet";
        String walletKey = "12345";
        String webhookUrl = "http://localhost:3000";
        String verityUrl = "http://localhost:3000";
        TestWallet testWallet = new TestWallet(walletName, walletKey);
        JSONObject config = new JSONObject();
        config.put("walletName", walletName);
        config.put("walletKey", walletKey);
        config.put("verityUrl", verityUrl);
        config.put("verityPublicVerkey", testWallet.getVerityPublicVerkey());
        config.put("verityPairwiseVerkey", testWallet.getVerityPairwiseVerkey());
        config.put("sdkPairwiseVerkey", testWallet.getSdkPairwiseVerkey());
        config.put("webhookUrl", webhookUrl);
        return new VerityConfig(config.toString());
    }

    public JSONObject unpackMessage(VerityConfig verityConfig, byte[] message) throws InterruptedException, ExecutionException, IndyException {
        byte[] partiallyUnpackedMessageJWE = Crypto.unpackMessage(verityConfig.getWalletHandle(), message).get();
        String partiallyUnpackedMessage = new JSONObject(new String(partiallyUnpackedMessageJWE)).getString("message");
        return MessagePackaging.unpackMessageFromVerity(verityConfig, partiallyUnpackedMessage.getBytes());
    }

    @Test
    public void basicSchemaTest() throws Exception {
        try {
            VerityConfig verityConfig = getConfig();

            String schemaName = "test schema";
            String schemaVersion = "0.0.1";
            String attr1 = "name";
            String attr2 = "degree";
            Schema schema = new Schema(schemaName, schemaVersion, attr1, attr2);
            JSONObject unpackedMessage = unpackMessage(verityConfig, schema.getMessage(verityConfig));
            assertEquals(schema.toString(), unpackedMessage.toString());
            assertEquals(attr1, unpackedMessage.getJSONObject("schema").getJSONArray("attrNames").get(0));
            assertEquals(attr2, unpackedMessage.getJSONObject("schema").getJSONArray("attrNames").get(1));

            verityConfig.closeWallet();
        } catch(Exception e) {
            e.printStackTrace();
            assertTrue(false);
            throw e;
        } finally {
            String walletConfig = new JSONObject().put("id", "java_test_wallet").toString();
            String walletCredentials = new JSONObject().put("key", "12345").toString();
            Wallet.deleteWallet(walletConfig, walletCredentials).get();
        }
    }
}
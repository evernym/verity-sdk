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

public class ConnectionTest {

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

    @Test
    public void oneParamsConstructor() throws Exception {
        try {
            VerityConfig verityConfig = getConfig();

            String sourceId = "source_id";
            Connection connection = new Connection(sourceId);
            byte[] partiallyUnpackedMessageJWE = Crypto.unpackMessage(verityConfig.getWalletHandle(), connection.getMessage(verityConfig)).get();
            String partiallyUnpackedMessage = new JSONObject(new String(partiallyUnpackedMessageJWE)).getString("message");
            JSONObject unpackedMessage = MessagePackaging.unpackForwardMsg(verityConfig, new JSONObject(partiallyUnpackedMessage).getJSONArray("@msg"));
            assertEquals(connection.toString(), unpackedMessage.toString());
            String currentSourceId = unpackedMessage.getJSONObject("connectionDetail").getString("sourceId");
            assertEquals(sourceId, currentSourceId);

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

    @Test
    public void twoParamsConstructor() throws Exception {
        try {
            VerityConfig verityConfig = getConfig();

            String sourceId = "source_id";
            String phoneNumber = "123-456-7891";
            Connection connection = new Connection(sourceId, phoneNumber);
            byte[] partiallyUnpackedMessageJWE = Crypto.unpackMessage(verityConfig.getWalletHandle(), connection.getMessage(verityConfig)).get();
            String partiallyUnpackedMessage = new JSONObject(new String(partiallyUnpackedMessageJWE)).getString("message");
            JSONObject unpackedMessage = MessagePackaging.unpackForwardMsg(verityConfig, new JSONObject(partiallyUnpackedMessage).getJSONArray("@msg"));
            assertEquals(connection.toString(), unpackedMessage.toString());
            String currentSourceId = unpackedMessage.getJSONObject("connectionDetail").getString("sourceId");
            assertEquals(sourceId, currentSourceId);
            String currentPhoneNumber = unpackedMessage.getJSONObject("connectionDetail").getString("phoneNo");
            assertEquals(phoneNumber, currentPhoneNumber);

            verityConfig.closeWallet();
        } catch(Exception e) {
            e.printStackTrace();
            assertTrue(false);
        } finally {
            String walletConfig = new JSONObject().put("id", "java_test_wallet").toString();
            String walletCredentials = new JSONObject().put("key", "12345").toString();
            Wallet.deleteWallet(walletConfig, walletCredentials).get();
        }
    }
}
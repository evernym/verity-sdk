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
import org.junit.Ignore;
import org.junit.Test;

public class CredentialTest {
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
    public void basicCredDefTest() throws Exception {
        try {
            VerityConfig verityConfig = getConfig();

            String connectionId = "...someConnectionId...";
            String credDefId = "...someCredDefId...";
            JSONObject credentialValues = new JSONObject();
            credentialValues.put("name", "Jose Smith");
            credentialValues.put("degree", "Bachelors");
            credentialValues.put("gpa", "3.67");
            int price = 3;
            Credential credential = new Credential(connectionId, credDefId, credentialValues, price);

            JSONObject unpackedOfferMessage = unpackMessage(verityConfig, credential.getOfferMessage(verityConfig));            
            assertEquals(credential.offerMessageToString(), unpackedOfferMessage.toString());
            assertEquals(connectionId, unpackedOfferMessage.getString("connectionId"));
            assertEquals(credDefId, unpackedOfferMessage.getString("credDefId"));

            JSONObject unpackedCredentialMessage = unpackMessage(verityConfig, credential.getCredentialMessage(verityConfig));
            assertEquals(credential.credentialMessageToString(), unpackedCredentialMessage.toString());
            assertEquals(unpackedOfferMessage.getString("@id"), unpackedCredentialMessage.getJSONObject("~thread").getString("pthid"));
            assertEquals(connectionId, unpackedCredentialMessage.getString("connectionId"));
            assertEquals(credDefId, unpackedCredentialMessage.getJSONObject("credentialData").getString("credDefId"));
            assertEquals(credentialValues.toString(), unpackedCredentialMessage.getJSONObject("credentialData").getJSONObject("credentialValues").toString());
            assertEquals(price, unpackedCredentialMessage.getJSONObject("credentialData").getInt("price"));

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
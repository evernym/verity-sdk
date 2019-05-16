package com.evernym.verity.sdk.protocols;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.utils.MessagePackaging;
import com.evernym.verity.sdk.utils.VerityConfig;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.crypto.Crypto;
import org.hyperledger.indy.sdk.did.Did;
import org.hyperledger.indy.sdk.did.DidResults;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;
import org.junit.Test;

public class ProvableQuestionTest {
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

    VerityConfig getConfig() throws InterruptedException, ExecutionException, IndyException {
        String walletName = "java_test_wallet";
        String walletKey = "12345";
        String webhookUrl = "http://localhost:3000";
        String agencyUrl = "http://localhost:3000";
        TestWallet testWallet = new TestWallet(walletName, walletKey);
        JSONObject config = new JSONObject();
        config.put("walletName", walletName);
        config.put("walletKey", walletKey);
        config.put("agencyUrl", agencyUrl);
        config.put("agencyPublicVerkey", testWallet.getAgencyPublicVerkey());
        config.put("agencyPairwiseVerkey", testWallet.getAgencyPairwiseVerkey());
        config.put("sdkPairwiseVerkey", testWallet.getSdkPairwiseVerkey());
        config.put("webhookUrl", webhookUrl);
        return new VerityConfig(config.toString());
    }

    @Test
    public void properlyBuildMessage() throws Exception {
        try {
            VerityConfig verityConfig = getConfig();
            
            String connectionId = "abcd12345";
            String questionText = "Question text";
            String questionDetail = "Optional question detail";
            String[] validResponses = {"Yes", "No"};
            ProvableQuestion provableQuestion = new ProvableQuestion(connectionId, questionText, questionDetail, validResponses);
            byte[] partiallyUnpackedMessageJWE = Crypto.unpackMessage(verityConfig.getWalletHandle(), provableQuestion.getMessage(verityConfig)).get();
            String partiallyUnpackedMessage = new JSONObject(new String(partiallyUnpackedMessageJWE)).getString("message");
            JSONObject unpackedMessage = MessagePackaging.unpackMessageFromAgency(verityConfig, partiallyUnpackedMessage.getBytes());
            assertEquals(provableQuestion.toString(), unpackedMessage.toString());
            assertEquals(connectionId, unpackedMessage.getString("connection_id"));
            assertEquals(questionText, unpackedMessage.getJSONObject("question").getString("question_text"));
            assertEquals(questionDetail, unpackedMessage.getJSONObject("question").getString("question_detail"));
            assertEquals(validResponses[0], unpackedMessage.getJSONObject("question").getJSONArray("valid_responses").getJSONObject(0).getString("text"));
            assertNotNull(unpackedMessage.getJSONObject("question").getJSONArray("valid_responses").getJSONObject(0).getString("nonce"));
            assertEquals(validResponses[1], unpackedMessage.getJSONObject("question").getJSONArray("valid_responses").getJSONObject(1).getString("text"));
            assertNotNull(unpackedMessage.getJSONObject("question").getJSONArray("valid_responses").getJSONObject(1).getString("nonce"));

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
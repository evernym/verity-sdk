package com.evernym.verity.sdk.protocols;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import com.evernym.verity.sdk.utils.MessagePackaging;
import com.evernym.verity.sdk.utils.WalletContents;

import org.hyperledger.indy.sdk.crypto.Crypto;
import org.hyperledger.indy.sdk.did.*;
import org.hyperledger.indy.sdk.non_secrets.WalletRecord;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;
import org.junit.Test;

public class ConnectionTest {

    @Test
    public void noParamsConstructor() throws Exception {
        String agencyUrl = "http://agency.url";
        String walletConfig = new JSONObject().put("id", "java_test_wallet").toString();
        String walletCredentials = new JSONObject().put("key", "12345").toString();
        Wallet myWallet = null;
        try {
            Wallet.createWallet(walletConfig, walletCredentials).get();
            myWallet = Wallet.openWallet(walletConfig, walletCredentials).get();

            DidResults.CreateAndStoreMyDidResult theirResult = Did.createAndStoreMyDid(myWallet, "{}").get();
            String theirDid = theirResult.getDid();
            DidResults.CreateAndStoreMyDidResult theirPairwiseResult = Did.createAndStoreMyDid(myWallet, "{}").get();
            String theirPairwiseDid = theirPairwiseResult.getDid();
            DidResults.CreateAndStoreMyDidResult myPairwiseResult = Did.createAndStoreMyDid(myWallet, "{}").get();
            String myPairwiseDid = myPairwiseResult.getDid();
            WalletRecord.add(myWallet, "sdk_details", "agency_url", agencyUrl, null);
            WalletRecord.add(myWallet, "sdk_details", "agency_did", theirDid, null);
            WalletRecord.add(myWallet, "sdk_details", "agency_pairwise_did", theirPairwiseDid, null);
            WalletRecord.add(myWallet, "sdk_details", "my_pairwise_did", myPairwiseDid, null);

            myWallet.closeWallet().get();

            WalletContents walletContents = new WalletContents("java_test_wallet", "12345");

            Connection connection = new Connection();
            byte[] partiallyUnpackedMessageJWE = Crypto.unpackMessage(walletContents.getWalletHandle(), connection.encrypt(walletContents)).get();
            String partiallyUnpackedMessage = new JSONObject(new String(partiallyUnpackedMessageJWE)).getString("message");
            String unpackedMessage = MessagePackaging.unpackMessageFromAgency(walletContents, partiallyUnpackedMessage.getBytes());
            assertEquals(connection.toString(), unpackedMessage);

            walletContents.close();
        } catch(Exception e) {
            e.printStackTrace();
            assertTrue(false);
        } finally {
            Wallet.deleteWallet(walletConfig, walletCredentials).get();
        }
    }
}
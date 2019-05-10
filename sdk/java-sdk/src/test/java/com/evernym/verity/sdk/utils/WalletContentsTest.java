package com.evernym.verity.sdk.utils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.hyperledger.indy.sdk.wallet.*;
import org.hyperledger.indy.sdk.non_secrets.*;
import org.hyperledger.indy.sdk.did.*;

import org.junit.Test;
import org.json.JSONObject;

public class WalletContentsTest {

    @Test
    public void ableToFindParams() throws Exception {
        String agencyUrl = "http://agency.url";
        String walletConfig = new JSONObject().put("id", "java_test_wallet").toString();
        String walletCredentials = new JSONObject().put("key", "12345").toString();
        Wallet myWallet = null;
        try {
            Wallet.createWallet(walletConfig, walletCredentials).get();
            myWallet = Wallet.openWallet(walletConfig, walletCredentials).get();

            DidResults.CreateAndStoreMyDidResult theirResult = Did.createAndStoreMyDid(myWallet, "{}").get();
            String theirDid = theirResult.getDid();
            String theirVerkey = theirResult.getVerkey();
            DidResults.CreateAndStoreMyDidResult theirPairwiseResult = Did.createAndStoreMyDid(myWallet, "{}").get();
            String theirPairwiseDid = theirPairwiseResult.getDid();
            String theirPairwiseVerkey = theirPairwiseResult.getVerkey();
            DidResults.CreateAndStoreMyDidResult myPairwiseResult = Did.createAndStoreMyDid(myWallet, "{}").get();
            String myPairwiseDid = myPairwiseResult.getDid();
            String myPairwiseVerkey = myPairwiseResult.getVerkey();
            WalletRecord.add(myWallet, "sdk_details", "agency_url", agencyUrl, null);
            WalletRecord.add(myWallet, "sdk_details", "agency_did", theirDid, null);
            WalletRecord.add(myWallet, "sdk_details", "agency_pairwise_did", theirPairwiseDid, null);
            WalletRecord.add(myWallet, "sdk_details", "my_pairwise_did", myPairwiseDid, null);

            myWallet.closeWallet().get();

            WalletContents walletContents = new WalletContents("java_test_wallet", "12345");
            assertEquals(agencyUrl, walletContents.getAgencyUrl());
            assertEquals(theirVerkey, walletContents.getAgencyVerkey());
            assertEquals(theirPairwiseVerkey, walletContents.getAgencyPairwiseVerkey());
            assertEquals(myPairwiseVerkey, walletContents.getMyPairwiseVerkey());
            walletContents.close();
        } catch(Exception e) {
            e.printStackTrace();
            assertTrue(false);
        } finally {
            Wallet.deleteWallet(walletConfig, walletCredentials).get();
        }
    }
}
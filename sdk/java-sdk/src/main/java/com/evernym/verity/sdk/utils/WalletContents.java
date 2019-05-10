package com.evernym.verity.sdk.utils;

import java.util.concurrent.ExecutionException;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.wallet.*;
import org.hyperledger.indy.sdk.non_secrets.*;
import org.hyperledger.indy.sdk.did.*;
import org.json.JSONObject;

public class WalletContents {
    String agencyUrl;
    String agencyVerkey;
    String agencyPairwiseVerkey;
    String myPairwiseVerkey;
    Wallet walletHandle;

    public WalletContents(String walletName, String walletKey) throws InterruptedException, ExecutionException, IndyException {
        String walletConfig = new JSONObject().put("id", walletName).toString();
        String walletCredentials = new JSONObject().put("key", walletKey).toString();
        walletHandle = Wallet.openWallet(walletConfig, walletCredentials).get();
        agencyUrl = new JSONObject(WalletRecord.get(walletHandle, "sdk_details", "agency_url", "{}").get()).getString("value");
        String agencyDid = new JSONObject(WalletRecord.get(walletHandle, "sdk_details", "agency_did", "{}").get()).getString("value");
        agencyVerkey = Did.keyForLocalDid(walletHandle, agencyDid).get();
        String agencyPairwiseDid = new JSONObject(WalletRecord.get(walletHandle, "sdk_details", "agency_pairwise_did", "{}").get()).getString("value");
        agencyPairwiseVerkey = Did.keyForLocalDid(walletHandle, agencyPairwiseDid).get();
        String myPairwiseDid = new JSONObject(WalletRecord.get(walletHandle, "sdk_details", "my_pairwise_did", "{}").get()).getString("value");
        myPairwiseVerkey = Did.keyForLocalDid(walletHandle, myPairwiseDid).get();
    }

    public void close() throws InterruptedException, ExecutionException, IndyException {
        walletHandle.closeWallet().get();
    }

    public String getAgencyUrl() {
        return agencyUrl;
    }

    public String getAgencyVerkey() {
        return agencyVerkey;
    }

    public String getAgencyPairwiseVerkey() {
        return agencyPairwiseVerkey;
    }

    public String getMyPairwiseVerkey() {
        return myPairwiseVerkey;
    }

    public Wallet getWalletHandle() {
        return walletHandle;
    }
}
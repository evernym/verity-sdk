package com.evernym.verity.sdk;

import java.util.concurrent.ExecutionException;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.did.Did;
import org.hyperledger.indy.sdk.did.DidResults;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;

public class TestWallet {
    String verityPublicVerkey;
    String verityPairwiseVerkey;
    String sdkPairwiseVerkey;
    String verityPairwiseDID;

    public TestWallet(String walletName, String walletKey) throws InterruptedException, ExecutionException, IndyException {
        String walletConfig = new JSONObject().put("id", walletName).toString();
        String walletCredentials = new JSONObject().put("key", walletKey).toString();
        Wallet.createWallet(walletConfig, walletCredentials).get();
        Wallet walletHandle = Wallet.openWallet(walletConfig, walletCredentials).get();
        
        DidResults.CreateAndStoreMyDidResult theirResult = Did.createAndStoreMyDid(walletHandle, "{}").get();
        this.verityPublicVerkey = theirResult.getVerkey();
        DidResults.CreateAndStoreMyDidResult theirPairwiseResult = Did.createAndStoreMyDid(walletHandle, "{}").get();
        this.verityPairwiseVerkey = theirPairwiseResult.getVerkey();
        this.verityPairwiseDID = theirPairwiseResult.getDid();
        DidResults.CreateAndStoreMyDidResult myPairwiseResult = Did.createAndStoreMyDid(walletHandle, "{}").get();
        this.sdkPairwiseVerkey = myPairwiseResult.getVerkey();

        walletHandle.closeWallet().get();
    }

    public String getVerityPublicVerkey() {
        return verityPublicVerkey;
    }

    public String getVerityPairwiseVerkey() {
        return verityPairwiseVerkey;
    }

    public String getSdkPairwiseVerkey() {
        return sdkPairwiseVerkey;
    }

    public String getVerityPairwiseDID() {
        return verityPairwiseDID;
    }
}
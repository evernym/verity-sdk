package com.evernym.verity.sdk;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.did.Did;
import org.hyperledger.indy.sdk.did.DidResults;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class TestWallet implements AutoCloseable {
    private final String verityPublicDID;
    private final String verityPublicVerkey;
    private final String verityPairwiseDID;
    private final String verityPairwiseVerkey;
    private final String sdkPairwiseDID;
    private final String sdkPairwiseVerkey;
    private final String walletCredentials;
    private final String walletConfig;

    public TestWallet(String walletName, String walletKey) throws InterruptedException, ExecutionException, IndyException {
        walletConfig = new JSONObject().put("id", walletName).toString();
        walletCredentials = new JSONObject().put("key", walletKey).toString();
        Wallet.createWallet(walletConfig, walletCredentials).get();
        Wallet walletHandle = Wallet.openWallet(walletConfig, walletCredentials).get();
        
        DidResults.CreateAndStoreMyDidResult theirResult = Did.createAndStoreMyDid(walletHandle, "{}").get();
        this.verityPublicDID = theirResult.getDid();
        this.verityPublicVerkey = theirResult.getVerkey();
        DidResults.CreateAndStoreMyDidResult theirPairwiseResult = Did.createAndStoreMyDid(walletHandle, "{}").get();
        this.verityPairwiseVerkey = theirPairwiseResult.getVerkey();
        this.verityPairwiseDID = theirPairwiseResult.getDid();
        DidResults.CreateAndStoreMyDidResult myPairwiseResult = Did.createAndStoreMyDid(walletHandle, "{}").get();
        this.sdkPairwiseDID = myPairwiseResult.getDid();
        this.sdkPairwiseVerkey = myPairwiseResult.getVerkey();

        walletHandle.closeWallet().get();
    }

    public String getVerityPublicDID() {
        return verityPublicDID;
    }

    public String getVerityPublicVerkey() {
        return verityPublicVerkey;
    }

    public String getVerityPairwiseDID() {
        return verityPairwiseDID;
    }

    public String getVerityPairwiseVerkey() {
        return verityPairwiseVerkey;
    }

    public String getSdkPairwiseDID() {
        return sdkPairwiseDID;
    }

    public String getSdkPairwiseVerkey() {
        return sdkPairwiseVerkey;
    }

    @Override
    public void close() throws Exception {
        Wallet.deleteWallet(walletConfig, walletCredentials).get();
    }
}
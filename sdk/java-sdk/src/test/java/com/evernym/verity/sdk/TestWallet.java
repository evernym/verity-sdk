package com.evernym.verity.sdk;

import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.wallet.DefaultWalletConfig;
import com.evernym.verity.sdk.wallet.WalletConfig;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.did.Did;
import org.hyperledger.indy.sdk.did.DidResults;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class TestWallet implements AutoCloseable, WalletConfig {
    private final String verityPublicDID;
    private final String verityPublicVerkey;
    private final String verityPairwiseDID;
    private final String verityPairwiseVerkey;
    private final String sdkPairwiseDID;
    private final String sdkPairwiseVerkey;
    private final DefaultWalletConfig walletConfig;

    public TestWallet(String walletName, String walletKey) throws WalletException {
        try {
            walletConfig = DefaultWalletConfig.build(walletName, walletKey);
            Wallet.createWallet(walletConfig.config(), walletConfig.credential()).get();
            Wallet walletHandle = Wallet.openWallet(walletConfig.config(), walletConfig.credential()).get();

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
        } catch (InterruptedException | ExecutionException | IndyException e) {
            throw new WalletException("Failed to initialize TestWallet", e);
        }
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
    public void close() throws WalletException {
        try {
            Wallet.deleteWallet(walletConfig.config(), walletConfig.credential()).get();
        } catch (IndyException | ExecutionException | InterruptedException e) {
            throw new WalletException("Failed to close TestWallet", e);
        }
    }

    @Override
    public String config() {return walletConfig.config();}

    @Override
    public String credential() {return walletConfig.credential();}

    @Override
    public void addToJson(JSONObject json) {
        throw new RuntimeException("Not implemented for tests");
    }
}
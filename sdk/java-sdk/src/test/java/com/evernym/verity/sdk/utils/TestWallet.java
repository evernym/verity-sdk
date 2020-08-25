package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.wallet.DefaultWalletConfig;
import com.evernym.verity.sdk.wallet.WalletConfig;
import com.evernym.verity.sdk.wallet.WalletUtil;
import org.hyperledger.indy.sdk.IndyException;
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
        this(walletName, walletKey, null);
    }

    public TestWallet(String walletName, String walletKey, String seed) throws WalletException {
        try {
            walletConfig = DefaultWalletConfig.build(walletName, walletKey);
            WalletUtil.tryCreateWallet(walletConfig);
            Wallet walletHandle = WalletUtil.openIndyWallet(walletConfig);

            Did theirResult = Did.createNewDid(walletHandle);
            this.verityPublicDID = theirResult.did;
            this.verityPublicVerkey = theirResult.verkey;
            Did theirPairwiseResult = Did.createNewDid(walletHandle);
            this.verityPairwiseVerkey = theirPairwiseResult.verkey;
            this.verityPairwiseDID = theirPairwiseResult.did;
            Did myPairwiseResult = Did.createNewDid(walletHandle, seed);
            this.sdkPairwiseDID = myPairwiseResult.did;
            this.sdkPairwiseVerkey = myPairwiseResult.verkey;

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
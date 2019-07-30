package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.exceptions.WalletException;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.hyperledger.indy.sdk.wallet.WalletExistsException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class WalletUtil {
    public static void tryCreateWallet(String walletName, String walletKey) throws WalletException {
        try {
            String walletConfig = new JSONObject().put("id", walletName).toString();
            String walletCredentials = new JSONObject().put("key", walletKey).toString();
            Wallet.createWallet(walletConfig, walletCredentials).get();
        }
        catch (WalletExistsException ignored) {} // This is ok, we want to only create if wallet don't exist
        catch (IndyException | InterruptedException | ExecutionException e) {
            throw new WalletException("Unable to try-create wallet", e);
        }
    }
}

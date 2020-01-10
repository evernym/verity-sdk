package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.wallet.WalletConfig;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.hyperledger.indy.sdk.wallet.WalletExistsException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class WalletUtil {
    public static void tryCreateWallet(String walletName, String walletKey) throws WalletException {
        String walletConfig = new JSONObject().put("id", walletName).toString();
        String walletCredentials = new JSONObject().put("key", walletKey).toString();
        tryToCreateWallet(walletConfig, walletCredentials);
    }

    public static void tryCreateWallet(String walletName, String walletKey, String walletPath) throws WalletException {
        String walletConfig = new JSONObject()
                .put("id", walletName)
                .put("path", walletPath)
                .toString();
        String walletCredentials = new JSONObject().put("key", walletKey).toString();
        tryToCreateWallet(walletConfig, walletCredentials);
    }

    public static void tryCreateWallet(WalletConfig config) throws WalletException {
        tryToCreateWallet(config.config(), config.credential());
    }

    private static void tryToCreateWallet(String walletConfig, String walletCredentials) throws WalletException {
        try {
            Wallet.createWallet(walletConfig, walletCredentials).get();
        }
        catch (WalletExistsException ignored) {} // This is ok, we want to only create if wallet don't exist
        catch (IndyException | InterruptedException | ExecutionException e) {
            if(e.getCause() != null && e.getCause() instanceof WalletExistsException) {}  // This is ok, we want to only create if wallet don't exist
            else {
                throw new WalletException("Unable to try-create wallet", e);
            }
        }
    }
}

package com.evernym.verity.sdk.wallet;

import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.exceptions.WalletOpenException;
import com.evernym.vdrtools.IndyException;
import com.evernym.vdrtools.LibIndy;
import com.evernym.vdrtools.wallet.Wallet;
import com.evernym.vdrtools.wallet.WalletExistsException;
import com.evernym.vdrtools.wallet.WalletNotFoundException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Static Utilities for creating local wallets. Indy-sdk don't provide a way to check if a wallet exists so
 * these utils encapsulate creating a wallet and trapping the error when the wallet already exists.
 *
 */
public class WalletUtil {
    private WalletUtil() {}

    /**
     * Tries to create a wallet with the given parameters. If creation fails because the wallet already exists, that
     * error is trapped and this function will complete but other errors will be raised.
     * @param walletName the given name for the wallet to be created
     * @param walletKey the given key for the wallet to be created
     * @throws WalletException when the creation of the wallet fails for other reasons besides that the wallet already
     * exists.
     */
    public static void tryCreateWallet(String walletName, String walletKey) throws WalletException {
        DefaultWalletConfig wallet = DefaultWalletConfig.build(walletName, walletKey);
        String walletConfig = wallet.config();
        String walletCredentials = wallet.credential();
        tryToCreateWallet(walletConfig, walletCredentials);
    }

    /**
     * Tries to create a wallet with the given parameters. If creation fails because the wallet already exists, that
     * error is trapped and this function will complete but other errors will be raised.
     * @param walletName the given name for the wallet to be created
     * @param walletKey the given key for the wallet to be created
     * @param walletPath the given path where the wallet on disk file will be created
     * @throws WalletException when the creation of the wallet fails for other reasons besides that the wallet already
     * exists.
     */
    public static void tryCreateWallet(String walletName, String walletKey, String walletPath) throws WalletException {
        DefaultWalletConfig wallet = DefaultWalletConfig.build(walletName, walletKey, walletPath);
        String walletConfig = wallet.config();
        String walletCredentials = wallet.credential();

        tryToCreateWallet(walletConfig, walletCredentials);
    }

    /**
     * Tries to create a wallet with the given parameters. If creation fails because the wallet already exists, that
     * error is trapped and this function will complete but other errors will be raised.
     *
     * @param config the WalletConfig object that defines configuration for the creation of the wallet
     * @throws WalletException when the creation of the wallet fails for other reasons besides that the wallet already
     * exists.
     */
    public static void tryCreateWallet(WalletConfig config) throws WalletException {
        tryToCreateWallet(config.config(), config.credential());
    }

    private static void tryToCreateWallet(String walletConfig, String walletCredentials) throws WalletException {
        try {
            if(LibIndy.api == null) {
                throw new WalletException("Libindy failed to initialize - likely the shared library was not found");
            }
            Wallet.createWallet(walletConfig, walletCredentials).get();
        }
        catch (WalletExistsException ignored) {} // This is ok, we want to only create if wallet don't exist
        catch (IndyException | InterruptedException | ExecutionException e) {
            if( !(e.getCause() != null && e.getCause() instanceof WalletExistsException)) {
                throw new WalletException("Unable to try-create wallet", e);
            }
            // This is ok, we want to only create if wallet don't exist
        }
    }

    public static Wallet openIndyWallet(WalletConfig walletConfig) throws WalletOpenException, JSONException {
        try {
            if(LibIndy.api == null) {
                throw new WalletOpenException("Libindy failed to initialize - likely the shared library was not found");
            }
            return Wallet.openWallet(walletConfig.config(), walletConfig.credential()).get();
        }
        catch (IndyException | ExecutionException | InterruptedException e){
            throw new WalletOpenException("Wallet failed to open", e);
        }
    }

    /**
     * Deletes indy wallet
     * @param walletName the given name for the wallet to be deleted
     * @param walletKey the given key for the wallet to be deleted
     * @throws WalletException when the deletion of the wallet fails
     */
    public static void deleteWallet(String walletName, String walletKey) throws WalletException {
        String walletConfig = new JSONObject().put("id", walletName).toString();
        String walletCredentials = new JSONObject().put("key", walletKey).toString();
        try {
            if(LibIndy.api == null) {
                throw new WalletException("Libindy failed to initialize - likely the shared library was not found");
            }
            Wallet.deleteWallet(walletConfig, walletCredentials).get();
        }
        catch (IndyException | InterruptedException | ExecutionException e) {
            if( !(e.getCause() != null && e.getCause() instanceof WalletNotFoundException)) {
                throw new WalletException("Unable to delete wallet", e);
            }
        }
    }
}

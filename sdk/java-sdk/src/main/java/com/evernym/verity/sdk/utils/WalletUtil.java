package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.wallet.WalletConfig;

/**
 * @deprecated replaced by {@link com.evernym.verity.sdk.wallet.WalletUtil}
 */
@Deprecated
public class WalletUtil {
    private WalletUtil() {}

    /**
     *
     * @deprecated replaced by {@link com.evernym.verity.sdk.wallet.WalletUtil#tryCreateWallet(String, String) }
     */
    @Deprecated
    public static void tryCreateWallet(String walletName, String walletKey) throws WalletException {
        com.evernym.verity.sdk.wallet.WalletUtil.tryCreateWallet(walletName, walletKey);
    }

    /**
     *
     * @deprecated replaced by {@link com.evernym.verity.sdk.wallet.WalletUtil#tryCreateWallet(String, String, String)}  }
     */
    @Deprecated
    public static void tryCreateWallet(String walletName, String walletKey, String walletPath) throws WalletException {
        com.evernym.verity.sdk.wallet.WalletUtil.tryCreateWallet(walletName, walletKey, walletPath);
    }

    /**
     *
     * @deprecated replaced by {@link com.evernym.verity.sdk.wallet.WalletUtil#tryCreateWallet(WalletConfig)}  }
     */
    @Deprecated
    public static void tryCreateWallet(WalletConfig config) throws WalletException {
        com.evernym.verity.sdk.wallet.WalletUtil.tryCreateWallet(config);
    }


}

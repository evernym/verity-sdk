using Hyperledger.Indy;
using Hyperledger.Indy.WalletApi;
using System.Json;
using VeritySDK.Exceptions;

namespace VeritySDK.Wallets
{
    /// <summary>
    /// Static Utilities for creating local wallets.Indy-sdk don't provide a way to check if a wallet exists so
    /// these utils encapsulate creating a wallet and trapping the error when the wallet already exists.
    /// </summary>
    public class WalletUtil
    {
        private WalletUtil() { }

        /// <summary>
        /// Tries to create a wallet with the given parameters. If creation fails because the wallet already exists, that
        /// error is trapped and this function will complete but other errors will be raised. 
        /// </summary>
        /// <param name="walletName">the given name for the wallet to be created</param>
        /// <param name="walletKey">the given key for the wallet to be created</param>
        public static void tryCreateWallet(string walletName, string walletKey)
        {
            DefaultWalletConfig wallet = DefaultWalletConfig.build(walletName, walletKey);
            string walletConfig = wallet.config();
            string walletCredentials = wallet.credential();
            tryToCreateWallet(walletConfig, walletCredentials);
        }

        /// <summary>
        /// Tries to create a wallet with the given parameters.If creation fails because the wallet already exists, that
        /// error is trapped and this function will complete but other errors will be raised.
        /// </summary>
        /// <param name="walletName">the given name for the wallet to be created</param>
        /// <param name="walletKey">the given key for the wallet to be created</param>
        /// <param name="walletPath">the given path where the wallet on disk file will be created</param>
        public static void tryCreateWallet(string walletName, string walletKey, string walletPath)
        {
            DefaultWalletConfig wallet = DefaultWalletConfig.build(walletName, walletKey, walletPath);
            string walletConfig = wallet.config();
            string walletCredentials = wallet.credential();

            tryToCreateWallet(walletConfig, walletCredentials);
        }

        /// <summary>
        /// Tries to create a wallet with the given parameters.If creation fails because the wallet already exists, that
        /// error is trapped and this function will complete but other errors will be raised.
        /// </summary>
        /// <param name="config">the WalletConfig object that defines configuration for the creation of the wallet</param>
        public static void tryCreateWallet(WalletConfig config)
        {
            tryToCreateWallet(config.config(), config.credential());
        }

        private static void tryToCreateWallet(string walletConfig, string walletCredentials)
        {
            try
            {
                Wallet.CreateWalletAsync(walletConfig, walletCredentials).GetAwaiter().GetResult();
            }
            catch (WalletExistsException ignored)
            {
                // This is ok, we want to only create if wallet don't exist
                var s = ignored.Message; // for fix build warning;
            }
            catch (IndyException e)
            {
                if (!(e.InnerException != null && e.InnerException is WalletExistsException))
                {
                    throw new WalletException("Unable to try-create wallet", e);
                }
                // This is ok, we want to only create if wallet don't exist
            }
        }

        public static Wallet openIndyWallet(WalletConfig walletConfig)
        {
            try
            {
                return Wallet.OpenWalletAsync(walletConfig.config(), walletConfig.credential()).GetAwaiter().GetResult();
            }
            catch (IndyException e)
            {
                throw new WalletOpenException("Wallet failed to open", e);
            }
        }

        /// <summary>
        /// Deletes indy wallet 
        /// </summary>
        /// <param name="walletName">the given name for the wallet to be created</param>
        /// <param name="walletKey">the given key for the wallet to be created</param>
        public static void deleteWallet(string walletName, string walletKey)
        {
            var json = new JsonObject();
            json.Add("id", walletName);
            string walletConfig = json.ToString();

            json = new JsonObject();
            json.Add("key", walletKey);
            string walletCredentials = json.ToString();
            try
            {
                Wallet.DeleteWalletAsync(walletConfig, walletCredentials).GetAwaiter().GetResult();
            }
            catch (IndyException e)
            {
                if (!(e.InnerException != null && e.InnerException is WalletNotFoundException))
                {
                    throw new WalletException("Unable to delete wallet", e);
                }
            }
        }
    }
}
using Com.Evernym.Vdrtools;
using Com.Evernym.Vdrtools.WalletApi;
using System;
using System.Collections.Generic;
using System.Json;
using System.Text;
using VeritySDK.Exceptions;
using VeritySDK.Utils;
using VeritySDK.Wallets;

namespace VeritySDK.Test
{
    public class TestWallet : WalletConfig, IDisposable
    {
        private string verityPublicDID;
        private string verityPublicVerkey;
        private string verityPairwiseDID;
        private string verityPairwiseVerkey;
        private string sdkPairwiseDID;
        private string sdkPairwiseVerkey;
        private DefaultWalletConfig walletConfig;

        public TestWallet(string walletName, string walletKey) : this(walletName, walletKey, null)
        {
        }

        public TestWallet(string walletName, string walletKey, string seed)
        {
            try
            {
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

                walletHandle.CloseAsync().GetAwaiter().GetResult();
            }
            catch (IndyException e)
            {
                throw new WalletException("Failed to initialize TestWallet", e);
            }
        }

        public string getVerityPublicDID()
        {
            return verityPublicDID;
        }

        public string getVerityPublicVerkey()
        {
            return verityPublicVerkey;
        }

        public string getVerityPairwiseDID()
        {
            return verityPairwiseDID;
        }

        public string getVerityPairwiseVerkey()
        {
            return verityPairwiseVerkey;
        }

        public string getSdkPairwiseDID()
        {
            return sdkPairwiseDID;
        }

        public string getSdkPairwiseVerkey()
        {
            return sdkPairwiseVerkey;
        }

        public void close()
        {
            try
            {
                Wallet.DeleteWalletAsync(walletConfig.config(), walletConfig.credential()).GetAwaiter().GetResult();
            }
            catch (IndyException e)
            {
                throw new WalletException("Failed to close TestWallet", e);
            }
        }

        public string config()
        {
            return walletConfig.config();
        }

        public string credential()
        {
            return walletConfig.credential();
        }

        public void addToJson(JsonObject json)
        {
            throw new Exception("Not implemented for tests");
        }

        public void Dispose()
        {
            close();
        }
    }
}

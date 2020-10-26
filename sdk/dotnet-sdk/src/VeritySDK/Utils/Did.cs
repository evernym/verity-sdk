using Hyperledger.Indy;
using Hyperledger.Indy.DidApi;
using Hyperledger.Indy.WalletApi;
using System;
using System.Json;

namespace VeritySDK
{
    /**
     * Simple holder object for a DID
     */
    public class Did
    {
        public string did;

        public string verkey;

        /**
         * Constructs object with given DID and verkey
         * @param did given DID
         * @param verkey given verkey
         */
        public Did(string did, string verkey)
        {
            this.did = did;
            this.verkey = verkey;
        }

        /**
         * Constructs object from the Indy SDK wrapper object CreateAndStoreMyDidResult
         *
         * @param didSource source of information for Did object
         */

        public Did(CreateAndStoreMyDidResult didSource)
        {
            this.did = didSource.Did;
            this.verkey = didSource.VerKey;
        }

        /**
         * Creates a new generic DID in the given wallet
         * @param handle handle to an created and opened indy wallet
         * @return a DID object based on the DID created in the wallet
         * @throws WalletException when wallet operations fails
         */
        public static Did createNewDid(Wallet handle) //throws WalletException
        {
            return createNewDid(handle, null);
        }

        /**
         * Creates a new generic DID in the given wallet based on a given seed
         * @param handle handle to an created and opened indy wallet
         * @param seed a 32 character seed string used to deterministically create a key for the DID
         * @return a DID object based on the DID created in the wallet
         * @throws WalletException when wallet operations fails
         */
        public static Did createNewDid(Wallet handle, string seed) //throws WalletException
        {
            try
            {
                string didJson = "{}";
                if (seed != null)
                {
                    didJson = CreateAndStoreMyDidJSONParameter(null, seed, null, null);
                };

                var result = Hyperledger.Indy.DidApi.Did.CreateAndStoreMyDidAsync(handle, didJson).GetAwaiter().GetResult();

                return new Did(result);
            }
            catch (IndyException e /* | InterruptedException | ExecutionException */)
            {
                throw new WalletException("Unable to create DID with wallet", e);
            }
        }


        private static string CreateAndStoreMyDidJSONParameter(String did, String seed, String cryptoType, Boolean? cid)
        {
            JsonObject rtn = new JsonObject();

            if (did != null) rtn.Add("did", did);
            if (seed != null) rtn.Add("seed", seed);
            if (cryptoType != null) rtn.Add("crypto_type", cryptoType);
            if (cid != null) rtn.Add("cid", cid);

            return rtn.ToString();
        }

    }
}

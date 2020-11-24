using Hyperledger.Indy;
using Hyperledger.Indy.DidApi;
using Hyperledger.Indy.WalletApi;
using System;
using System.Json;
using VeritySDK.Exceptions;

namespace VeritySDK.Utils
{
    /// <summary>
    /// Simple holder object for a DID
    /// </summary>
    public class Did
    {
        public string did;

        public string verkey;

        /// <summary>
        /// Constructs object with given DID and verkey 
        /// </summary>
        /// <param name="did">did given DID</param>
        /// <param name="verkey">verkey given verkey</param>
        public Did(string did, string verkey)
        {
            this.did = did;
            this.verkey = verkey;
        }

        /// <summary>
        /// Constructs object from the Indy SDK wrapper object CreateAndStoreMyDidResult 
        /// </summary>
        /// <param name="didSource">source of information for Did object</param>
        public Did(CreateAndStoreMyDidResult didSource)
        {
            this.did = didSource.Did;
            this.verkey = didSource.VerKey;
        }

        /// <summary>
        /// Creates a new generic DID in the given wallet 
        /// </summary>
        /// <param name="handle">handle handle to an created and opened indy wallet</param>
        /// <returns>a DID object based on the DID created in the wallet</returns>
        public static Did createNewDid(Wallet handle) 
        {
            return createNewDid(handle, null);
        }

        /// <summary>
        /// Creates a new generic DID in the given wallet based on a given seed 
        /// </summary>
        /// <param name="handle">handle handle to an created and opened indy wallet</param>
        /// <param name="seed">a 32 character seed string used to deterministically create a key for the DID</param>
        /// <returns>a DID object based on the DID created in the wallet</returns>
        public static Did createNewDid(Wallet handle, string seed)
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
            catch (Exception e)
            {
                throw new WalletException("Unable to create DID with wallet", e);
            }
        }


        private static string CreateAndStoreMyDidJSONParameter(string did, string seed, string cryptoType, bool? cid)
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

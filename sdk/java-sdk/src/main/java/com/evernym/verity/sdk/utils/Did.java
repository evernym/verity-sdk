package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.vdrtools.IndyException;
import com.evernym.vdrtools.did.DidJSONParameters;
import com.evernym.vdrtools.did.DidResults;
import com.evernym.vdrtools.wallet.Wallet;

import java.util.concurrent.ExecutionException;

/**
 * Simple holder object for a DID
 */
public class Did {
    public final String did;
    public final String verkey;

    /**
     * Constructs object with given DID and verkey
     * @param did given DID
     * @param verkey given verkey
     */
    public Did(String did, String verkey) {
        this.did = did;
        this.verkey = verkey;
    }

    /**
     * Constructs object from the Indy SDK wrapper object CreateAndStoreMyDidResult
     *
     * @param didSource source of information for Did object
     */
    public Did(DidResults.CreateAndStoreMyDidResult didSource) {
        this.did = didSource.getDid();
        this.verkey = didSource.getVerkey();
    }

    /**
     * Creates a new generic DID in the given wallet
     * @param handle handle to an created and opened indy wallet
     * @return a DID object based on the DID created in the wallet
     * @throws WalletException when wallet operations fails
     */
    public static Did createNewDid(Wallet handle) throws WalletException {
        return createNewDid(handle, null);
    }

    /**
     * Creates a new generic DID in the given wallet based on a given seed
     * @param handle handle to an created and opened indy wallet
     * @param seed a 32 character seed string used to deterministically create a key for the DID
     * @return a DID object based on the DID created in the wallet
     * @throws WalletException when wallet operations fails
     */
    public static Did createNewDid(Wallet handle, String seed) throws WalletException {
        try {
            String didJson = "{}";
            if (seed != null) {
                didJson = new DidJSONParameters.CreateAndStoreMyDidJSONParameter(
                        null,
                        seed,
                        null,
                        null
                ).toJson();
            }

            return new Did(com.evernym.vdrtools.did.Did.createAndStoreMyDid(
                    handle,
                    didJson
            ).get());
        } catch (InterruptedException | ExecutionException | IndyException e) {
            throw new WalletException("Unable to create DID with wallet", e);
        }
    }
}

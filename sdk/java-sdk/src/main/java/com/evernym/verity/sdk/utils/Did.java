package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.exceptions.WalletException;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.did.DidResults;
import org.hyperledger.indy.sdk.wallet.Wallet;

import java.util.concurrent.ExecutionException;

public class Did {
    public static Did createNewDid(Wallet handle) throws WalletException {
        try {
            return new Did(org.hyperledger.indy.sdk.did.Did.createAndStoreMyDid(
                    handle,
                    "{}"
            ).get());
        } catch (InterruptedException | ExecutionException | IndyException e) {
            throw new WalletException("Unable to create DID with wallet", e);
        }
    }

    public final String did;
    public final String verkey;

    public Did(String did, String verkey) {
        this.did = did;
        this.verkey = verkey;
    }

    public Did(DidResults.CreateAndStoreMyDidResult didSource) {
        this.did = didSource.getDid();
        this.verkey = didSource.getVerkey();
    }
}

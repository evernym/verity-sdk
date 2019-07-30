package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.exceptions.WalletOpenException;
import org.hyperledger.indy.sdk.wallet.Wallet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.evernym.verity.sdk.utils.VerityUtil.retrieveVerityPublicDid;
import static com.evernym.verity.sdk.utils.WalletUtil.tryCreateWallet;

public class ContextBuilder {

    public static Context fromScratch(String walletKey, String verityUrl)
            throws IOException, WalletException {

        return fromScratch("default", walletKey, verityUrl);
    }

    public static Context fromScratch(String walletName, String walletKey, String verityUrl)
            throws IOException, WalletException {
        tryCreateWallet(walletName, walletKey);

        Did verityPublicDid = retrieveVerityPublicDid(verityUrl);

        return _scratchContext(walletName, walletKey, verityUrl, verityPublicDid);
    }

    static Context _scratchContext(String walletName, String walletKey, String verityUrl, Did verityDid)
            throws WalletException {

        Context inter = new ContextBuilder()
                .verityPublicDID(verityDid.did)
                .verityPublicVerkey(verityDid.verkey)
                .walletName(walletName)
                .walletKey(walletKey)
                .verityUrl(String.format("%s/agency/msg", verityUrl))
                .build();

        Did mime = Did.createNewDid(inter.walletHandle());

        return inter.toContextBuilder()
                .sdkPairwiseDID(mime.did)
                .sdkPairwiseVerkey(mime.verkey)
                .build();
    }

    private Map<String, String> elements = new HashMap<>();
    private Wallet walletHandle = null;

    private final String walletName = "walletName";
    private final String walletKey = "walletKey";
    private final String verityUrl = "verityUrl";
    private final String verityPublicDID = "verityPublicDID";
    private final String verityPublicVerkey = "verityPublicVerkey";
    private final String verityPairwiseDID = "verityPairwiseDID";
    private final String verityPairwiseVerkey = "verityPairwiseVerkey";
    private final String sdkPairwiseDID = "sdkPairwiseDID";
    private final String sdkPairwiseVerkey = "sdkPairwiseVerkey";
    private final String endpointUrl = "endpointUrl";

    public ContextBuilder() {}

    private ContextBuilder putElement(String key, String val) {
        elements.put(key, val);
        return this;
    }

    public ContextBuilder walletName(String val) {return putElement(walletName, val);}
    public ContextBuilder walletKey(String val) {return putElement(walletKey, val);}
    public ContextBuilder verityUrl(String val) {return putElement(verityUrl, val);}
    public ContextBuilder verityPublicDID(String val) {return putElement(verityPublicDID, val);}
    public ContextBuilder verityPublicVerkey(String val) {return putElement(verityPublicVerkey, val);}
    public ContextBuilder verityPairwiseDID(String val) {return putElement(verityPairwiseDID, val);}
    public ContextBuilder verityPairwiseVerkey(String val) {return putElement(verityPairwiseVerkey, val);}
    public ContextBuilder sdkPairwiseDID(String val) {return putElement(sdkPairwiseDID, val);}
    public ContextBuilder sdkPairwiseVerkey(String val) {return putElement(sdkPairwiseVerkey, val);}
    public ContextBuilder endpointUrl(String val) {return putElement(endpointUrl, val);}
    ContextBuilder walletHandle(Wallet val) {
        walletHandle = val;
        return this;
    }

    /**
     *
     * @return Built Context instance from this builder.
     * @throws WalletOpenException when the wallet does not exist or Indy is unable to open it.
     */
    public Context build() throws WalletOpenException {
        if (walletHandle == null) {
            return new Context(
                    elements.get(walletName),
                    elements.get(walletKey),
                    elements.get(verityUrl),
                    elements.get(verityPublicDID),
                    elements.get(verityPublicVerkey),
                    elements.get(verityPairwiseDID),
                    elements.get(verityPairwiseVerkey),
                    elements.get(sdkPairwiseDID),
                    elements.get(sdkPairwiseVerkey),
                    elements.get(endpointUrl)
            );
        }
        else {
            return new Context(
                    elements.get(walletName),
                    elements.get(walletKey),
                    elements.get(verityUrl),
                    elements.get(verityPublicDID),
                    elements.get(verityPublicVerkey),
                    elements.get(verityPairwiseDID),
                    elements.get(verityPairwiseVerkey),
                    elements.get(sdkPairwiseDID),
                    elements.get(sdkPairwiseVerkey),
                    elements.get(endpointUrl),
                    walletHandle
            );
        }
    }
}


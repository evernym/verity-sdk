package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.exceptions.WalletOpenException;
import com.evernym.verity.sdk.wallet.DefaultWalletConfig;
import com.evernym.verity.sdk.wallet.WalletConfig;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.evernym.verity.sdk.utils.VerityUtil.retrieveVerityPublicDid;
import static com.evernym.verity.sdk.utils.WalletUtil.tryCreateWallet;

public class ContextBuilder {
    public static Context fromScratch(String walletId, String walletKey, String verityUrl)
            throws IOException, WalletException {

        tryCreateWallet(walletId, walletKey);
        return scratchContext(DefaultWalletConfig.build(walletId, walletKey), verityUrl);
    }

    public static Context fromScratch(WalletConfig walletConfig, String verityUrl)
            throws IOException, WalletException {

        tryCreateWallet(walletConfig);
        return scratchContext(walletConfig, verityUrl);
    }

    public static ContextBuilder fromJson(String json) {
        return new ContextBuilder().json(json);
    }

    public static ContextBuilder fromJson(JSONObject json) {
        return new ContextBuilder().json(json);
    }

    public static ContextBuilder blank() {
        return new ContextBuilder();
    }

    protected static Context scratchContext(WalletConfig wallet, String verityUrl)
            throws WalletException, IOException {

        Did verityDid = retrieveVerityPublicDid(verityUrl);

        return scratchContext(wallet, verityUrl, verityDid);
    }

    protected static Context scratchContext(WalletConfig wallet, String verityUrl, Did verityDid)
            throws WalletException {

        Context inter = new ContextBuilder()
                .verityPublicDID(verityDid.did)
                .verityPublicVerkey(verityDid.verkey)
                .walletConfig(wallet)
                .verityUrl(verityUrl)
                .build();

        Did mime = Did.createNewDid(inter.walletHandle());

        return inter.toContextBuilder()
                .sdkPairwiseDID(mime.did)
                .sdkPairwiseVerkey(mime.verkey)
                .build();
    }

    private Map<String, String> elements = new HashMap<>();
    private WalletConfig walletConfig;
    private Wallet walletHandle = null;

    private final String verityUrl = "verityUrl";
    private final String verityPublicDID = "verityPublicDID";
    private final String verityPublicVerkey = "verityPublicVerkey";
    private final String verityPairwiseDID = "verityPairwiseDID";
    private final String verityPairwiseVerkey = "verityPairwiseVerkey";
    private final String sdkPairwiseDID = "sdkPairwiseDID";
    private final String sdkPairwiseVerkey = "sdkPairwiseVerkey";
    private final String endpointUrl = "endpointUrl";

    private ContextBuilder() {}

    private ContextBuilder putElement(String key, String val) {
        elements.put(key, val);
        return this;
    }

    private ContextBuilder putElementIgnoreNull(String key, String val) {
        if(val != null) {
            return putElement(key, val);
        }
        else return this;
    }

    public ContextBuilder json(JSONObject json){
        WalletConfig w = DefaultWalletConfig.build(
                json.getString("walletName"),
                json.getString("walletKey"),
                json.optString("walletPath", null)
        );
        this.walletConfig(w);
        putElementIgnoreNull(verityUrl, json.optString(verityUrl));
        putElementIgnoreNull(verityPublicDID, json.optString(verityPublicDID));
        putElementIgnoreNull(verityPublicVerkey, json.optString(verityPublicVerkey));
        putElementIgnoreNull(verityPairwiseDID, json.optString(verityPairwiseDID));
        putElementIgnoreNull(verityPairwiseVerkey, json.optString(verityPairwiseVerkey));
        putElementIgnoreNull(sdkPairwiseDID, json.optString(sdkPairwiseDID));
        putElementIgnoreNull(sdkPairwiseVerkey, json.optString(sdkPairwiseVerkey));
        putElementIgnoreNull(endpointUrl, json.optString(endpointUrl));

        return this;
    }

    public ContextBuilder json(String json) {return json(new JSONObject(json));}

    public ContextBuilder walletConfig(WalletConfig config) {walletConfig = config; return this;}
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
                    walletConfig,
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
                    walletConfig,
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


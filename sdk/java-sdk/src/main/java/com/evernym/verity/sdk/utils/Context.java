package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletCloseException;
import com.evernym.verity.sdk.exceptions.WalletClosedException;
import com.evernym.verity.sdk.exceptions.WalletOpenException;
import com.evernym.verity.sdk.wallet.WalletConfig;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * An object used to hold the wallet handle and other configuration information. 
 * An instance if this object is passed around to many different API calls.
 */
public final class Context implements AsJsonObject{
    final private WalletConfig walletConfig;
    final private String verityUrl;
    final private String verityPublicDID;
    final private String verityPublicVerkey;
    final private String verityPairwiseDID;
    final private String verityPairwiseVerkey;
    final private String sdkPairwiseDID;
    final private String sdkPairwiseVerkey;
    final private String endpointUrl;
    final private Wallet walletHandle;


    private boolean walletClosedFlag = false;

    Context( // Not a public constructor! Allows work with ContextBuilder
        WalletConfig walletConfig,
        String verityUrl,
        String verityPublicDID,
        String verityPublicVerkey,
        String verityPairwiseDID,
        String verityPairwiseVerkey,
        String sdkPairwiseDID,
        String sdkPairwiseVerkey,
        String endpointUrl
    ) throws WalletOpenException {
        this.walletConfig = walletConfig;
        this.verityUrl = verityUrl;
        this.verityPublicDID = verityPublicDID;
        this.verityPublicVerkey = verityPublicVerkey;
        this.verityPairwiseDID = verityPairwiseDID;
        this.verityPairwiseVerkey = verityPairwiseVerkey;
        this.sdkPairwiseDID = sdkPairwiseDID;
        this.sdkPairwiseVerkey = sdkPairwiseVerkey;
        this.endpointUrl = endpointUrl;
        this.walletHandle = openWallet();
    }

    Context( // Not a public constructor! Allows work with ContextBuilder
            WalletConfig walletConfig,
            String verityUrl,
            String verityPublicDID,
            String verityPublicVerkey,
            String verityPairwiseDID,
            String verityPairwiseVerkey,
            String sdkPairwiseDID,
            String sdkPairwiseVerkey,
            String endpointUrl,
            Wallet handle
    ) throws WalletOpenException {
        if (handle == null) {
            throw new WalletOpenException("Context can not be constructed without wallet handle");
        }

        this.walletConfig = walletConfig;
        this.verityUrl = verityUrl;
        this.verityPublicDID = verityPublicDID;
        this.verityPublicVerkey = verityPublicVerkey;
        this.verityPairwiseDID = verityPairwiseDID;
        this.verityPairwiseVerkey = verityPairwiseVerkey;
        this.sdkPairwiseDID = sdkPairwiseDID;
        this.sdkPairwiseVerkey = sdkPairwiseVerkey;
        this.endpointUrl = endpointUrl;
        this.walletHandle = handle;
    }



    private Wallet openWallet() throws WalletOpenException, JSONException {
        if (walletConfig == null) {
            throw new WalletOpenException("Unable to open wallet without wallet configuration.");
        }

        try {
            return Wallet.openWallet(walletConfig.config(), walletConfig.credential()).get();
        }
        catch (IndyException | ExecutionException | InterruptedException e){
            throw new WalletOpenException(e);
        }
    }

    /**
     * Closes the wallet handle stored inside the Context object.
     *
     * @throws WalletCloseException
     */
    public void closeWallet() throws WalletCloseException {
        walletClosedFlag = true;
        try {
            walletHandle.closeWallet().get();
        }
        catch (IndyException | ExecutionException | InterruptedException e){
            throw new WalletCloseException(e);
        }
    }

    private <T> T throwIfNull(T val, String fieldName) throws UndefinedContextException {
        if(val == null) {
            throw new UndefinedContextException(
                    String.format("Context field is used without definition -- %s", fieldName)
            );
        }
        return val;
    }

    public WalletConfig walletConfig() throws UndefinedContextException {
        return throwIfNull(walletConfig, "walletConfig");
    }

    public String verityUrl() throws UndefinedContextException {
        return throwIfNull(verityUrl, "verityUrl");
    }

    public String verityPublicDID() throws UndefinedContextException {
        return throwIfNull(verityPublicDID, "verityPublicDID");
    }

    public String verityPublicVerkey() throws UndefinedContextException {
        return throwIfNull(verityPublicVerkey, "verityPublicVerkey");
    }

    public String verityPairwiseDID() throws UndefinedContextException {
        return throwIfNull(verityPairwiseDID, "verityPairwiseDID");
    }

    public String verityPairwiseVerkey() throws UndefinedContextException {
        return throwIfNull(verityPairwiseVerkey, "verityPairwiseVerkey");
    }

    public String sdkPairwiseDID() throws UndefinedContextException {
        return throwIfNull(sdkPairwiseDID, "sdkPairwiseDID");
    }

    public String sdkPairwiseVerkey() throws UndefinedContextException {
        return throwIfNull(sdkPairwiseVerkey, "sdkPairwiseVerkey");
    }

    public String endpointUrl() throws UndefinedContextException {
        return throwIfNull(endpointUrl, "endpointUrl");
    }

    public Wallet walletHandle() throws WalletClosedException {
        if (walletClosedFlag) {
            throw new WalletClosedException();
        }
        return walletHandle;
    }

    public boolean walletIsClosed() {
        return walletClosedFlag;
    }

    public ContextBuilder toContextBuilder() {
        ContextBuilder rtn = new ContextBuilder();
        if(walletConfig != null) rtn.walletConfig(walletConfig);
        if(verityUrl != null) rtn.verityUrl(verityUrl);
        if(verityPublicDID != null) rtn.verityPublicDID(verityPublicDID);
        if(verityPublicVerkey != null) rtn.verityPublicVerkey(verityPublicVerkey);
        if(verityPairwiseDID != null) rtn.verityPairwiseDID(verityPairwiseDID);
        if(verityPairwiseVerkey != null) rtn.verityPairwiseVerkey(verityPairwiseVerkey);
        if(sdkPairwiseDID != null) rtn.sdkPairwiseDID(sdkPairwiseDID);
        if(sdkPairwiseVerkey != null) rtn.sdkPairwiseVerkey(sdkPairwiseVerkey);
        if(endpointUrl != null) rtn.endpointUrl(endpointUrl);

        if (!walletClosedFlag) {
            rtn.walletHandle(walletHandle);
        }

        return rtn;
    }

    @Override
    public JSONObject toJson() {
        JSONObject rtn = new JSONObject();

        if(walletConfig != null) walletConfig.addToJson(rtn);

        if(verityUrl != null) rtn.put("verityUrl", verityUrl);
        if(verityPublicDID != null) rtn.put("verityPublicDID", verityPublicDID);
        if(verityPublicVerkey != null) rtn.put("verityPublicVerkey", verityPublicVerkey);
        if(verityPairwiseDID != null) rtn.put("verityPairwiseDID", verityPairwiseDID);
        if(verityPairwiseVerkey != null) rtn.put("verityPairwiseVerkey", verityPairwiseVerkey);
        if(sdkPairwiseDID != null) rtn.put("sdkPairwiseDID", sdkPairwiseDID);
        if(sdkPairwiseVerkey != null) rtn.put("sdkPairwiseVerkey", sdkPairwiseVerkey);
        if(endpointUrl != null) rtn.put("endpointUrl", endpointUrl);

        return rtn;
    }
}
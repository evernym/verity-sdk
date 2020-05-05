package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.exceptions.*;
import com.evernym.verity.sdk.wallet.WalletConfig;
import org.bitcoinj.core.Base58;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.crypto.Crypto;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import static com.evernym.verity.sdk.utils.ContextConstants.*;

/**
 * An object used to hold the wallet handle and other configuration information. 
 * An instance if this object is passed around to many different API calls.
 */
public final class Context implements AsJsonObject{
    final private String version;

    final private WalletConfig walletConfig;
    final private Wallet walletHandle;
    final private String endpointUrl;

    final private String verityUrl;
    final private String verityPublicDID;
    final private String verityPublicVerKey;
    final private String domainDID;
    final private String verityAgentVerKey;
    final private String sdkVerKeyId;
    final private String sdkVerKey;

    private boolean walletClosedFlag = false;

    Context( // Not a public constructor! Allows work with ContextBuilder
        WalletConfig walletConfig,
        String version,
        String verityUrl,
        String verityPublicDID,
        String verityPublicVerKey,
        String domainDID,
        String verityAgentVerKey,
        String sdkVerKeyId,
        String sdkVerKey,
        String endpointUrl
    ) throws WalletOpenException {
        this.walletConfig = walletConfig;
        this.version = version;
        this.verityUrl = verityUrl;
        this.verityPublicDID = verityPublicDID;
        this.verityPublicVerKey = verityPublicVerKey;
        this.domainDID = domainDID;
        this.verityAgentVerKey = verityAgentVerKey;
        this.sdkVerKeyId = sdkVerKeyId;
        this.sdkVerKey = sdkVerKey;
        this.endpointUrl = endpointUrl;
        this.walletHandle = openWallet();
    }

    Context( // Not a public constructor! Allows work with ContextBuilder
            WalletConfig walletConfig,
            String version,
            String verityUrl,
            String verityPublicDID,
            String verityPublicVerKey,
            String domainDID,
            String verityAgentVerKey,
            String sdkVerKeyId,
            String sdkVerKey,
            String endpointUrl,
            Wallet handle
    ) throws WalletOpenException {
        if (handle == null) {
            throw new WalletOpenException("Context can not be constructed without wallet handle");
        }

        this.walletConfig = walletConfig;
        this.version = version;
        this.verityUrl = verityUrl;
        this.verityPublicDID = verityPublicDID;
        this.verityPublicVerKey = verityPublicVerKey;
        this.domainDID = domainDID;
        this.verityAgentVerKey = verityAgentVerKey;
        this.sdkVerKeyId = sdkVerKeyId;
        this.sdkVerKey = sdkVerKey;
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
     * @throws WalletCloseException when failing to close the wallet
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
        return throwIfNull(walletConfig, WALLET_CONFIG);
    }

    public String verityUrl() throws UndefinedContextException {
        return throwIfNull(verityUrl, VERITY_URL);
    }

    public String verityPublicDID() throws UndefinedContextException {
        return throwIfNull(verityPublicDID, VERITY_PUBLIC_DID);
    }

    public String verityPublicVerKey() throws UndefinedContextException {
        return throwIfNull(verityPublicVerKey, VERITY_PUBLIC_VER_KEY);
    }

    public String domainDID() throws UndefinedContextException {
        return throwIfNull(domainDID, DOMAIN_DID);
    }

    public String verityAgentVerKey() throws UndefinedContextException {
        return throwIfNull(verityAgentVerKey, VERITY_AGENT_VER_KEY);
    }

    public String sdkVerKeyId() throws UndefinedContextException {
        return throwIfNull(sdkVerKeyId, SDK_VER_KEY_ID);
    }

    public String sdkVerKey() throws UndefinedContextException {
        return throwIfNull(sdkVerKey, SDK_VER_KEY);
    }

    public String endpointUrl() throws UndefinedContextException {
        return throwIfNull(endpointUrl, ENDPOINT_URL);
    }

    public String version() throws UndefinedContextException {
        return throwIfNull(version, VERSION);
    }

    public String restApiToken() throws VerityException, IndyException {
        try {
            String verkey = sdkVerKey();
            byte[] signature = Crypto.cryptoSign(
                    walletHandle(),
                    verkey,
                    verkey.getBytes(StandardCharsets.UTF_8)
            ).get();
            return verkey +":" + Base58.encode(signature);
        } catch (InterruptedException | ExecutionException e) {
            if(e.getCause() instanceof IndyException) throw (IndyException) e.getCause();
            else {
                throw new VerityException("Signing verkey did not complete", e);
            }
        }
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
        ContextBuilder rtn = ContextBuilder.blank();
        if(walletConfig != null) rtn.walletConfig(walletConfig);
        if(verityUrl != null) rtn.verityUrl(verityUrl);
        if(verityPublicDID != null) rtn.verityPublicDID(verityPublicDID);
        if(verityPublicVerKey != null) rtn.verityPublicVerKey(verityPublicVerKey);
        if(domainDID != null) rtn.domainDID(domainDID);
        if(verityAgentVerKey != null) rtn.verityAgentVerKey(verityAgentVerKey);
        if(sdkVerKeyId != null) rtn.sdkVerKeyId(sdkVerKeyId);
        if(sdkVerKey != null) rtn.sdkVerKey(sdkVerKey);
        if(endpointUrl != null) rtn.endpointUrl(endpointUrl);

        if (!walletClosedFlag) {
            rtn.walletHandle(walletHandle);
        }

        return rtn;
    }

    @Override
    public JSONObject toJson() {
        JSONObject rtn = new JSONObject();

        if(version != null) rtn.put(VERSION, version);
        if(walletConfig != null) walletConfig.addToJson(rtn);
        if(endpointUrl != null) rtn.put(ENDPOINT_URL, endpointUrl);
        if(verityUrl != null) rtn.put(VERITY_URL, verityUrl);
        if(verityPublicDID != null) rtn.put(VERITY_PUBLIC_DID, verityPublicDID);
        if(verityPublicVerKey != null) rtn.put(VERITY_PUBLIC_VER_KEY, verityPublicVerKey);
        if(domainDID != null) rtn.put(DOMAIN_DID, domainDID);
        if(verityAgentVerKey != null) rtn.put(VERITY_AGENT_VER_KEY, verityAgentVerKey);
        if(sdkVerKeyId != null) rtn.put(SDK_VER_KEY_ID, sdkVerKeyId);
        if(sdkVerKey != null) rtn.put(SDK_VER_KEY, sdkVerKey);
        rtn.put(VERSION, V_0_2);

        return rtn;
    }
}
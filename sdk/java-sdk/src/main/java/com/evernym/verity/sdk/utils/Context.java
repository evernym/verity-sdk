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
 * This Context object holds the data for accessing an agent on a verity-application. A complete and correct data in
 * the context allows for access and authentication to that agent.
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
            throw new WalletOpenException("Wallet failed to open", e);
        }
    }

    /**
     * Closes the the open wallet handle stored inside the Context object.
     *
     * @throws WalletException when failing to close the wallet
     */
    public void closeWallet() throws WalletException {
        walletClosedFlag = true;
        try {
            walletHandle.closeWallet().get();
        }
        catch (IndyException | ExecutionException | InterruptedException e){
            throw new WalletException("Wallet failed to close", e);
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

    /**
     * The WalletConfig expressed by this object
     * @return the WalletConfig held
     * @throws UndefinedContextException when this field is not defined
     */
    public WalletConfig walletConfig() throws UndefinedContextException {
        return throwIfNull(walletConfig, WALLET_CONFIG);
    }

    /**
     * The url for the verity-application that this object is connected to
     * @return the url for the verity-application
     * @throws UndefinedContextException when this field is not defined
     */
    public String verityUrl() throws UndefinedContextException {
        return throwIfNull(verityUrl, VERITY_URL);
    }

    /**
     * The public identifier for the verity-application. This identifier is unique for each verity-application instance
     * but is common for all agents on that instance.
     * @return the public identifier (DID) for a verity-application instance
     * @throws UndefinedContextException when this field is not defined
     */
    public String verityPublicDID() throws UndefinedContextException {
        return throwIfNull(verityPublicDID, VERITY_PUBLIC_DID);
    }

    /**
     * The public verkey for the verity-application. This verkey is unique for each verity-application instance
     * but is common for all agents on that instance.
     * @return the public verkey for a verity-application instance
     * @throws UndefinedContextException when this field is not defined
     */
    public String verityPublicVerKey() throws UndefinedContextException {
        return throwIfNull(verityPublicVerKey, VERITY_PUBLIC_VER_KEY);
    }

    /**
     * The identifier for a identity domain. This identifier identifies a self-sovereign Identity. It will be common
     * across all agents and controllers of that Identity.
     * @return the Domain identifier (DID)
     * @throws UndefinedContextException when this field is not defined
     */
    public String domainDID() throws UndefinedContextException {
        return throwIfNull(domainDID, DOMAIN_DID);
    }

    /**
     * The verkey for the agent on the verity-application
     * @return the verkey for the verity-application agent
     * @throws UndefinedContextException  when this field is not defined
     */
    public String verityAgentVerKey() throws UndefinedContextException {
        return throwIfNull(verityAgentVerKey, VERITY_AGENT_VER_KEY);
    }

    /**
     * An id for the locally held sdk verkey
     * @return the id for the sdk verkey
     * @throws UndefinedContextException when this field is not defined
     */
    public String sdkVerKeyId() throws UndefinedContextException {
        return throwIfNull(sdkVerKeyId, SDK_VER_KEY_ID);
    }

    /**
     * The verkey for the locally held key-pair. A corresponding private key is held in the wallet for this verkey
     * @return the verkey for the sdk
     * @throws UndefinedContextException when this field is not defined
     */
    public String sdkVerKey() throws UndefinedContextException {
        return throwIfNull(sdkVerKey, SDK_VER_KEY);
    }

    /**
     * The endpoint for receiving messages from the agent on the verity-application. This endpoint must be registered
     * using the UpdateEndpoint protocol to effectively change it.
     * @return the endpoint contained in this context
     * @throws UndefinedContextException when this field is not defined
     */
    public String endpointUrl() throws UndefinedContextException {
        return throwIfNull(endpointUrl, ENDPOINT_URL);
    }

    /**
     * The context version
     * @return the version
     * @throws UndefinedContextException when this field is not defined
     */
    public String version() throws UndefinedContextException {
        return throwIfNull(version, VERSION);
    }

    /**
     * Converts the local keys held in the context to REST api token. This token can be used with the REST API for the
     * verity-application
     * @return a REST API token
     * @throws VerityException when wallet operations fails
     * @throws IndyException when the signature fails
     */
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
                throw new WalletException("Signing REST API key could not complete", e);
            }
        }
    }

    /**
     * The open wallet handle used by this object.
     * @return a wallet handle
     * @throws WalletClosedException when the wallet is closed and there is not handle available
     */
    public Wallet walletHandle() throws WalletClosedException {
        if (walletClosedFlag) {
            throw new WalletClosedException();
        }
        return walletHandle;
    }

    /**
     * Whether the wallet is closed or not
     * @return true if the wallet is closed, otherwise false
     */
    public boolean walletIsClosed() {
        return walletClosedFlag;
    }

    /**
     * Builds a ContextBuilder based on this context. Since Context is immutable, this allows for changes by
     * building a new Context using a ContextBuilder
     * @return a ContextBuilder based on this context
     */
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

    /**
     * Converts this object into a JSON object
     * @return an JSON object based on this context
     */
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
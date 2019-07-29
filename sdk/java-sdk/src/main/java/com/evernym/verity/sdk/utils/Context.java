package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletCloseException;
import com.evernym.verity.sdk.exceptions.WalletClosedException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.exceptions.WalletOpenException;
import com.evernym.verity.sdk.transports.HTTPTransport;
import com.evernym.verity.sdk.transports.Transport;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * An object used to hold the wallet handle and other configuration information. 
 * An instance if this object is passed around to many different API calls. 
 * It should be initialized the config output by the tools/provision_sdk.py script.
 */
public final class Context {
    final private String walletName;
    final private String walletKey;
    final private String verityUrl;
    final private String verityPublicDID;
    final private String verityPublicVerkey;
    final private String verityPairwiseDID;
    final private String verityPairwiseVerkey;
    final private String sdkPairwiseDID;
    final private String sdkPairwiseVerkey;
    final private String webhookUrl;
    final private Wallet walletHandle;


    private boolean walletClosedFlag = false;

    Context(
        String walletName,
        String walletKey,
        String verityUrl,
        String verityPublicDID,
        String verityPublicVerkey,
        String verityPairwiseDID,
        String verityPairwiseVerkey,
        String sdkPairwiseDID,
        String sdkPairwiseVerkey,
        String webhookUrl
    ) throws WalletOpenException {
        this.walletName = walletName;
        this.walletKey = walletKey;
        this.verityUrl = verityUrl;
        this.verityPublicDID = verityPublicDID;
        this.verityPublicVerkey = verityPublicVerkey;
        this.verityPairwiseDID = verityPairwiseDID;
        this.verityPairwiseVerkey = verityPairwiseVerkey;
        this.sdkPairwiseDID = sdkPairwiseDID;
        this.sdkPairwiseVerkey = sdkPairwiseVerkey;
        this.webhookUrl = webhookUrl;
        this.walletHandle = openWallet();
    }

    Context( // Not a public constructor! Allows work with ContextBuilder
            String walletName,
            String walletKey,
            String verityUrl,
            String verityPublicDID,
            String verityPublicVerkey,
            String verityPairwiseDID,
            String verityPairwiseVerkey,
            String sdkPairwiseDID,
            String sdkPairwiseVerkey,
            String webhookUrl,
            Wallet handle
    ) throws WalletOpenException {
        if (handle == null) {
            throw new WalletOpenException("Context can not be constructed without wallet handle");
        }

        this.walletName = walletName;
        this.walletKey = walletKey;
        this.verityUrl = verityUrl;
        this.verityPublicDID = verityPublicDID;
        this.verityPublicVerkey = verityPublicVerkey;
        this.verityPairwiseDID = verityPairwiseDID;
        this.verityPairwiseVerkey = verityPairwiseVerkey;
        this.sdkPairwiseDID = sdkPairwiseDID;
        this.sdkPairwiseVerkey = sdkPairwiseVerkey;
        this.webhookUrl = webhookUrl;
        this.walletHandle = handle;
    }

    /**
     * Initialize the Context object
     * @param configJson the config output by the tools/provision_sdk.py script
     * @throws InterruptedException when the wallet does not exist or Indy is unable to open it.
     * @throws ExecutionException when the wallet does not exist or Indy is unable to open it.
     * @throws IndyException when the wallet does not exist or Indy is unable to open it.
     */
    public Context(String configJson) throws WalletOpenException {
        // TODO: Validate config
        JSONObject config = new JSONObject(configJson);
        this.walletName = config.getString("walletName");
        this.walletKey = config.getString("walletKey");
        this.verityUrl = config.getString("verityUrl");
        this.verityPublicDID = config.getString("verityPublicDID");
        this.verityPublicVerkey = config.getString("verityPublicVerkey");
        this.verityPairwiseDID = config.getString("verityPairwiseDID");
        this.verityPairwiseVerkey = config.getString("verityPairwiseVerkey");
        this.sdkPairwiseDID = config.getString("sdkPairwiseDID");
        this.sdkPairwiseVerkey = config.getString("sdkPairwiseVerkey");
        this.webhookUrl = config.getString("webhookUrl");
        this.walletHandle = openWallet();
    }

    /**
     * Builds and encrypts the message to let Verity know what the SDK's endpoint is
     * @return the encrypted message, ready to be POSTed to the agency endpoint
     * @throws WalletException when there are issues with encryption and decryption
     * @throws UndefinedContextException when the context don't have enough information for this operation
     */
    public byte[] getUpdateWebhookMessage() throws WalletException, UndefinedContextException {
        /*
            {
                "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/common/0.1/update_com_method",
                "@id": <uuid>,
                "comMethod": {
                    "id": "webhook",
                    "type": "webhook"
                    "value": <new webhook>
                }
            }
        */
        JSONObject message = new JSONObject();
        message.put("@type", "did:sov:123456789abcdefghi1234;spec/configs/0.6/UPDATE_COM_METHOD");
        message.put("@id", UUID.randomUUID().toString());
        JSONObject comMethod = new JSONObject();
        comMethod.put("id", "webhook");
        comMethod.put("type", 2);
        comMethod.put("value", this.webhookUrl);
        message.put("comMethod", comMethod);
        return MessagePackaging.packMessageForVerity(this, message.toString());
    }

    /**
     * Sends a message to Verity to let it know what the SDK's endpoint is.
     * @param context
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws WalletException when there are issues with encryption and decryption
     * @throws UndefinedContextException when the context don't have enough information for this operation
     */
    public void sendUpdateWebhookMessage(Context context) throws IOException, UndefinedContextException, WalletException {
        // Later we can switch on transport type
        Transport transport = new HTTPTransport();
        transport.sendMessage(context.verityUrl(), getUpdateWebhookMessage());
    }

    private Wallet openWallet() throws WalletOpenException {
        String walletConfig = new JSONObject().put("id", walletName).toString();
        String walletCredentials = new JSONObject().put("key", walletKey).toString();
        try {
            return Wallet.openWallet(walletConfig, walletCredentials).get();
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

    private String throwIfNull(String val, String fieldName) throws UndefinedContextException {
        if(val == null) {
            throw new UndefinedContextException(
                    String.format("Context field is used without definition -- %s", fieldName)
            );
        }
        return val;
    }

    public String walletName() throws UndefinedContextException {
        return throwIfNull(walletName, "walletName");
    }

    public String walletKey() throws UndefinedContextException {
        return throwIfNull(walletKey, "walletName");
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

    public String webhookUrl() throws UndefinedContextException {
        return throwIfNull(webhookUrl, "webhookUrl");
    }

    public Wallet walletHandle() throws WalletClosedException {
        if (walletClosedFlag) {
            throw new WalletClosedException();
        }
        return walletHandle;
    }

    public ContextBuilder toContextBuilder() {
        ContextBuilder rtn = new ContextBuilder();
        if(walletName != null) rtn.walletName(walletName);
        if(walletKey != null) rtn.walletKey(walletKey);
        if(verityUrl != null) rtn.verityUrl(verityUrl);
        if(verityPublicDID != null) rtn.verityPublicDID(verityPublicDID);
        if(verityPublicVerkey != null) rtn.verityPublicVerkey(verityPublicVerkey);
        if(verityPairwiseDID != null) rtn.verityPairwiseDID(verityPairwiseDID);
        if(verityPairwiseVerkey != null) rtn.verityPairwiseVerkey(verityPairwiseVerkey);
        if(sdkPairwiseDID != null) rtn.sdkPairwiseDID(sdkPairwiseDID);
        if(sdkPairwiseVerkey != null) rtn.sdkPairwiseVerkey(sdkPairwiseVerkey);
        if(webhookUrl != null) rtn.webhookUrl(webhookUrl);

        if (!walletClosedFlag) {
            rtn.walletHandle(walletHandle);
        }

        return rtn;
    }
}
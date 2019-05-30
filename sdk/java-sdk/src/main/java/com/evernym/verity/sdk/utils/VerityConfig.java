package com.evernym.verity.sdk.utils;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.transports.HTTPTransport;
import com.evernym.verity.sdk.transports.Transport;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.wallet.*;
import org.json.JSONObject;

/**
 * An object used to hold the wallet handle and other configuration information. 
 * An instance if this object is passed around to many different API calls. 
 * It should be initialized the config output by the tools/provision_sdk.py script.
 */
public class VerityConfig {
    protected String walletName;
    protected String walletKey;
    protected String verityUrl;
    protected String verityPublicVerkey;
    protected String verityPairwiseVerkey;
    protected String sdkPairwiseVerkey;
    protected String webhookUrl;
    protected Wallet walletHandle;

    /**
     * Initialize the VerityConfig object
     * @param configJson the config output by the tools/provision_sdk.py script
     * @throws InterruptedException when the wallet does not exist or Indy is unable to open it.
     * @throws ExecutionException when the wallet does not exist or Indy is unable to open it.
     * @throws IndyException when the wallet does not exist or Indy is unable to open it.
     */
    public VerityConfig(String configJson) throws InterruptedException, ExecutionException, IndyException {
        // TODO: Validate config
        JSONObject config = new JSONObject(configJson);
        this.walletName = config.getString("walletName");
        this.walletKey = config.getString("walletKey");
        this.verityUrl = config.getString("verityUrl");
        this.verityPublicVerkey = config.getString("verityPublicVerkey");
        this.verityPairwiseVerkey = config.getString("verityPairwiseVerkey");
        this.sdkPairwiseVerkey = config.getString("sdkPairwiseVerkey");
        this.webhookUrl = config.getString("webhookUrl");
        String walletConfig = new JSONObject().put("id", walletName).toString();
        String walletCredentials = new JSONObject().put("key", walletKey).toString();
        walletHandle = Wallet.openWallet(walletConfig, walletCredentials).get();
    }

    /**
     * Builds and encrypts the message to let Verity know what the SDK's endpoint is
     * @return the encrypted message, ready to be POSTed to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public byte[] getUpdateWebhookMessage() throws InterruptedException, ExecutionException, IndyException {
        /*
            {
                "@type": "vs.service/common/0.1/update_com_method",
                "@id": <uuid>,
                "comMethod": {
                    "id": "webhook",
                    "type": "webhook"
                    "value": <new webhook>
                }
            }
        */
        JSONObject message = new JSONObject();
        message.put("@type", "vs.service/common/0.1/update_com_method");
        message.put("@id", UUID.randomUUID().toString());
        JSONObject comMethod = new JSONObject();
        comMethod.put("id", "webhook");
        comMethod.put("type", "webhook");
        comMethod.put("value", this.webhookUrl);
        message.put("comMethod", comMethod);
        return MessagePackaging.packMessageForVerity(this, message.toString());
    }

    /**
     * Sends a message to Verity to let it know what the SDK's endpoint is.
     * @param verityConfig
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public void sendUpdateWebhookMessage(VerityConfig verityConfig) throws IOException, InterruptedException, ExecutionException, IndyException {
        // Later we can switch on transport type
        Transport transport = new HTTPTransport();
        transport.sendMessage(verityConfig.getVerityUrl(), getUpdateWebhookMessage());
    }

    /**
     * Closes the wallet handle stored inside the VerityConfig object.
     * @throws InterruptedException when there are errors closing the wallet
     * @throws ExecutionException when there are errors closing the wallet
     * @throws IndyException when there are errors closing the wallet
     */
    public void closeWallet() throws InterruptedException, ExecutionException, IndyException {
        walletHandle.closeWallet().get();
    }

    public String getVerityUrl() {
        return verityUrl;
    }

    public String getVerityPublicVerkey() {
        return verityPublicVerkey;
    }

    public String getVerityPairwiseVerkey() {
        return verityPairwiseVerkey;
    }

    public String getSdkPairwiseVerkey() {
        return sdkPairwiseVerkey;
    }

    public Wallet getWalletHandle() {
        return walletHandle;
    }
}
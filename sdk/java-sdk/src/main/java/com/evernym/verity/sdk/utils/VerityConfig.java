package com.evernym.verity.sdk.utils;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.wallet.*;
import org.json.JSONObject;

public class VerityConfig {
    protected String walletName;
    protected String walletKey;
    protected String agencyUrl;
    protected String agencyPublicVerkey;
    protected String agencyPairwiseVerkey;
    protected String sdkPairwiseVerkey;
    protected String webhookUrl;
    protected Wallet walletHandle;

    public VerityConfig(String configJson) throws InterruptedException, ExecutionException, IndyException {
        // TODO: Validate config
        JSONObject config = new JSONObject(configJson);
        this.walletName = config.getString("walletName");
        this.walletKey = config.getString("walletKey");
        this.agencyUrl = config.getString("agencyUrl");
        this.agencyPublicVerkey = config.getString("agencyPublicVerkey");
        this.agencyPairwiseVerkey = config.getString("agencyPairwiseVerkey");
        this.sdkPairwiseVerkey = config.getString("sdkPairwiseVerkey");
        this.webhookUrl = config.getString("webhookUrl");
        String walletConfig = new JSONObject().put("id", walletName).toString();
        String walletCredentials = new JSONObject().put("key", walletKey).toString();
        walletHandle = Wallet.openWallet(walletConfig, walletCredentials).get();
    }

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
        return MessagePackaging.packMessageForAgency(this, message.toString());
    }

    public void closeWallet() throws InterruptedException, ExecutionException, IndyException {
        walletHandle.closeWallet().get();
    }

    public String getAgencyUrl() {
        return agencyUrl;
    }

    public String getAgencyPublicVerkey() {
        return agencyPublicVerkey;
    }

    public String getAgencyPairwiseVerkey() {
        return agencyPairwiseVerkey;
    }

    public String getSdkPairwiseVerkey() {
        return sdkPairwiseVerkey;
    }

    public Wallet getWalletHandle() {
        return walletHandle;
    }
}
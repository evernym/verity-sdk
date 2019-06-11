package com.evernym.verity.sdk;

import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.utils.MessagePackaging;
import com.evernym.verity.sdk.utils.Context;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.crypto.Crypto;
import org.json.JSONObject;

public class TestHelpers {
    
    public static Context getConfig() throws InterruptedException, ExecutionException, IndyException {
        String walletName = "java_test_wallet";
        String walletKey = "12345";
        String webhookUrl = "http://localhost:3000";
        String verityUrl = "http://localhost:3000";
        TestWallet testWallet = new TestWallet(walletName, walletKey);
        JSONObject config = new JSONObject();
        config.put("walletName", walletName);
        config.put("walletKey", walletKey);
        config.put("verityUrl", verityUrl);
        config.put("verityPublicVerkey", testWallet.getVerityPublicVerkey());
        config.put("verityPairwiseVerkey", testWallet.getVerityPairwiseVerkey());
        config.put("verityPairwiseDID", testWallet.getVerityPairwiseDID());
        config.put("sdkPairwiseVerkey", testWallet.getSdkPairwiseVerkey());
        config.put("webhookUrl", webhookUrl);
        return new Context(config.toString());
    }

    public static JSONObject unpackMessage(Context context, byte[] message) throws InterruptedException, ExecutionException, IndyException {
        byte[] partiallyUnpackedMessageJWE = Crypto.unpackMessage(context.getWalletHandle(), message).get();
        String partiallyUnpackedMessage = new JSONObject(new String(partiallyUnpackedMessageJWE)).getString("message");
        return MessagePackaging.unpackForwardMsg(context, new JSONObject(partiallyUnpackedMessage).getJSONObject("@msg"));
    }
}
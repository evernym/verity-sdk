package com.evernym.verity.sdk;

import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.exceptions.WalletOpenException;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.MessagePackaging;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.crypto.Crypto;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class TestHelpers {
    
    public static Context getConfig() throws InterruptedException, ExecutionException, IndyException, WalletOpenException {
        String walletName = "java_test_wallet";
        String walletKey = "12345";
        String webhookUrl = "http://localhost:3000";
        String verityUrl = "http://localhost:3000";
        TestWallet testWallet = new TestWallet(walletName, walletKey);
        JSONObject config = new JSONObject();
        config.put("walletName", walletName);
        config.put("walletKey", walletKey);
        config.put("verityUrl", verityUrl);
        config.put("verityPublicDID", testWallet.getVerityPublicVerkey());
        config.put("verityPublicVerkey", testWallet.getVerityPublicVerkey());
        config.put("verityPairwiseVerkey", testWallet.getVerityPairwiseVerkey());
        config.put("verityPairwiseDID", testWallet.getVerityPairwiseDID());
        config.put("sdkPairwiseDID", testWallet.getSdkPairwiseDID());
        config.put("sdkPairwiseVerkey", testWallet.getSdkPairwiseVerkey());
        config.put("webhookUrl", webhookUrl);
        return new Context(config.toString());
    }

    public static JSONObject unpackMessage(Context context, byte[] message) throws WalletException {
        try {
            byte[] partiallyUnpackedMessageJWE = Crypto.unpackMessage(context.walletHandle(), message).get();
            String partiallyUnpackedMessage = new JSONObject(new String(partiallyUnpackedMessageJWE)).getString("message");
            return MessagePackaging.unpackForwardMsg(context, new JSONObject(partiallyUnpackedMessage).getJSONObject("@msg"));
        }
        catch (IndyException | InterruptedException | ExecutionException e) {
            throw new WalletException("Unable to unpack message", e);
        }
    }
}
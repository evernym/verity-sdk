package com.evernym.verity.sdk;

import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.ContextBuilder;
import com.evernym.verity.sdk.utils.TestWallet;
import com.evernym.verity.sdk.wallet.WalletConfig;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;

import java.util.UUID;

import static com.evernym.verity.sdk.utils.Util.unpackMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestHelpers {
    protected TestHelpers() {
    }

    public static Context getContext() throws WalletException {
        return getContext(null);
    }

    public static Context getContext(String seed) throws WalletException {

        String walletName = UUID.randomUUID().toString();
        String walletKey = UUID.randomUUID().toString();
        String endpointUrl = "http://localhost:3000";
        String verityUrl = "http://localhost:3000";

        TestWallet testWallet = new TestWallet(walletName, walletKey, seed);
        return ContextBuilder
                .blank()
                .walletConfig(testWallet)
                .verityUrl(verityUrl)
                .verityPublicDID(testWallet.getVerityPublicVerkey())
                .verityPublicVerKey(testWallet.getVerityPublicVerkey())
                .domainDID(testWallet.getVerityPairwiseDID())
                .verityAgentVerKey(testWallet.getVerityPairwiseVerkey())
                .sdkVerKeyId(testWallet.getSdkPairwiseDID())
                .sdkVerKey(testWallet.getSdkPairwiseVerkey())
                .endpointUrl(endpointUrl)
                .build();
    }

    public static void cleanup(Context context) throws Exception {
        if(context != null) {
            if(! context.walletIsClosed()) {
                context.closeWallet();
            }
            WalletConfig config = context.walletConfig();
            Wallet.deleteWallet(config.config(), config.credential()).get();
        }
    }

    public static void assertEqualsJSONObject(JSONObject expected, JSONObject actual) {
        assertEquals(expected.toMap().size(), actual.toMap().size());
        expected.toMap().forEach((s, o) ->
                assertEquals(actual.get(s), o)
        );
    }


    public static JSONObject unpackForwardMessage(Context context, byte[] message) throws WalletException {
        JSONObject unpackedOnceMessage = unpackMessage(context, message);
        byte[] unpackedOnceMessageMessage = unpackedOnceMessage.getJSONObject("@msg").toString().getBytes();
        return unpackMessage(context, unpackedOnceMessageMessage);
    }
}
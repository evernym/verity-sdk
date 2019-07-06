package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.TestHelpers;

import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessagePackagingTest {

    @Test
    public void packCanUnpack() throws Exception {
        try {
            Context context = TestHelpers.getConfig();
            
            JSONObject testMessage = new JSONObject().put("hello", "world");
            byte[] packedMessage = MessagePackaging.packMessageForVerity(context, testMessage.toString());

            JSONObject unpackedMessage = TestHelpers.unpackMessage(context, packedMessage);
            assertEquals(testMessage.toString(), unpackedMessage.toString());

            context.closeWallet();
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            String walletConfig = new JSONObject().put("id", "java_test_wallet").toString();
            String walletCredentials = new JSONObject().put("key", "12345").toString();
            Wallet.deleteWallet(walletConfig, walletCredentials).get();
        }
    }
}
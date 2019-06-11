package com.evernym.verity.sdk.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.evernym.verity.sdk.TestHelpers;

import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.junit.Test;

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
            assertTrue(false);
        } finally {
            String walletConfig = new JSONObject().put("id", "java_test_wallet").toString();
            String walletCredentials = new JSONObject().put("key", "12345").toString();
            Wallet.deleteWallet(walletConfig, walletCredentials).get();
        }
    }

    @Test
    public void test_ObjectToByteArray_basicUsage() {
        int[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        JSONArray jsonArray = new JSONArray(array);
        byte[] byteArray = MessagePackaging.objectToByteArray(jsonArray);
        for (int i = 0; i < jsonArray.length(); i++) {
            assertEquals(array[i], (int)byteArray[i]);
        }
    }

    @Test(expected = org.json.JSONException.class)
    public void test_ObjectToByteArray_badInput() {
        int[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        JSONArray jsonArray = new JSONArray(array);
        jsonArray.put("string");
        byte[] byteArray = MessagePackaging.objectToByteArray(jsonArray);
        for (int i = 0; i < jsonArray.length(); i++) {
            assertEquals(array[i], (int)byteArray[i]);
        }
    }

    @Test
    public void test_ObjectToByteArray_emptyArray() {
        int[] array = {};
        JSONArray jsonArray = new JSONArray(array);
        byte[] byteArray = MessagePackaging.objectToByteArray(jsonArray);
        assertEquals(byteArray.length, 0);
    }
}
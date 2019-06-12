package com.evernym.verity.sdk.protocols;

import static org.junit.Assert.assertTrue;

import static org.junit.Assert.assertEquals;

import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.TestHelpers;

import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;
import org.junit.Test;

public class ConnectionTest {

    @Test
    public void oneParamsConstructor() throws Exception {
        try {
            Context context = TestHelpers.getConfig();

            String sourceId = "source_id";
            Connection connection = new Connection(sourceId);
            JSONObject unpackedMessage = TestHelpers.unpackMessage(context, connection.getMessage(context));
            assertEquals(connection.toString(), unpackedMessage.toString());
            assertEquals(sourceId, unpackedMessage.getString("sourceId"));

            context.closeWallet();
        } catch(Exception e) {
            e.printStackTrace();
            assertTrue(false);
            throw e;
        } finally {
            String walletConfig = new JSONObject().put("id", "java_test_wallet").toString();
            String walletCredentials = new JSONObject().put("key", "12345").toString();
            Wallet.deleteWallet(walletConfig, walletCredentials).get();
        }
    }

    @Test
    public void twoParamsConstructor() throws Exception {
        try {
            Context context = TestHelpers.getConfig();

            String sourceId = "source_id";
            String phoneNumber = "123-456-7891";
            Connection connection = new Connection(sourceId, phoneNumber);
            JSONObject unpackedMessage = TestHelpers.unpackMessage(context, connection.getMessage(context));
            assertEquals(connection.toString(), unpackedMessage.toString());
            assertEquals(sourceId, unpackedMessage.getString("sourceId"));
            assertEquals(phoneNumber, unpackedMessage.getString("phoneNo"));

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
    public void threeParamsConstructor() throws Exception {
        try {
            Context context = TestHelpers.getConfig();

            String sourceId = "source_id";
            String phoneNumber = "123-456-7891";
            boolean usePublicDid = true;
            Connection connection = new Connection(sourceId, phoneNumber, usePublicDid);
            JSONObject unpackedMessage = TestHelpers.unpackMessage(context, connection.getMessage(context));
            assertEquals(connection.toString(), unpackedMessage.toString());
            assertEquals(sourceId, unpackedMessage.getString("sourceId"));
            assertEquals(phoneNumber, unpackedMessage.getString("phoneNo"));
            assertEquals(usePublicDid, unpackedMessage.getBoolean("usePublicDid"));

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
}
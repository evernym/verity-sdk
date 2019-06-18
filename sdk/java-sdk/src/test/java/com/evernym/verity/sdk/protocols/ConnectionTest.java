package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.TestHelpers;

import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConnectionTest {

    @Test
    public void oneParamsConstructor() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getConfig();

            String sourceId = "source_id";
            Connection connection = new Connection(sourceId);
            JSONObject unpackedMessage = TestHelpers.unpackMessage(context, connection.getMessage(context));
            assertEquals(connection.toString(), unpackedMessage.toString());
            assertEquals(sourceId, unpackedMessage.getString("sourceId"));
        } catch(Exception e) {
            e.printStackTrace();
            fail();
            throw e;
        } finally {
            if(context != null) {
                context.closeWallet();
            }
            String walletConfig = new JSONObject().put("id", "java_test_wallet").toString();
            String walletCredentials = new JSONObject().put("key", "12345").toString();
            Wallet.deleteWallet(walletConfig, walletCredentials).get();
        }
    }

    @Test
    public void twoParamsConstructor() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getConfig();

            String sourceId = "source_id";
            String phoneNumber = "123-456-7891";
            Connection connection = new Connection(sourceId, phoneNumber);
            JSONObject unpackedMessage = TestHelpers.unpackMessage(context, connection.getMessage(context));
            assertEquals(connection.toString(), unpackedMessage.toString());
            assertEquals(sourceId, unpackedMessage.getString("sourceId"));
            assertEquals(phoneNumber, unpackedMessage.getString("phoneNo"));
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            if(context != null) {
                context.closeWallet();
            }
            String walletConfig = new JSONObject().put("id", "java_test_wallet").toString();
            String walletCredentials = new JSONObject().put("key", "12345").toString();
            Wallet.deleteWallet(walletConfig, walletCredentials).get();
        }
    }

    @Test
    public void threeParamsConstructor() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getConfig();

            String sourceId = "source_id";
            String phoneNumber = "123-456-7891";
            boolean usePublicDid = true;
            Connection connection = new Connection(sourceId, phoneNumber, usePublicDid);
            JSONObject unpackedMessage = TestHelpers.unpackMessage(context, connection.getMessage(context));
            assertEquals(connection.toString(), unpackedMessage.toString());
            assertEquals(sourceId, unpackedMessage.getString("sourceId"));
            assertEquals(phoneNumber, unpackedMessage.getString("phoneNo"));
            assertEquals(usePublicDid, unpackedMessage.getBoolean("usePublicDid"));
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            if(context != null) {
                context.closeWallet();
            }
            String walletConfig = new JSONObject().put("id", "java_test_wallet").toString();
            String walletCredentials = new JSONObject().put("key", "12345").toString();
            Wallet.deleteWallet(walletConfig, walletCredentials).get();
        }
    }
}
package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;

import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class CredDefTest {

    @Test
    public void basicCredDefTest() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getConfig();

            String name = "cred def name";
            String schemaId = "...someSchemaId...";
            CredDef credDef = new CredDef(name, schemaId);
            JSONObject unpackedMessage = TestHelpers.unpackMessage(context, credDef.getMessage(context));
            assertEquals(credDef.toString(), unpackedMessage.toString());
            assertEquals(name, unpackedMessage.getString("name"));
            assertEquals(schemaId, unpackedMessage.getString("schemaId"));
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
    public void credDefTestWithTag() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getConfig();

            String name = "cred def name";
            String schemaId = "...someSchemaId...";
            String tag = "latest";
            CredDef credDef = new CredDef(name, schemaId, tag);
            JSONObject unpackedMessage = TestHelpers.unpackMessage(context, credDef.getMessage(context));
            assertEquals(credDef.toString(), unpackedMessage.toString());
            assertEquals(name, unpackedMessage.getString("name"));
            assertEquals(schemaId, unpackedMessage.getString("schemaId"));
            assertEquals(tag, unpackedMessage.getString("tag"));
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
    public void credDefTestWithRevocationDetails() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getConfig();

            String name = "cred def name";
            String schemaId = "...someSchemaId...";
            JSONObject revocationDetails = new JSONObject();
            revocationDetails.put("support_revocation", false);
            CredDef credDef = new CredDef(name, schemaId, revocationDetails);
            JSONObject unpackedMessage = TestHelpers.unpackMessage(context, credDef.getMessage(context));
            assertEquals(credDef.toString(), unpackedMessage.toString());
            assertEquals(name, unpackedMessage.getString("name"));
            assertEquals(schemaId, unpackedMessage.getString("schemaId"));
            assertEquals(revocationDetails.toString(), unpackedMessage.getJSONObject("revocationDetails").toString());
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
    public void credDefTestWithTagAndRevocationDetails() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getConfig();

            String name = "cred def name";
            String schemaId = "...someSchemaId...";
            String tag = "latest";
            JSONObject revocationDetails = new JSONObject();
            revocationDetails.put("support_revocation", false);
            CredDef credDef = new CredDef(name, schemaId, tag, revocationDetails);
            JSONObject unpackedMessage = TestHelpers.unpackMessage(context, credDef.getMessage(context));
            assertEquals(credDef.toString(), unpackedMessage.toString());
            assertEquals(name, unpackedMessage.getString("name"));
            assertEquals(schemaId, unpackedMessage.getString("schemaId"));
            assertEquals(tag, unpackedMessage.getString("tag"));
            assertEquals(revocationDetails.toString(), unpackedMessage.getJSONObject("revocationDetails").toString());
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
}
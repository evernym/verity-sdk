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

            String schemaId = "...someSchemaId...";
            CredDef credDef = new CredDef(schemaId);
            JSONObject unpackedMessage = TestHelpers.unpackMessage(context, credDef.getMessage(context));
            assertEquals(credDef.toString(), unpackedMessage.toString());
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
}
package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;

import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class SchemaTest {

    @Test
    public void basicSchemaTest() throws Exception {
        try {
            Context context = TestHelpers.getConfig();

            String schemaName = "test schema";
            String schemaVersion = "0.0.1";
            String attr1 = "name";
            String attr2 = "degree";
            Schema schema = new Schema(schemaName, schemaVersion, attr1, attr2);
            JSONObject unpackedMessage = TestHelpers.unpackMessage(context, schema.getMessage(context));
            assertEquals(schema.toString(), unpackedMessage.toString());
            assertEquals(attr1, unpackedMessage.getJSONObject("schema").getJSONArray("attrNames").get(0));
            assertEquals(attr2, unpackedMessage.getJSONObject("schema").getJSONArray("attrNames").get(1));

            context.closeWallet();
        } catch(Exception e) {
            e.printStackTrace();
            fail();
            throw e;
        } finally {
            String walletConfig = new JSONObject().put("id", "java_test_wallet").toString();
            String walletCredentials = new JSONObject().put("key", "12345").toString();
            Wallet.deleteWallet(walletConfig, walletCredentials).get();
        }
    }
}
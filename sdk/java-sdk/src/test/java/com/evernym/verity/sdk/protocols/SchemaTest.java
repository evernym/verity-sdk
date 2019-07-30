package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SchemaTest {

    @Test
    public void basicSchemaTest() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getConfig();

            String schemaName = "test schema";
            String schemaVersion = "0.0.1";
            String attr1 = "name";
            String attr2 = "degree";
            Schema schema = new Schema(schemaName, schemaVersion, attr1, attr2);
            JSONObject unpackedMessage = TestHelpers.unpackMessage(context, schema.getMessage(context));
            assertEquals(schema.toString(), unpackedMessage.toString());
            assertEquals(attr1, unpackedMessage.getJSONArray("attrNames").get(0));
            assertEquals(attr2, unpackedMessage.getJSONArray("attrNames").get(1));
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
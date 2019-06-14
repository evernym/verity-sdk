package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;

import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class CredentialTest {

    @Test
    public void basicCredDefTest() throws Exception {
        try {
            Context context = TestHelpers.getConfig();

            String connectionId = "...someConnectionId...";
            String credDefId = "...someCredDefId...";
            JSONObject credentialValues = new JSONObject();
            credentialValues.put("name", "Jose Smith");
            credentialValues.put("degree", "Bachelors");
            credentialValues.put("gpa", "3.67");
            int price = 3;
            Credential credential = new Credential(connectionId, credDefId, credentialValues, price);

            JSONObject unpackedCredentialMessage = TestHelpers.unpackMessage(context, credential.getMessage(context));
            assertEquals(credential.toString(), unpackedCredentialMessage.toString());
            assertEquals(connectionId, unpackedCredentialMessage.getString("connectionId"));
            assertEquals(credDefId, unpackedCredentialMessage.getJSONObject("credentialData").getString("credDefId"));
            assertEquals(credentialValues.toString(), unpackedCredentialMessage.getJSONObject("credentialData").getJSONObject("credentialValues").toString());
            assertEquals(price, unpackedCredentialMessage.getJSONObject("credentialData").getInt("price"));

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
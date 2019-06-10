package com.evernym.verity.sdk.protocols;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;

import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

public class ProofRequestTest {

    @Test
    public void properlyBuildMessage() throws Exception {
        try {
            Context context = TestHelpers.getConfig();
            
            String proofRequestName = "Name Check";
            JSONArray proofAttrs = getProofAttrs();
            String connectionId = "<some connection id>";
            ProofRequest proofRequest = new ProofRequest(proofRequestName, proofAttrs, connectionId);
            JSONObject unpackedMessage = TestHelpers.unpackMessage(context, proofRequest.getMessage(context));
            assertEquals(proofRequest.toString(), unpackedMessage.toString());
            assertEquals(proofRequestName, unpackedMessage.getJSONObject("proof").getString("name"));
            assertEquals(proofAttrs.toString(), unpackedMessage.getJSONObject("proof").getJSONArray("proofAttrs").toString());
            assertEquals(connectionId, unpackedMessage.getString("connectionId"));
            
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

    private JSONArray getProofAttrs() {
        JSONArray proofAttrs = new JSONArray();
        JSONObject proofAttr = new JSONObject();
        proofAttr.put("name", "name"); 
        JSONArray restrictions = new JSONArray();
        JSONObject restriction = new JSONObject();
        restriction.put("issuer_did", "UOISDFOPUASOFIUSAF");
        restrictions.put(restriction);
        proofAttr.put("restrictions", restrictions);
        proofAttrs.put(proofAttr);
        return proofAttrs;
    }
}
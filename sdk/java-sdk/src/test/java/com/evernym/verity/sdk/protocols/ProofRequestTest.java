package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;

import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProofRequestTest {

    @Test
    public void properlyBuildMessage() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getConfig();
            
            String proofRequestName = "Name Check";
            JSONArray proofAttrs = getProofAttrs();
            String connectionId = "<some connection id>";
            ProofRequest proofRequest = new ProofRequest(connectionId, proofRequestName, proofAttrs);
            JSONObject unpackedMessage = TestHelpers.unpackMessage(context, proofRequest.getMessage(context));
            assertEquals(proofRequest.toString(), unpackedMessage.toString());
            assertEquals(proofRequestName, unpackedMessage.getJSONObject("proofRequest").getString("name"));
            assertEquals(proofAttrs.toString(), unpackedMessage.getJSONObject("proofRequest").getJSONArray("proofAttrs").toString());
            assertEquals(connectionId, unpackedMessage.getString("connectionId"));
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
    public void properlyBuildMessageWithRevocationInterval() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getConfig();
            
            String proofRequestName = "Name Check";
            JSONArray proofAttrs = getProofAttrs();
            String connectionId = "<some connection id>";
            JSONObject revocationInterval = new JSONObject();
            ProofRequest proofRequest = new ProofRequest(connectionId, proofRequestName, proofAttrs, revocationInterval);
            JSONObject unpackedMessage = TestHelpers.unpackMessage(context, proofRequest.getMessage(context));
            assertEquals(proofRequest.toString(), unpackedMessage.toString());
            assertEquals(proofRequestName, unpackedMessage.getJSONObject("proofRequest").getString("name"));
            assertEquals(proofAttrs.toString(), unpackedMessage.getJSONObject("proofRequest").getJSONArray("proofAttrs").toString());
            assertEquals(connectionId, unpackedMessage.getString("connectionId"));
            assertEquals(revocationInterval.toString(), unpackedMessage.getJSONObject("proofRequest").getJSONObject("revocationInterval").toString());
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
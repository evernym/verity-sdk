package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;

import com.evernym.verity.sdk.utils.Util;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class PresentProofTest {

    private String connectionId = "<some connection id>";
    private String proofRequestName = "Name Check";
    private JSONArray proofAttrs = getProofAttrs();
    private JSONObject revocationInterval = new JSONObject();

    @Test
    public void testGetMessageType() {
        PresentProof presentProof = new PresentProof(connectionId, proofRequestName, proofAttrs, revocationInterval);
        String msgName = "msg name";
        assertEquals(Util.getMessageType("present-proof", "0.1", msgName), PresentProof.getMessageType(msgName));
    }

    @Test
    public void testConstructorWithoutRevocationInterval() {
        PresentProof presentProof = new PresentProof(connectionId, proofRequestName, proofAttrs);
        assertEquals(connectionId, presentProof.connectionId);
        assertEquals(proofRequestName, presentProof.name);
        assertEquals(proofAttrs.toString(), presentProof.proofAttrs.toString());
        assertNull(presentProof.revocationInterval);
        testMessages(presentProof);
    }

    @Test
    public void testFullConstructor() {
        PresentProof presentProof = new PresentProof(connectionId, proofRequestName, proofAttrs, revocationInterval);
        assertEquals(connectionId, presentProof.connectionId);
        assertEquals(proofRequestName, presentProof.name);
        assertEquals(proofAttrs.toString(), presentProof.proofAttrs.toString());
        assertEquals(revocationInterval.toString(), presentProof.revocationInterval.toString());
        testMessages(presentProof);
    }

    private void testMessages(PresentProof presentProof) {
        JSONObject msg = presentProof.messages.getJSONObject(PresentProof.PROOF_REQUEST);
        assertEquals(PresentProof.getMessageType("request"), msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertEquals(connectionId, msg.getString("connectionId"));
        assertEquals(proofRequestName, msg.getJSONObject("proofRequest").getString("name"));
        assertEquals(proofAttrs.toString(), msg.getJSONObject("proofRequest").getJSONArray("proofAttrs").toString());
        if(presentProof.revocationInterval != null)
            assertEquals(revocationInterval.toString(), msg.getJSONObject("proofRequest").getJSONObject("revocationInterval").toString());
    }

    @Test
    public void testRequest() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            PresentProof presentProof = new PresentProof(connectionId, proofRequestName, proofAttrs);
            presentProof.disableHTTPSend();
            byte [] message = presentProof.request(context);
            JSONObject unpackedMessage = Util.unpackForwardMessage(context, message);
            assertEquals(PresentProof.getMessageType(PresentProof.PROOF_REQUEST), unpackedMessage.getString("@type"));
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
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
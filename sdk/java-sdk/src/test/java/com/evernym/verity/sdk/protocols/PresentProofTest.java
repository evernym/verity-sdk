package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class PresentProofTest {

    private String forRelationship = "...someDid...";
    private String proofRequestName = "Name Check";
    private JSONArray proofAttrs = getProofAttrs();
    private JSONArray proofPredicates = getPredicates();
    private JSONObject revocationInterval = getRevocationInterval();

    @Test
    public void testGetMessageType() {
        PresentProof presentProof = new PresentProof(forRelationship, proofRequestName, proofAttrs, proofPredicates, revocationInterval);
        String msgName = "msg name";
        assertEquals(Util.getMessageType(
                Util.EVERNYM_MSG_QUALIFIER,
                "present-proof",
                "0.6",
                msgName
        ), PresentProof.getMessageType(msgName));
    }

    @Test
    public void testConstructorWithoutRevocationIntervalAndPredicates() {
        PresentProof presentProof = new PresentProof(forRelationship, proofRequestName, proofAttrs);
        assertEquals(forRelationship, presentProof.forRelationship);
        assertEquals(proofRequestName, presentProof.name);
        assertEquals(proofAttrs.toString(), presentProof.proofAttrs.toString());
        assertNull(presentProof.proofPredicates);
        assertNull(presentProof.revocationInterval);
        testMessages(presentProof);
    }

    @Test
    public void testConstructorWithoutRevocationInterval() {
        PresentProof presentProof = new PresentProof(forRelationship, proofRequestName, proofAttrs, proofPredicates);
        assertEquals(forRelationship, presentProof.forRelationship);
        assertEquals(proofRequestName, presentProof.name);
        assertEquals(proofAttrs.toString(), presentProof.proofAttrs.toString());
        assertEquals(proofPredicates.toString(), presentProof.proofPredicates.toString());
        assertNull(presentProof.revocationInterval);
        testMessages(presentProof);
    }

    @Test
    public void testFullConstructor() {
        PresentProof presentProof = new PresentProof(forRelationship, proofRequestName, proofAttrs, proofPredicates, revocationInterval);
        assertEquals(forRelationship, presentProof.forRelationship);
        assertEquals(proofRequestName, presentProof.name);
        assertEquals(proofAttrs.toString(), presentProof.proofAttrs.toString());
        assertEquals(proofPredicates.toString(), presentProof.proofPredicates.toString());
        assertEquals(revocationInterval.toString(), presentProof.revocationInterval.toString());
        testMessages(presentProof);
    }

    private void testMessages(PresentProof presentProof) {
        JSONObject requestMsg = presentProof.messages.getJSONObject(PresentProof.PROOF_REQUEST);
        assertEquals(PresentProof.getMessageType(PresentProof.PROOF_REQUEST), requestMsg.getString("@type"));
        assertNotNull(requestMsg.getString("@id"));
        assertNotNull(requestMsg.getJSONObject("~thread").getString("thid"));
        assertEquals(forRelationship, requestMsg.getString("~for_relationship"));
        assertEquals(proofRequestName, requestMsg.getString("name"));
        assertEquals(proofAttrs.toString(), requestMsg.getJSONArray("proofAttrs").toString());
        if(presentProof.proofPredicates != null)
            assertEquals(proofPredicates.toString(), requestMsg.getJSONArray("proofPredicates").toString());
        if(presentProof.revocationInterval != null)
            assertEquals(revocationInterval.toString(), requestMsg.getJSONObject("revocationInterval").toString());

        JSONObject statusMsg = presentProof.messages.getJSONObject(PresentProof.GET_STATUS);
        assertEquals(PresentProof.getMessageType(PresentProof.GET_STATUS), statusMsg.getString("@type"));
        assertNotNull(statusMsg.getString("@id"));
        assertNotNull(statusMsg.getJSONObject("~thread").getString("thid"));
        assertEquals(forRelationship, statusMsg.getString("~for_relationship"));
    }

    @Test
    public void testRequest() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            PresentProof presentProof = new PresentProof(forRelationship, proofRequestName, proofPredicates, proofAttrs);
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

    @Test
    public void testGetStatus() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            PresentProof presentProof = new PresentProof(forRelationship, proofRequestName, proofPredicates, proofAttrs);
            presentProof.disableHTTPSend();
            byte [] message = presentProof.status(context);
            JSONObject unpackedMessage = Util.unpackForwardMessage(context, message);
            assertEquals(PresentProof.getMessageType(PresentProof.GET_STATUS), unpackedMessage.getString("@type"));
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
        proofAttr.put("name", "age");

        JSONArray restrictions = new JSONArray();
        JSONObject restriction = new JSONObject();
        restriction.put("issuer_did", "UOISDFOPUASOFIUSAF");
        restrictions.put(restriction);

        proofAttr.put("restrictions", restrictions);

        proofAttrs.put(proofAttr);
        return proofAttrs;
    }

    private JSONArray getPredicates() {
        JSONArray proofPredicates = new JSONArray();

        JSONObject predicate = new JSONObject();
        predicate.put("name", "age");
        predicate.put("p_type", "GT");
        predicate.put("p_value", 18);

        JSONArray restrictions = new JSONArray();
        JSONObject restriction = new JSONObject();
        restriction.put("issuer_did", "UOISDFOPUASOFIUSAF");
        restrictions.put(restriction);

        predicate.put("restrictions", restrictions);

        proofPredicates.put(predicate);
        return proofPredicates;
    }

    private JSONObject getRevocationInterval() {
        JSONObject interval = new JSONObject();

        interval.put("from", 1000);

        return interval;
    }
}
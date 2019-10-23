package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;

import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class IssueCredentialTest {

    private String forRelationship = "...someDid...";
    private String credentialName = "Bachelors Degree";
    private String credDefId = "...someCredDefId...";
    private JSONObject credentialValues = new JSONObject();
    private String price = "3";

    public IssueCredentialTest() {
        credentialValues.put("name", "Jose Smith");
        credentialValues.put("degree", "Bachelors");
        credentialValues.put("gpa", "3.67");
    }

    @Test
    public void testGetMessageType() {
        IssueCredential issueCredential = new IssueCredential(forRelationship, credentialName, credDefId, credentialValues, price);
        String msgName = "msg name";
        assertEquals(Util.getMessageType("issue-credential", "0.6", msgName), IssueCredential.getMessageType(msgName));
    }

    @Test
    public void testConstructor() {
        IssueCredential issueCredential = new IssueCredential(forRelationship, credentialName, credDefId, credentialValues, price);
        assertEquals(forRelationship, issueCredential.forRelationship);
        assertEquals(credentialName, issueCredential.credentialName);
        assertEquals(credDefId, issueCredential.credDefId);
        assertEquals(credentialValues.toString(), issueCredential.credentialValues.toString());
        assert price == issueCredential.price;
        testMessages(issueCredential);
    }

    private void testMessages(IssueCredential issueCredential) {
        JSONObject offerMsg = issueCredential.messages.getJSONObject(IssueCredential.OFFER_CREDENTIAL);
        assertEquals(IssueCredential.getMessageType(IssueCredential.OFFER_CREDENTIAL), offerMsg.getString("@type"));
        assertNotNull(offerMsg.getString("@id"));
        assertNotNull(offerMsg.getJSONObject("~thread").getString("thid"));
        assertEquals(forRelationship, offerMsg.getString("~for_relationship"));
        assertEquals(credentialName, offerMsg.getString("name"));
        assertEquals(credDefId, offerMsg.getString("credDefId"));
        assertEquals(credentialValues.toString(), offerMsg.getJSONObject("credentialValues").toString());
        assertEquals(price, offerMsg.getString("price"));

        JSONObject issueMsg = issueCredential.messages.getJSONObject(IssueCredential.ISSUE_CREDENTIAL);
        assertEquals(IssueCredential.getMessageType(IssueCredential.ISSUE_CREDENTIAL), issueMsg.getString("@type"));
        assertNotNull(issueMsg.getString("@id"));
        assertNotNull(issueMsg.getJSONObject("~thread").getString("thid"));
        assertEquals(forRelationship, issueMsg.getString("~for_relationship"));

        JSONObject statusMsg = issueCredential.messages.getJSONObject(IssueCredential.GET_STATUS);
        assertEquals(IssueCredential.getMessageType(IssueCredential.GET_STATUS), statusMsg.getString("@type"));
        assertNotNull(statusMsg.getString("@id"));
        assertNotNull(statusMsg.getJSONObject("~thread").getString("thid"));
        assertEquals(forRelationship, statusMsg.getString("~for_relationship"));
    }

    @Test
    public void testIssue() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            IssueCredential issueCredential = new IssueCredential(forRelationship, credentialName, credDefId, credentialValues, price);
            issueCredential.disableHTTPSend();
            byte [] offerMsg = issueCredential.offerCredential(context);
            JSONObject unpackedOfferMessage = Util.unpackForwardMessage(context, offerMsg);
            assertEquals(IssueCredential.getMessageType(IssueCredential.OFFER_CREDENTIAL), unpackedOfferMessage.getString("@type"));
            byte [] issueMsg = issueCredential.issueCredential(context);
            JSONObject unpackedIssueMessage = Util.unpackForwardMessage(context, issueMsg);
            assertEquals(IssueCredential.getMessageType(IssueCredential.ISSUE_CREDENTIAL), unpackedIssueMessage.getString("@type"));
            byte [] statusMsg = issueCredential.status(context);
            JSONObject unpackedStatusMessage = Util.unpackForwardMessage(context, statusMsg);
            assertEquals(IssueCredential.getMessageType(IssueCredential.GET_STATUS), unpackedStatusMessage.getString("@type"));
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }
}
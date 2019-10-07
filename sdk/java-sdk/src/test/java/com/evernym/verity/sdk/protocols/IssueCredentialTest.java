package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;

import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class IssueCredentialTest {

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
        IssueCredential issueCredential = new IssueCredential(credentialName, credDefId, credentialValues, price);
        String msgName = "msg name";
        assertEquals(Util.getMessageType("issue-credential", "0.6", msgName), IssueCredential.getMessageType(msgName));
    }

    @Test
    public void testConstructor() {
        IssueCredential issueCredential = new IssueCredential(credentialName, credDefId, credentialValues, price);
        assertEquals(credentialName, issueCredential.credentialName);
        assertEquals(credDefId, issueCredential.credDefId);
        assertEquals(credentialValues.toString(), issueCredential.credentialValues.toString());
        assert price == issueCredential.price;
        testMessages(issueCredential);
    }

    private void testMessages(IssueCredential issueCredential) {
        JSONObject msg = issueCredential.messages.getJSONObject(IssueCredential.ISSUE);
        assertEquals(IssueCredential.getMessageType(IssueCredential.ISSUE), msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertNotNull(msg.getJSONObject("credentialData").getString("id"));
        assertEquals(credentialName, msg.getJSONObject("credentialData").getString("name"));
        assertEquals(credDefId, msg.getJSONObject("credentialData").getString("credDefId"));
        assertEquals(credentialValues.toString(), msg.getJSONObject("credentialData").getJSONObject("credentialValues").toString());
        assertEquals(price, msg.getJSONObject("credentialData").getString("price"));
    }

    @Test
    public void testIssue() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            IssueCredential issueCredential = new IssueCredential(credentialName, credDefId, credentialValues, price);
            issueCredential.disableHTTPSend();
            byte [] message = issueCredential.issue(context);
            JSONObject unpackedMessage = Util.unpackForwardMessage(context, message);
            assertEquals(IssueCredential.getMessageType(IssueCredential.ISSUE), unpackedMessage.getString("@type"));
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }
}
package com.evernym.verity.sdk.protocols.issue_cred.v0_6;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.issuecredential.IssueCredential;
import com.evernym.verity.sdk.protocols.issuecredential.v0_6.IssueCredentialV0_6;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class IssueCredentialTest {

    private String forRelationship = "...someDid...";
    private String credentialName = "Bachelors Degree";
    private String credDefId = "...someCredDefId...";
    private Map<String, String> credentialValues = new HashMap<>();
    private String price = "0";

    public IssueCredentialTest() {
        credentialValues.put("name", "Jose Smith");
        credentialValues.put("degree", "Bachelors");
        credentialValues.put("gpa", "3.67");
    }

    @Test
    public void testGetMessageType() {
        IssueCredentialV0_6 testProtocol = IssueCredential.v0_6(
                forRelationship,
                credentialName,
                credentialValues,
                credDefId
            );
        String msgName = "msg name";
        assertEquals(
                Util.getMessageType(Util.EVERNYM_MSG_QUALIFIER, testProtocol.family(), testProtocol.version(), msgName),
                testProtocol.getMessageType(msgName)
        );
    }

    @Test
    public void testConstructor() throws VerityException {
        Context context = TestHelpers.getContext();
        IssueCredentialV0_6 testProtocol = IssueCredential.v0_6(
                forRelationship,
                credentialName,
                credentialValues,
                credDefId
        );

        testOfferMessages(testProtocol.offerCredentialMsg(context));
        testIssueMsg(testProtocol.issueCredentialMsg(context));
        testStatusMsg(testProtocol.statusMsg(context));
    }

    private void testOfferMessages(JSONObject msg) {
        assertNotNull(msg.getString("@id"));
        assertNotNull(msg.getJSONObject("~thread").getString("thid"));
        assertEquals(forRelationship, msg.getString("~for_relationship"));
        assertEquals(credentialName, msg.getString("name"));
        assertEquals(credDefId, msg.getString("credDefId"));
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals(credentialValues, msg.getJSONObject("credentialValues").toMap());
        assertEquals(price, msg.getString("price"));
    }
    private void testIssueMsg(JSONObject msg) {
        assertNotNull(msg.getString("@id"));
        assertNotNull(msg.getJSONObject("~thread").getString("thid"));
        assertEquals(forRelationship, msg.getString("~for_relationship"));
    }
    private void testStatusMsg(JSONObject msg) {
        assertNotNull(msg.getString("@id"));
        assertNotNull(msg.getJSONObject("~thread").getString("thid"));
        assertEquals(forRelationship, msg.getString("~for_relationship"));
    }

    @Test
    public void testIssue() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            IssueCredentialV0_6 testProtocol = IssueCredential.v0_6(
                    forRelationship,
                    credentialName,
                    credentialValues,
                    credDefId
            );

            byte [] offerMsg = testProtocol.offerCredentialMsgPacked(context);
            JSONObject unpackedOfferMessage = Util.unpackForwardMessage(context, offerMsg);
            assertEquals(
                    "did:sov:123456789abcdefghi1234;spec/issue-credential/0.6/send-offer",
                    unpackedOfferMessage.getString("@type")
            );

            byte [] issueMsg = testProtocol.issueCredentialMsgPacked(context);
            JSONObject unpackedIssueMessage = Util.unpackForwardMessage(context, issueMsg);
            assertEquals(
                    "did:sov:123456789abcdefghi1234;spec/issue-credential/0.6/issue-credential",
                    unpackedIssueMessage.getString("@type")
            );

            byte [] statusMsg = testProtocol.statusMsgPacked(context);
            JSONObject unpackedStatusMessage = Util.unpackForwardMessage(context, statusMsg);
            assertEquals(
                    "did:sov:123456789abcdefghi1234;spec/issue-credential/0.6/get-status",
                    unpackedStatusMessage.getString("@type")
            );
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }
}
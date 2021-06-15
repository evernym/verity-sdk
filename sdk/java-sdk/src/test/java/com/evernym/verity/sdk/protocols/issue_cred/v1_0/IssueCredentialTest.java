package com.evernym.verity.sdk.protocols.issue_cred.v1_0;

import com.evernym.verity.sdk.TestBase;
import com.evernym.verity.sdk.protocols.issuecredential.IssueCredential;
import com.evernym.verity.sdk.protocols.issuecredential.v1_0.IssueCredentialV1_0;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.evernym.verity.sdk.TestHelpers.unpackForwardMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IssueCredentialTest extends TestBase {

    private final String forRelationship = "...someDid...";
    private final Map<String, String> values = new HashMap<>();
    private final String credDefId = "cred-def-id";
    private final String comment = "some comment";
    private final String price = "0";
    private final Boolean autoIssue = true;
    private final Boolean byInvitation = true;
    private final String threadId = "some thread id";

    public IssueCredentialTest() {
        values.put("name", "Jose Smith");
        values.put("degree", "Bachelors");
        values.put("gpa", "3.67");
    }

    @Test
    public void testGetMessageType() {
        IssueCredentialV1_0 testProtocol = IssueCredential.v1_0(
                forRelationship,
                threadId
        );
        String msgName = "msg name";
        assertEquals(
                Util.getMessageType(Util.COMMUNITY_MSG_QUALIFIER, testProtocol.family(),
                        testProtocol.version(), msgName), testProtocol.messageType(msgName)
        );
    }

    @Test
    public void testGetThreadId() {
        IssueCredentialV1_0 testProtocol = IssueCredential.v1_0(
                forRelationship,
                threadId
        );
        assertNotNull(testProtocol.getThreadId());
    }

    @Test
    public void testConstructorWithRequiredFieldAsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IssueCredential.v1_0(null, null,null, null, null, null);
        });
    }

    @Test
    public void testConstructorWithAllOptionalAsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IssueCredential.v1_0(forRelationship, null,null, null, null, null);
        });
    }


//    @Test
//    public void testSendPropose() throws Exception {
//
//        withContext ( context -> {
//            IssueCredentialV1_0 testProtocol = IssueCredential.v1_0(forRelationship,credDefId, values, comment, null, null);
//            byte [] msg = testProtocol.proposeCredentialMsgPacked(context);
//            JSONObject unpackedMessage = Util.unpackForwardMessage(context, msg);
//            testProposalMessage(unpackedMessage);
//        });
//    }

    @Test
    public void testSendOffer() throws Exception {
        withContext ( context -> {
            IssueCredentialV1_0 testProtocol = IssueCredential.v1_0(forRelationship, credDefId, values, comment, price, autoIssue, byInvitation);
            byte [] msg = testProtocol.offerCredentialMsgPacked(context);
            JSONObject unpackedMessage = unpackForwardMessage(context, msg);
            testOfferMessage(unpackedMessage);
        });
    }

    @Test
    public void testIssueCred() throws Exception {
        withContext ( context -> {
            IssueCredentialV1_0 testProtocol = IssueCredential.v1_0(forRelationship, threadId);
            byte [] msg = testProtocol.issueCredentialMsgPacked(context);
            JSONObject unpackedMessage = unpackForwardMessage(context, msg);
            testIssueCredMessage(unpackedMessage);
        });
    }

//    @Test
//    public void testRequest() throws Exception {
//        withContext ( context -> {
//            IssueCredentialV1_0 testProtocol = IssueCredential.v1_0(forRelationship, credDefId, values, comment, null);
//            byte [] msg = testProtocol.requestCredentialMsgPacked(context);
//            JSONObject unpackedMessage = Util.unpackForwardMessage(context, msg);
//            testRequestMessage(unpackedMessage);
//        });
//    }

    @Test
    public void testReject() throws Exception {
        withContext ( context -> {
            IssueCredentialV1_0 testProtocol = IssueCredential.v1_0(forRelationship, threadId);
            byte [] msg = testProtocol.rejectMsgPacked(context);
            JSONObject unpackedMessage = unpackForwardMessage(context, msg);
            testRejectMessage(unpackedMessage);
        });
    }

    @Test
    public void testStatus() throws Exception {
        withContext ( context -> {
            IssueCredentialV1_0 testProtocol = IssueCredential.v1_0(forRelationship, threadId);
            byte [] msg = testProtocol.statusMsgPacked(context);
            JSONObject unpackedMessage = unpackForwardMessage(context, msg);
            testStatusMessage(unpackedMessage);
        });
    }

    //below are helper methods

    private void testProposalMessage(JSONObject msg) {
        testCommonProposeAndOfferMsg(msg);
        assertEquals(Util.COMMUNITY_MSG_QUALIFIER + "/issue-credential/1.0/propose", msg.getString("@type"));
    }

    private void testOfferMessage(JSONObject msg) {
        testCommonProposeAndOfferMsg(msg);
        assertEquals(Util.COMMUNITY_MSG_QUALIFIER + "/issue-credential/1.0/offer", msg.getString("@type"));
        assertEquals(msg.getString("price"), price);
        assertEquals(msg.getBoolean("auto_issue"), autoIssue);
        assertEquals(msg.getBoolean("by_invitation"), byInvitation);
    }

    private void testIssueCredMessage(JSONObject msg) {
        testBaseMessage(msg);
        assertEquals(Util.COMMUNITY_MSG_QUALIFIER + "/issue-credential/1.0/issue", msg.getString("@type"));
        assertEquals(threadId, msg.getJSONObject("~thread").getString("thid"));
//        assertEquals(msg.getString("comment"), comment); // this is optional argument, this version is not able to send.
    }

    private void testRequestMessage(JSONObject msg) {
        testBaseMessage(msg);
        assertEquals(Util.COMMUNITY_MSG_QUALIFIER + "/issue-credential/1.0/request", msg.getString("@type"));
        // this must be the same threadId as received, but this version does not support it
//        assertEquals(threadId, msg.getJSONObject("~thread").getString("thid"));
        assertEquals(credDefId, msg.getString("cred_def_id"));
//        assertEquals(msg.getString("comment"), comment); // this is optional argument, this version is not able to send.
    }

    private void testRejectMessage(JSONObject msg) {
        testBaseMessage(msg);
        assertEquals(Util.COMMUNITY_MSG_QUALIFIER + "/issue-credential/1.0/reject", msg.getString("@type"));
        assertEquals(threadId, msg.getJSONObject("~thread").getString("thid"));
//        assertEquals(msg.getString("comment"), comment); // this is optional argument, this version is not able to send.
    }

    private void testStatusMessage(JSONObject msg) {
        testBaseMessage(msg);
        assertEquals(Util.COMMUNITY_MSG_QUALIFIER + "/issue-credential/1.0/status", msg.getString("@type"));
        assertEquals(threadId, msg.getJSONObject("~thread").getString("thid"));
    }

    private void testCommonProposeAndOfferMsg(JSONObject msg) {
        testBaseMessage(msg);
        assertEquals(credDefId, msg.getString("cred_def_id"));
        assertEquals(new JSONObject(values).toString(), msg.getJSONObject("credential_values").toString());
        assertEquals(comment, msg.getString("comment"));
    }

    private void testBaseMessage(JSONObject msg) {
        assertNotNull(msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertEquals(forRelationship, msg.getString("~for_relationship"));
        assertNotNull(msg.getJSONObject("~thread").getString("thid"));
    }
}

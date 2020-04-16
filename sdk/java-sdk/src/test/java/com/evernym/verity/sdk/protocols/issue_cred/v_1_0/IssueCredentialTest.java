package com.evernym.verity.sdk.protocols.issue_cred.v_1_0;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.issuecredential.IssueCredential;
import com.evernym.verity.sdk.protocols.issuecredential.v1_0.IssueCredentialV1_0;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class IssueCredentialTest {

    private String forRelationship = "...someDid...";
    private Map<String, String> values = new HashMap<>();

    public IssueCredentialTest() {
        values.put("name", "Jose Smith");
        values.put("degree", "Bachelors");
        values.put("gpa", "3.67");
    }

    @Test
    public void testGetMessageType() {
        IssueCredentialV1_0 testProtocol = IssueCredential.v1_0(
                forRelationship,
                "dummy-thread-id"
        );
        String msgName = "msg name";
        assertEquals(
                Util.getMessageType(Util.COMMUNITY_MSG_QUALIFIER, testProtocol.family(),
                        testProtocol.version(), msgName), testProtocol.getMessageType(msgName)
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithRequiredFieldAsNull() {
        IssueCredential.v1_0(null, null,null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithAllOptionalAsNull() {
        IssueCredential.v1_0(forRelationship, null,null, null, null);
    }

    @Test
    public void testValidConstructor() throws VerityException {
        Context context = TestHelpers.getContext();
        IssueCredentialV1_0 testProtocol = IssueCredential.v1_0(forRelationship, "cred-def-id", null, null, null);
        testProposalMessage(testProtocol.proposeCredentialMsg(context));
    }

    @Test
    public void testSendPropose() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            String credDefId = "cred-def-id";
            IssueCredentialV1_0 testProtocol = IssueCredential.v1_0(forRelationship,credDefId, values, "comment", null);

            byte [] offerMsg = testProtocol.proposeCredentialMsgPacked(context);
            JSONObject unpackedProposedMessage = Util.unpackForwardMessage(context, offerMsg);
            assertEquals(
                    "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/issue-credential/1.0/send-proposal",
                    unpackedProposedMessage.getString("@type")
            );
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }

    //below are helper methods

    private void testProposalMessage(JSONObject msg) {
        assertEquals("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/issue-credential/1.0/send-proposal", msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertNotNull(msg.getJSONObject("~thread").getString("thid"));
        assertNotNull(msg.getString("cred_def_id"));
        assertEquals(forRelationship, msg.getString("~for_relationship"));
    }


}

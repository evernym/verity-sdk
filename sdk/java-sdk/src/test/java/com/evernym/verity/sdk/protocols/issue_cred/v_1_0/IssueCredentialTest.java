package com.evernym.verity.sdk.protocols.issue_cred.v_1_0;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.issuecredential.IssueCredential;
import com.evernym.verity.sdk.protocols.issuecredential.v1_0.CredPreviewAttribute;
import com.evernym.verity.sdk.protocols.issuecredential.v1_0.IssueCredentialV1_0;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class IssueCredentialTest {

    private String forRelationship = "...someDid...";
    private List<CredPreviewAttribute> attributes = new ArrayList<>();

    public IssueCredentialTest() {
        attributes.add(new CredPreviewAttribute("name", null, "Jose Smith"));
        attributes.add(new CredPreviewAttribute("degree", null, "Bachelors"));
        attributes.add(new CredPreviewAttribute("gpa", null, "3.67"));
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
        IssueCredential.v1_0(null, null,
                null, null, null,null,
                null,null, null,null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithAllOptionalAsNull() {
        IssueCredential.v1_0(
                forRelationship, null, null, null, null,null,
                null,null, null,null, null);
    }

    @Test
    public void testValidConstructor() throws VerityException {
        Context context = TestHelpers.getContext();
        IssueCredentialV1_0 testProtocol = IssueCredential.v1_0(
                forRelationship, "driver license", "cred-def-id", null, null,
                null,null, null,null, null, null);
        testProposalMessage(testProtocol.proposeCredentialMsg(context));
    }

    @Test
    public void testSendPropose() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            String name = "cred-name";
            String credDefId = "cred-def-id";
            IssueCredentialV1_0 testProtocol = IssueCredential.v1_0(
                    forRelationship, name, credDefId,
                    attributes, null, "comment", null,
                    null, null, null, null);

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
        assertEquals(forRelationship, msg.getString("~for_relationship"));

        ArrayList<Boolean> optionalFieldCheckResults = new ArrayList<>();

        optionalFieldCheckResults.add(msg.has("credential_proposal"));
        optionalFieldCheckResults.add(msg.has("comment"));
        optionalFieldCheckResults.add(msg.has("schema_issuer_id"));
        optionalFieldCheckResults.add(msg.has("schema_id"));
        optionalFieldCheckResults.add(msg.has("schema_name"));
        optionalFieldCheckResults.add(msg.has("schema_version"));
        optionalFieldCheckResults.add(msg.has("cred_def_id"));
        optionalFieldCheckResults.add(msg.has("issuer_did"));

        assertTrue("one of the optional field should be present",
                optionalFieldCheckResults.stream().anyMatch(Objects::nonNull));
    }

    private ArrayList<String> optionalFieldNames() {
        ArrayList<String> optionalFields = new ArrayList<>();
        optionalFields.add("credential_proposal");
        optionalFields.add("schema_issuer_did");
        optionalFields.add("schema_id");
        optionalFields.add("schema_name");
        optionalFields.add("schema_version");
        optionalFields.add("cred_def_id");
        optionalFields.add("issuer_did");
        return optionalFields;
    }

}

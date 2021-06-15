package com.evernym.verity.sdk.protocols.relationship.v1_0;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.relationship.Relationship;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class RelationshipTest {

    final String label = "Alice";
    final URL logoUrl = new URL("http://server.com/profile_url.png");
    final String phoneNumber = "+18011234567";
    final String forRelationship = "did1";
    final boolean shortInvite = true;

    public RelationshipTest() throws MalformedURLException {
    }

    @Test
    public void testGetMessageType() {
        RelationshipV1_0 relationshipProvisioning = Relationship.v1_0(
                "forRelationship",
                "threadId"
        );
        String msgName = "msg name";
        assertEquals(
            Util.getMessageType(
                    Util.EVERNYM_MSG_QUALIFIER,
                    "relationship",
                    "1.0",
                    msgName
            ),
            relationshipProvisioning.messageType(msgName)
        );
    }

    @Test
    public void testGetThreadId() {
        RelationshipV1_0 testProtocol = Relationship.v1_0(label);
        assertNotNull(testProtocol.getThreadId());
    }

    @Test
    public void testCreateMsg() throws VerityException, IOException {
        RelationshipV1_0 relationship = Relationship.v1_0();
        JSONObject msg = relationship.createMsg(TestHelpers.getContext());
        testCreateMsg(msg, false, false);

        RelationshipV1_0 relationship1 = Relationship.v1_0(label);
        JSONObject msg1 = relationship1.createMsg(TestHelpers.getContext());
        testCreateMsg(msg1, true, false);

        RelationshipV1_0 relationship2 = Relationship.v1_0(null, logoUrl);
        JSONObject msg2 = relationship2.createMsg(TestHelpers.getContext());
        testCreateMsg(msg2, false, true);

        RelationshipV1_0 relationship3 = Relationship.v1_0(label, logoUrl);
        JSONObject msg3 = relationship3.createMsg(TestHelpers.getContext());
        testCreateMsg(msg3, true, true);
    }

    private void testCreateMsg(JSONObject msg, boolean hasLabel, boolean hasLogo) {

        testBaseMessage(msg);
        assertEquals(Util.EVERNYM_MSG_QUALIFIER + "/relationship/1.0/create", msg.getString("@type"));
        if (hasLabel)
            assertEquals(label.toString(), msg.getString("label"));
        else
            assertFalse(msg.has("label"));
        if (hasLogo)
            assertEquals(logoUrl.toString(), msg.getString("logoUrl"));
        else
            assertFalse(msg.has("logoUrl"));
    }

    @Test
    public void testConnectionInvitationMsg() throws VerityException, IOException {
        RelationshipV1_0 relationship = Relationship.v1_0(forRelationship, "thread-id");
        JSONObject msg = relationship.connectionInvitationMsg(TestHelpers.getContext(), null);
        testConnectionInvitationMsg(msg, false);
    }

    @Test
    public void testConnectionInvitationMsgWithShortInvite() throws VerityException, IOException {
        RelationshipV1_0 relationship = Relationship.v1_0(forRelationship, "thread-id");
        JSONObject msg = relationship.connectionInvitationMsg(TestHelpers.getContext(), shortInvite);
        testConnectionInvitationMsg(msg, true);
    }

    private void testConnectionInvitationMsg(JSONObject msg, boolean hasShortInvite) {
        testBaseMessage(msg);
        assertEquals(Util.EVERNYM_MSG_QUALIFIER + "/relationship/1.0/connection-invitation", msg.getString("@type"));
        assertNotNull(forRelationship, msg.getString("~for_relationship"));
        if (hasShortInvite)
            assertEquals(shortInvite, msg.getBoolean("shortInvite"));
    }

    @Test
    public void testSMSConnectionInvitationMsg() throws VerityException, IOException {
        RelationshipV1_0 relationship = Relationship.v1_0(forRelationship, "thread-id");
        JSONObject msg = relationship.smsConnectionInvitationMsg(TestHelpers.getContext(), phoneNumber);
        testSMSConnectionInvitationMsg(msg);
    }

    private void testSMSConnectionInvitationMsg(JSONObject msg) {
        testBaseMessage(msg);
        assertEquals(Util.EVERNYM_MSG_QUALIFIER + "/relationship/1.0/sms-connection-invitation", msg.getString("@type"));
        assertNotNull(forRelationship, msg.getString("~for_relationship"));
        assertEquals(phoneNumber, msg.getString("phoneNumber"));
    }

    @Test
    public void testOutOfBandIInvitationMsg() throws VerityException, IOException {
        RelationshipV1_0 relationship = Relationship.v1_0(forRelationship, "thread-id");
        JSONObject msg = relationship.outOfBandInvitationMsg(TestHelpers.getContext());
        testOutOfBandInvitationMsg(msg, false, null);
    }

    @Test
    public void testOutOfBandIInvitationMsgWithShortInvite() throws VerityException, IOException {
        RelationshipV1_0 relationship = Relationship.v1_0(forRelationship, "thread-id");
        JSONObject msg = relationship.outOfBandInvitationMsg(TestHelpers.getContext(), shortInvite);
        testOutOfBandInvitationMsg(msg, true, null);
    }

    @Test
    public void testOutOfBandIInvitationMsgWithGoalCode() throws VerityException, IOException {
        RelationshipV1_0 relationship = Relationship.v1_0(forRelationship, "thread-id");
        JSONObject msg = relationship.outOfBandInvitationMsg(
                TestHelpers.getContext(),
                null,
                GoalCode.REQUEST_PROOF
        );
        testOutOfBandInvitationMsg(msg, false, GoalCode.REQUEST_PROOF);
    }

    private void testOutOfBandInvitationMsg(JSONObject msg, boolean hasShortInvite, GoalCode goalCode) {
        testBaseMessage(msg);
        assertEquals(Util.EVERNYM_MSG_QUALIFIER + "/relationship/1.0/out-of-band-invitation", msg.getString("@type"));
        if (goalCode != null) {
            assert(msg.getString("goalCode").equals(goalCode.code()));
            assert(msg.getString("goal").equals(goalCode.goalName()));
        }
        else {
            assertFalse(msg.has("goalCode"));
            assertFalse(msg.has("goal"));
        }
        if (hasShortInvite)
            assertEquals(shortInvite, msg.getBoolean("shortInvite"));
    }

    @Test
    public void testSMSOutOfBandIInvitationMsg() throws VerityException, IOException {
        RelationshipV1_0 relationship = Relationship.v1_0(forRelationship, "thread-id");
        JSONObject msg = relationship.smsOutOfBandInvitationMsg(TestHelpers.getContext(), phoneNumber);
        testSMSOutOfBandInvitationMsg(msg, null);
    }

    @Test
    public void testSMSOutOfBandIInvitationMsgWithGoalCode() throws VerityException, IOException {
        RelationshipV1_0 relationship = Relationship.v1_0(forRelationship, "thread-id");
        JSONObject msg = relationship.smsOutOfBandInvitationMsg(
                TestHelpers.getContext(),
                phoneNumber,
                GoalCode.REQUEST_PROOF
        );
        testSMSOutOfBandInvitationMsg(msg, GoalCode.REQUEST_PROOF);
    }

    private void testSMSOutOfBandInvitationMsg(JSONObject msg, GoalCode goalCode) {
        testBaseMessage(msg);
        assertEquals(Util.EVERNYM_MSG_QUALIFIER + "/relationship/1.0/sms-out-of-band-invitation", msg.getString("@type"));
        if (goalCode != null) {
            assert(msg.getString("goalCode").equals(goalCode.code()));
            assert(msg.getString("goal").equals(goalCode.goalName()));
        }
        else {
            assertFalse(msg.has("goalCode"));
            assertFalse(msg.has("goal"));
        }
        assertEquals(phoneNumber, msg.getString("phoneNumber"));
    }

    private void testBaseMessage(JSONObject msg) {
        assertNotNull(msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertNotNull(msg.getJSONObject("~thread").getString("thid"));
    }
}
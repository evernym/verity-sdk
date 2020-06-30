package com.evernym.verity.sdk.protocols.relationship.v1_0;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.outofband.OutOfBand;
import com.evernym.verity.sdk.protocols.outofband.v1_0.OutOfBandV1_0;
import com.evernym.verity.sdk.protocols.relationship.Relationship;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RelationshipTest {

    final String label = "Alice";
    final String forRelationship = "did1";

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
            Util.getMessageType(relationshipProvisioning, msgName)
        );
    }

    @Test
    public void testCreateMsg() throws VerityException, IOException {
        RelationshipV1_0 relationship = Relationship.v1_0(label);
        JSONObject msg = relationship.createMsg(TestHelpers.getContext());
        testCreateMsg(msg);
    }

    private void testCreateMsg(JSONObject msg) {
        assertEquals("did:sov:123456789abcdefghi1234;spec/relationship/1.0/create", msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
    }

    @Test
    public void testConnectionInvitationMsg() throws VerityException, IOException {
        RelationshipV1_0 relationship = Relationship.v1_0(forRelationship, "thread-id");
        JSONObject msg = relationship.connectionInvitationMsg(TestHelpers.getContext());
        testConnectionInvitationMsg(msg);
    }

    private void testConnectionInvitationMsg(JSONObject msg) {
        assertEquals("did:sov:123456789abcdefghi1234;spec/relationship/1.0/connection-invitation", msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertNotNull(forRelationship, msg.getString("~for_relationship"));
    }

    @Test
    public void testOutOfBandIInvitationMsg() throws VerityException, IOException {
        RelationshipV1_0 relationship = Relationship.v1_0("label", "goalCode",
                "goal", null);
        JSONObject msg = relationship.outOfBandInvitationMsg(TestHelpers.getContext());
        testOutOfBandInvitationMsg(msg);
    }

    private void testOutOfBandInvitationMsg(JSONObject msg) {
        assertEquals("did:sov:123456789abcdefghi1234;spec/relationship/1.0/out-of-band-invitation", msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertNotNull(msg.getString("label"));
        assertNotNull(msg.getString("goal_code"));
        assertNotNull(msg.getString("goal"));
    }
}
package com.evernym.verity.sdk.protocols.relationship.v1_0;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.relationship.Relationship;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RelationshipTest {

    String did = "did1";
    String label = "Alice";
    String forRelationship = "did1";
    ArrayList<String> recipKeys = new ArrayList<> (Arrays.asList("1a", "2b"));
    ArrayList<String> routingKeys = new ArrayList<> (Arrays.asList("3c", "4d"));


    @Test
    public void testGetMessageType() {
        RelationshipV1_0 relationshipProvisioning = Relationship.v1_0(
                "forRelationship",
                "threadId",
                "label",
                recipKeys,
                emptyList()
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
        RelationshipV1_0 relationshipProvisioning = Relationship.v1_0();
        JSONObject msg = relationshipProvisioning.createMsg(TestHelpers.getContext());
        testCreateMsg(msg);
    }

    private void testCreateMsg(JSONObject msg) {
        assertEquals("did:sov:123456789abcdefghi1234;spec/relationship/1.0/create", msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
    }

    @Test
    public void testPrepareInvitationWithDID() throws VerityException, IOException {
        RelationshipV1_0 relationship = Relationship.v1_0(
                forRelationship,
                "threadId",
                label,
                did);
        JSONObject msg = relationship.prepareInvitationMsg(TestHelpers.getContext());
        testPrepareInvitationWithDID(msg);
    }

    private void testPrepareInvitationWithDID(JSONObject msg) {
        assertEquals("did:sov:123456789abcdefghi1234;spec/relationship/1.0/prepare-invite", msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertNotNull(forRelationship, msg.getString("~for_relationship"));
        assertEquals(label, msg.getString("label"));
        assertEquals(did, msg.getString("did"));
    }

    @Test
    public void testInvitationWithKeyMsg() throws VerityException, IOException {

        RelationshipV1_0 relationshipProvisioning = Relationship.v1_0(forRelationship, "threadId", label, recipKeys, routingKeys);
        JSONObject msg = relationshipProvisioning.prepareInvitationMsg(TestHelpers.getContext());
        testInvitationWithKeyMsg(msg);
    }

    private void testInvitationWithKeyMsg(JSONObject msg) {
        assertEquals("did:sov:123456789abcdefghi1234;spec/relationship/1.0/prepare-invite", msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertNotNull(forRelationship, msg.getString("~for_relationship"));
        assertEquals(recipKeys, fromJSONArray(msg.getJSONArray("recipient_keys")));
        assertEquals(routingKeys, fromJSONArray(msg.getJSONArray("routing_keys")));
        assertEquals(label, msg.getString("label"));
    }

    private ArrayList<String> fromJSONArray(JSONArray jsonArray) {
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            data.add(jsonArray.getString(i));
        }
        return data;
    }
}
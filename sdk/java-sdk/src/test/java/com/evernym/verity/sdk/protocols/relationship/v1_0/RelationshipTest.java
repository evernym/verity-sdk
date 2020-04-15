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
    String serviceEndpoint = "service-endpoint";
    ArrayList<String> recipKeys = new ArrayList<> (Arrays.asList("1a", "2b"));
    ArrayList<String> routingKeys = new ArrayList<> (Arrays.asList("3c", "4d"));


    @Test
    public void testGetMessageType() {
        RelationshipV1_0 relationshipProvisioning = Relationship.v1_0(
                "",
                "",
                "",
                emptyList(),
                emptyList(),
                ""
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
    public void testCreateKeyMsg() throws VerityException, IOException {
        RelationshipV1_0 relationshipProvisioning = Relationship.v1_0(
                "",
                "",
                "",
                emptyList(),
                emptyList(),
                "NhhHXi83n2mePEteXMkAaw"
        );
        JSONObject msg = relationshipProvisioning.createMsg(TestHelpers.getContext());
        testCreateKeyMsg(msg);
    }

    private void testCreateKeyMsg(JSONObject msg) {
        assertEquals("did:sov:123456789abcdefghi1234;spec/relationship/1.0/create", msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
    }

    @Test
    public void testInvitationWithDIDMsg() throws VerityException, IOException {
        RelationshipV1_0 relationshipProvisioning = Relationship.v1_0(
                forRelationship,
                "threadId",
                label,
                null,
                null,
                did);
        JSONObject msg = relationshipProvisioning.prepareInvitationMsg(TestHelpers.getContext());
        testInvitationWithDIDMsg(msg);
    }

    private void testInvitationWithDIDMsg(JSONObject msg) {
        assertEquals("did:sov:123456789abcdefghi1234;spec/relationship/1.0/prepare-invite", msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertNotNull(forRelationship, msg.getString("~for_relationship"));
        assertEquals(did, msg.getString("did"));
        assertEquals(label, msg.getString("label"));
    }

    @Test
    public void testInvitationWithKeyMsg() throws VerityException, IOException {

        RelationshipV1_0 relationshipProvisioning = Relationship.v1_0(forRelationship, "threadId", label, recipKeys, routingKeys, null);
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
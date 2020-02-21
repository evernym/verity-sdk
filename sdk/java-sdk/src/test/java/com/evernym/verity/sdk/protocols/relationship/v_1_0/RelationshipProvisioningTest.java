package com.evernym.verity.sdk.protocols.relationship.v_1_0;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.relationship.RelationshipProvisioning;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RelationshipProvisioningTest {

    String did = "did1";
    String label = "Alice";
    String forRelationship = "did1";
    String serviceEndpoint = "service-endpoint";
    ArrayList<String> recipKeys = new ArrayList<String> (Arrays.asList("1a", "2b"));
    ArrayList<String> routingKeys = new ArrayList<String> (Arrays.asList("3c", "4d"));


    @Test
    public void testGetMessageType() {
        RelationshipProvisioning relationshipProvisioning = RelationshipProvisioning.v_10();
        String msgName = "msg name";
        assertEquals(
            Util.getMessageType(
                    Util.EVERNYM_MSG_QUALIFIER,
                    "relationship-provisioning",
                    "1.0",
                    msgName
            ),
            Util.getMessageType(relationshipProvisioning, msgName)
        );
    }

    @Test
    public void testCreateKeyMsg() throws VerityException, IOException {
        RelationshipProvisioning relationshipProvisioning = RelationshipProvisioning.v_10();
        JSONObject msg = relationshipProvisioning.createKeyMsg(TestHelpers.getContext());
        testCreateKeyMsg(msg);
    }

    private void testCreateKeyMsg(JSONObject msg) {
        assertEquals("did:sov:123456789abcdefghi1234;spec/relationship-provisioning/1.0/create-key", msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
    }

    @Test
    public void testInvitationWithDIDMsg() throws VerityException, IOException {
        RelationshipProvisioning relationshipProvisioning = RelationshipProvisioning.v_10(forRelationship, label, null, null, did);
        JSONObject msg = relationshipProvisioning.prepareInvitationMsg(TestHelpers.getContext());
        testInvitationWithDIDMsg(msg);
    }

    private void testInvitationWithDIDMsg(JSONObject msg) {
        assertEquals("did:sov:123456789abcdefghi1234;spec/relationship-provisioning/1.0/prepare-with-did", msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertNotNull(forRelationship, msg.getString("~for_relationship"));
        assertEquals(did, msg.getString("did"));
        assertEquals(label, msg.getString("label"));
    }

    @Test
    public void testInvitationWithKeyMsg() throws VerityException, IOException {

        RelationshipProvisioning relationshipProvisioning = RelationshipProvisioning.v_10(forRelationship, label, recipKeys, routingKeys, null);
        JSONObject msg = relationshipProvisioning.prepareInvitationMsg(TestHelpers.getContext());
        testInvitationWithKeyMsg(msg);
    }

    private void testInvitationWithKeyMsg(JSONObject msg) {
        assertEquals("did:sov:123456789abcdefghi1234;spec/relationship-provisioning/1.0/prepare-with-key", msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertNotNull(forRelationship, msg.getString("~for_relationship"));
        assertEquals(recipKeys, fromJSONArray(msg.getJSONArray("recipientKeys")));
        assertEquals(routingKeys, fromJSONArray(msg.getJSONArray("routingKeys")));
        assertEquals(label, msg.getString("label"));
    }

    private ArrayList<String> fromJSONArray(JSONArray jsonArray) {
        ArrayList<String> data = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            data.add(jsonArray.getString(i));
        }
        return data;
    }
}
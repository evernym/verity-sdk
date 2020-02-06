package com.evernym.verity.sdk.protocols.connecting.v_1_0;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.connecting.Connecting;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ConnectionsTest {

    String did = "did1";
    String label = "Alice";
    String serviceEndpoint = "service-endpoint";
    ArrayList<String> recipKeys = new ArrayList<String> (Arrays.asList("1a", "2b"));
    ArrayList<String> routingKeys = new ArrayList<String> (Arrays.asList("3c", "4d"));


    @Test
    public void testGetMessageType() {
        Connecting connecting = Connecting.v_10(did, label);
        String msgName = "msg name";
        assertEquals(
            Util.getMessageType(
                    Util.COMMUNITY_MSG_QUALIFIER,
                    "connections",
                    "1.0",
                    msgName
            ),
            Util.getMessageType(connecting, msgName)
        );
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInvalidConstructor() throws VerityException, IOException {
        Connecting connecting = Connecting.v_10(null, label);
    }

    @Test
    public void testInvitationWithDIDMsg() throws VerityException, IOException {
        Connecting connecting = Connecting.v_10(did, label);
        JSONObject msg = connecting.invitationMsg(TestHelpers.getContext());
        testInvitationWithDIDMsg(msg);
    }

    private void testInvitationWithDIDMsg(JSONObject msg) {
        assertEquals("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/invitation", msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertNotNull(msg.getJSONObject("~thread").getString("thid"));
        assertEquals(did, msg.getString("did"));
        assertEquals(label, msg.getString("label"));
    }

    @Test
    public void testInvitationWithKeyMsg() throws VerityException, IOException {

        Connecting connecting = Connecting.v_10(serviceEndpoint, recipKeys, routingKeys, label);
        JSONObject msg = connecting.invitationMsg(TestHelpers.getContext());
        testInvitationWithKeyMsg(msg);
    }

    private void testInvitationWithKeyMsg(JSONObject msg) {
        assertEquals("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/invitation", msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertNotNull(msg.getJSONObject("~thread").getString("thid"));

        assertEquals(recipKeys, fromJSONArray(msg.getJSONArray("recipientKeys")));
        assertEquals(routingKeys, fromJSONArray(msg.getJSONArray("routingKeys")));
        assertEquals(serviceEndpoint, msg.getString("serviceEndpoint"));
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
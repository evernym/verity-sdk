package com.evernym.verity.sdk.protocols.relationship;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.protocols.relationship.v_1_0.RelationshipProvisioning_1_0;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public interface RelationshipProvisioning extends MessageFamily {

    static RelationshipProvisioning_1_0 v1_0() {
        return new RelationshipProvisioning_1_0();
    }

    static RelationshipProvisioning_1_0 v1_0(String forRelationship, String threadId, String label, List<String> recipientKeys, List<String> routingKeys, String did) {
        return new RelationshipProvisioning_1_0(forRelationship, threadId, label, recipientKeys, routingKeys, did);
    }

    void createKey(Context context) throws IOException, VerityException;
    JSONObject createKeyMsg(Context context) throws IOException, VerityException;
    byte[] createKeyMsgPacked(Context context) throws IOException, VerityException;

    void prepareInvitation(Context context) throws IOException, VerityException;
    JSONObject prepareInvitationMsg(Context context) throws IOException, VerityException;
    byte[] prepareInvitationMsgPacked(Context context) throws IOException, VerityException;
}

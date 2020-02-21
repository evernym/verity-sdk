package com.evernym.verity.sdk.protocols.relationship;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.protocols.relationship.v_1_0.RelationshipProvisioningImpl;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public interface RelationshipProvisioning extends MessageFamily {

    static RelationshipProvisioning v_10() {
        return new RelationshipProvisioningImpl();
    }

    static RelationshipProvisioning v_10(String forRelationship, String label, List<String> recipientKeys, List<String> routingKeys, String did) {
        return new RelationshipProvisioningImpl(forRelationship, label, recipientKeys, routingKeys, did);
    }

    void createKey(Context context) throws IOException, VerityException;
    JSONObject createKeyMsg(Context context) throws IOException, VerityException;
    byte[] createKeyMsgPacked(Context context) throws IOException, VerityException;

    void prepareInvitation(Context context) throws IOException, VerityException;
    JSONObject prepareInvitationMsg(Context context) throws IOException, VerityException;
    byte[] prepareInvitationMsgPacked(Context context) throws IOException, VerityException;
}

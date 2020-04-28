package com.evernym.verity.sdk.protocols.relationship;

import com.evernym.verity.sdk.protocols.relationship.v1_0.RelationshipV1_0;

import java.util.List;

public class Relationship {
    private Relationship() {}

    /**
     * used by inviter/invitee to create relationship
     * @return
     */
    public static RelationshipV1_0 v1_0() {
        return new RelationshipImplV1_0();
    }

    /**
     * used by inviter to prepare a invitation message with keys
     * @param forRelationship relationship identifier
     * @param threadId thread identifier
     * @param label required, suggested label for the connection
     * @param recipientKeys required, recipient keys
     * @param routingKeys optional, routing keys
     */
    public static RelationshipV1_0 v1_0(String forRelationship, String threadId, String label, List<String> recipientKeys, List<String> routingKeys) {
        return new RelationshipImplV1_0(forRelationship, threadId, label, recipientKeys, routingKeys);
    }

    /**
     * used by inviter to prepare a invitation message with DID
     * @param forRelationship relationship identifier
     * @param threadId thread identifier
     * @param label required, suggested label for the connection
     * @param did required, invitation with public DID
     */
    public static RelationshipV1_0 v1_0(String forRelationship, String threadId, String label, String did) {
        return new RelationshipImplV1_0(forRelationship, threadId, label, did);
    }

}

package com.evernym.verity.sdk.protocols.relationship;

import com.evernym.verity.sdk.protocols.relationship.v1_0.RelationshipV1_0;

import java.util.List;

public class Relationship {
    private Relationship() {}

    /**
     * used by inviter to prepare a invitation message
     * @param forRelationship relationship identifier
     * @param threadId thread identifier
     * @param label required, suggested label for the connection
     * @param recipientKeys required, recipient keys
     * @param routingKeys optional, routing keys
     * @param did optional, invitation with public DID
     */
    public static RelationshipV1_0 v1_0(String forRelationship, String threadId, String label, List<String> recipientKeys, List<String> routingKeys, String did) {
        return new RelationshipImplV1_0(forRelationship, threadId, label, recipientKeys, routingKeys, did);
    }
}

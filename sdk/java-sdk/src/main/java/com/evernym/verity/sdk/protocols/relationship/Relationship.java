package com.evernym.verity.sdk.protocols.relationship;

import com.evernym.verity.sdk.protocols.relationship.v1_0.RelationshipImplV1_0;

import java.util.List;

public class Relationship {
    public static RelationshipImplV1_0 v1_0() {
        return new RelationshipImplV1_0();
    }

    public static RelationshipImplV1_0 v1_0(String forRelationship, String threadId, String label, List<String> recipientKeys, List<String> routingKeys, String did) {
        return new RelationshipImplV1_0(forRelationship, threadId, label, recipientKeys, routingKeys, did);
    }
}

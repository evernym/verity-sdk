package com.evernym.verity.sdk.protocols.relationship;

import com.evernym.verity.sdk.protocols.relationship.v1_0.RelationshipV1_0;

public class Relationship {

    /**
     * used by inviter/invitee to create relationship
     * @param label label to be used in invitation
     * @return
     */
    public static RelationshipV1_0 v1_0(String label) {
        return new RelationshipImplV1_0(label);
    }

    /**
     * used by inviter to get a connection invitation message
     * @param forRelationship relationship identifier
     * @param threadId thread identifier
     */
    public static RelationshipV1_0 v1_0(String forRelationship, String threadId) {
        return new RelationshipImplV1_0(forRelationship, threadId);
    }


}

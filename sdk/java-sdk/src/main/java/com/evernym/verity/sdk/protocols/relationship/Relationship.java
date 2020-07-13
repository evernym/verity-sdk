package com.evernym.verity.sdk.protocols.relationship;

import com.evernym.verity.sdk.protocols.relationship.v1_0.RequestAttach;
import com.evernym.verity.sdk.protocols.relationship.v1_0.RelationshipV1_0;

import java.util.List;

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
     * used by inviter/invitee to create relationship
     * @param forRelationship relationship identifier
     * @param threadId thread identifier
     * @param goalCode of creation OutOfBand invitation
     *                 (issue-vc/request-proof/create-account/p2p-messaging)
     * @param goal of creation OutOfBand invitation
     * @param request to be used in OutOfBand invitation
     * @return
     */
    public static RelationshipV1_0 v1_0(String forRelationship, String threadId,
                                        String goalCode, String goal,
                                        List<RequestAttach> request) {
        return new RelationshipImplV1_0(forRelationship, threadId, goalCode, goal, request);
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

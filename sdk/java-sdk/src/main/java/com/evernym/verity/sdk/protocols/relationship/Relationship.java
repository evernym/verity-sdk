package com.evernym.verity.sdk.protocols.relationship;

import com.evernym.verity.sdk.protocols.connecting.Connecting;
import com.evernym.verity.sdk.protocols.relationship.v1_0.RelationshipV1_0;
import java.net.URL;

/**
 * Factory for the Relationship protocol objects
 * <p/>
 *
 * The Relationship protocol creates and manages relationships on the verity-application agent. These relationships
 * are secure communication channels between self-sovereign parties. A relationship much be created before using the
 * connections protocols. In the future this protocol will allow management of each given relationship
 * (eg key rotation)
 *
 * @see Connecting
 */
public class Relationship {

    /**
     * Constructor for the 1.0 Relationship object. This constructor creates an object that is ready to create a new
     * relationship.
     *
     * @param label the label presented in the invitation to connect to this relationship
     * @return 1.0 Relationship object
     */
    public static RelationshipV1_0 v1_0(String label) {
        return new RelationshipImplV1_0(label);
    }

    /**
     * Constructor for the 1.0 Relationship object. This constructor creates an object that is ready to create a new
     * relationship.
     *
     * @param label the label presented in the invitation to connect to this relationship
     * @param logoUrl logo url presented in invitation
     * @return 1.0 Relationship object
     */
    public static RelationshipV1_0 v1_0(String label, URL logoUrl) {
        return new RelationshipImplV1_0(label, logoUrl);
    }

    /**
     * Constructor for the 1.0 Relationship object. This constructor re-creates an object from a known relationship and
     * threadId. This object can only check status of the protocol.
     *
     * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
     * @param threadId The thread id of the already started protocol
     * @return 1.0 Relationship object
     */
    public static RelationshipV1_0 v1_0(String forRelationship, String threadId) {
        return new RelationshipImplV1_0(forRelationship, threadId);
    }
}

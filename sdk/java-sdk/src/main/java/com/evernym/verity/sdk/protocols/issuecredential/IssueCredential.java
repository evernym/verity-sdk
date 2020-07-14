package com.evernym.verity.sdk.protocols.issuecredential;

import com.evernym.verity.sdk.protocols.issuecredential.v1_0.IssueCredentialV1_0;

import java.util.Map;

/**
 * Factory for IssueCredential protocol objects.
 * <p/>
 *
 * The IssueCredential protocol allows one self-sovereign party to issue a verifiable credential to another
 * self-sovereign party. On the verity-application, these protocols use `anoncreds` as defined in the
 * Hyperledger-indy project. In the verity-sdk, the interface presented by these objects allow key-value attributes
 * to be issued to parties that have a DID-comm channel.
 *
 * @see <a href="https://github.com/hyperledger/aries-rfcs/tree/bb42a6c35e0d5543718fb36dd099551ab192f7b0/features/0036-issue-credential" target="_blank" rel="noopener noreferrer">Aries 0036: Issue Credential Protocol 1.0</a>
 */
public class IssueCredential {
    private IssueCredential() {}

    /**
     * Constructor for the 1.0 IssueCredential object. This constructor creates an object that is ready to start
     * process of issuing a credential.
     *
     * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
     * @param credDefId the Credential Definition that will be used to issue the credential
     * @param values a map of key-value pairs that make up the attributes in the credential
     * @param comment a human readable comment that is presented before issuing the credential
     * @param price token price (NOT CURRENTLY USED)
     * @param autoIssue flag for automatically issuing credential after receiving response for receiver (skip getting
     *                  signal for credential request and waiting for issue control message)
     * @return 1.0 IssueCredential object
     */
    public static IssueCredentialV1_0 v1_0(
            String forRelationship,
            String credDefId,
            Map<String, String> values,
            String comment,
            String price,
            Boolean autoIssue) {

        return new IssueCredentialImplV1_0(
                forRelationship,
                credDefId,
                values,
                comment,
                price,
                autoIssue);
    }

    /**
     * Constructor for the 1.0 IssueCredential object. This constructor creates an object that is ready to start
     * process of issuing a credential.
     *
     * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
     * @param credDefId the Credential Definition that will be used to issue the credential
     * @param values a map of key-value pairs that make up the attributes in the credential
     * @param comment a human readable comment that is presented before issuing the credential
     * @param price token price (NOT CURRENTLY USED)
     * @return 1.0 IssueCredential object
     */
    public static IssueCredentialV1_0 v1_0(
            String forRelationship,
            String credDefId,
            Map<String, String> values,
            String comment,
            String price) {

        return new IssueCredentialImplV1_0(
                forRelationship,
                credDefId,
                values,
                comment,
                price,
                false);
    }

    /**
     * Constructor for the 1.0 IssueCredential object. This constructor re-creates an object from a known relationship and
     * threadId. This object can only check status of the protocol.
     *
     * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
     * @param threadId The thread id of the already started protocol
     * @return 1.0 IssueCredential object
     */
    public static IssueCredentialV1_0 v1_0(
            String forRelationship,
            String threadId) {

        return new IssueCredentialImplV1_0(
                forRelationship,
                threadId
        );
    }

}

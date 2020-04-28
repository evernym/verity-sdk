package com.evernym.verity.sdk.protocols.issuecredential;

import com.evernym.verity.sdk.protocols.issuecredential.v0_6.IssueCredentialV0_6;
import com.evernym.verity.sdk.protocols.issuecredential.v1_0.IssueCredentialV1_0;

import java.util.Map;

public class IssueCredential {
    private IssueCredential() {}

    /**
     * Creates a new credential
     * @param name The name of credential.
     * @param credDefId The credDefId of the credential definition being used
     * @param values key-value pairs of credential attribute fields with the specified params defined in the credential definition
     */
    public static IssueCredentialV0_6 v0_6(
            String forRelationship,
            String name,
            Map<String, String> values,
            String credDefId) {


        return new IssueCredentialImplV0_6(
                forRelationship,
                name,
                values,
                credDefId
        );
    }

    public static IssueCredentialV0_6 v0_6(
            String forRelationship,
            String threadId) {

        return new IssueCredentialImplV0_6(
                forRelationship,
                threadId
        );
    }

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
                price);
    }

    public static IssueCredentialV1_0 v1_0(
            String forRelationship,
            String threadId) {

        return new IssueCredentialImplV1_0(
                forRelationship,
                threadId
        );
    }

}

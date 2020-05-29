package com.evernym.verity.sdk.protocols.issuecredential;

import com.evernym.verity.sdk.protocols.issuecredential.v1_0.IssueCredentialV1_0;

import java.util.Map;

public class IssueCredential {
    private IssueCredential() {}

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

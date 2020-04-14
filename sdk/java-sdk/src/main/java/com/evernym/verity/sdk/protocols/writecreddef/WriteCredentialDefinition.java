package com.evernym.verity.sdk.protocols.writecreddef;

import com.evernym.verity.sdk.protocols.writecreddef.v0_6.RevocationRegistryConfig;
import com.evernym.verity.sdk.protocols.writecreddef.v0_6.WriteCredentialDefinitionImplV0_6;
import com.evernym.verity.sdk.protocols.writecreddef.v0_6.WriteCredentialDefinitionV0_6;

public class WriteCredentialDefinition {
    private WriteCredentialDefinition() {}

    public static WriteCredentialDefinitionV0_6 v0_6(String name, String schemaId) {
        return new WriteCredentialDefinitionImplV0_6(name, schemaId);
    }

    public static WriteCredentialDefinitionV0_6 v0_6(String name, String schemaId, String tag) {
        return new WriteCredentialDefinitionImplV0_6(name, schemaId, tag);
    }

    public static WriteCredentialDefinitionV0_6 v0_6(String name, String schemaId, RevocationRegistryConfig revocation) {
        return new WriteCredentialDefinitionImplV0_6(name, schemaId, revocation);
    }

    public static WriteCredentialDefinitionV0_6 v0_6(String name, String schemaId, String tag, RevocationRegistryConfig revocation) {
        return new WriteCredentialDefinitionImplV0_6(name,schemaId, tag, revocation);
    }

}

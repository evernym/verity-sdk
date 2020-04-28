package com.evernym.verity.sdk.protocols.writecreddef;

import com.evernym.verity.sdk.protocols.writecreddef.v0_6.RevocationRegistryConfig;
import com.evernym.verity.sdk.protocols.writecreddef.v0_6.WriteCredentialDefinitionV0_6;

/**
 * Builds and sends an encrypted agent message to Verity asking Verity to
 * write a new Credential Definition to the ledger on behalf of the
 * SDK/enterprise.
 */
public class WriteCredentialDefinition {
    private WriteCredentialDefinition() {}


    /**
     * Initializes the CredDef object
     * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     */
    public static WriteCredentialDefinitionV0_6 v0_6(String name, String schemaId) {
        return new WriteCredentialDefinitionImplV0_6(name, schemaId);
    }

    /**
     * Initializes the CredDef object
     * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     * @param tag An optional tag for the credential definition
     */
    public static WriteCredentialDefinitionV0_6 v0_6(String name, String schemaId, String tag) {
        return new WriteCredentialDefinitionImplV0_6(name, schemaId, tag);
    }

    /**
     * Initializes the CredDef object
     * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     * @param revocation the revocation object defining revocation support. See libvcx documentation for more details.
     */
    public static WriteCredentialDefinitionV0_6 v0_6(String name, String schemaId, RevocationRegistryConfig revocation) {
        return new WriteCredentialDefinitionImplV0_6(name, schemaId, revocation);
    }

    /**
     * Initializes the CredDef object
     * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     * @param tag An optional tag for the credential definition
     * @param revocation the revocation object defining revocation support. See libvcx documentation for more details.
     */
    public static WriteCredentialDefinitionV0_6 v0_6(String name, String schemaId, String tag, RevocationRegistryConfig revocation) {
        return new WriteCredentialDefinitionImplV0_6(name,schemaId, tag, revocation);
    }

}

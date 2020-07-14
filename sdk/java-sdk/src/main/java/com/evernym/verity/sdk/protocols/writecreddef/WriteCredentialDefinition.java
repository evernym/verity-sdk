package com.evernym.verity.sdk.protocols.writecreddef;

import com.evernym.verity.sdk.protocols.writecreddef.v0_6.RevocationRegistryConfig;
import com.evernym.verity.sdk.protocols.writecreddef.v0_6.WriteCredentialDefinitionV0_6;

/**
 * Factory for the WriteCredentialDefinition protocol objects
 * <p/>
 *
 * The WriteCredentialDefinition protocol writes credential definitions to an Indy Identity Ledger
 * (often the Sovrin Ledger). This protocol expect that the issuer has been setup.
 */
public class WriteCredentialDefinition {
    private WriteCredentialDefinition() {}


    /**
     * Constructor for the 0.6 WriteCredentialDefinition object. This constructor creates an object that is
     * ready to write a credential definitions the ledger
     *
     * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     * @return 0.6 WriteCredentialDefinition object
     */
    public static WriteCredentialDefinitionV0_6 v0_6(String name, String schemaId) {
        return new WriteCredentialDefinitionImplV0_6(name, schemaId);
    }

    /**
     * Constructor for the 0.6 WriteCredentialDefinition object. This constructor creates an object that is
     * ready to write a credential definitions the ledger
     *
     * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     * @param tag An optional tag for the credential definition
     * @return 0.6 WriteCredentialDefinition object
     */
    public static WriteCredentialDefinitionV0_6 v0_6(String name, String schemaId, String tag) {
        return new WriteCredentialDefinitionImplV0_6(name, schemaId, tag);
    }

    /**
     * Constructor for the 0.6 WriteCredentialDefinition object. This constructor creates an object that is
     * ready to write a credential definitions the ledger
     *
     * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     * @param revocation the revocation object defining revocation support.
     * @return 0.6 WriteCredentialDefinition object
     */
    public static WriteCredentialDefinitionV0_6 v0_6(String name, String schemaId, RevocationRegistryConfig revocation) {
        return new WriteCredentialDefinitionImplV0_6(name, schemaId, revocation);
    }

    /**
     * Constructor for the 0.6 WriteCredentialDefinition object. This constructor creates an object that is
     * ready to write a credential definitions the ledger.
     *
     * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     * @param tag An optional tag for the credential definition
     * @param revocation the revocation object defining revocation support.
     * @return 0.6 WriteCredentialDefinition object
     */
    public static WriteCredentialDefinitionV0_6 v0_6(String name, String schemaId, String tag, RevocationRegistryConfig revocation) {
        return new WriteCredentialDefinitionImplV0_6(name,schemaId, tag, revocation);
    }

}

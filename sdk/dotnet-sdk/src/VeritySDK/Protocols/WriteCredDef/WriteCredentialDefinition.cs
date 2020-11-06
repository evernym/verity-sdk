using System;
using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// Factory for the WriteCredentialDefinition protocol objects
    /// 
    /// The WriteCredentialDefinition protocol writes credential definitions to an Indy Identity Ledger
    /// (often the Sovrin Ledger). This protocol expect that the issuer has been setup.
    /// </summary>
    public class WriteCredentialDefinition
    {
        /// <summary>
        /// Constructor of object
        /// </summary>
        private WriteCredentialDefinition() { }

        /// <summary>
        /// Constructor for the 0.6 WriteCredentialDefinition object. This constructor creates an object that is
        /// ready to write a credential definitions the ledger
        /// </summary>
        /// <param name="name">The name of the new credential definition</param>
        /// <param name="schemaId">The id of the schema this credential definition will be based on</param>
        /// <returns>0.6 WriteCredentialDefinition object</returns>
        public static WriteCredentialDefinitionV0_6 v0_6(string name, string schemaId)
        {
            return new WriteCredentialDefinitionImplV0_6(name, schemaId);
        }

        /// <summary>
        /// Constructor for the 0.6 WriteCredentialDefinition object. This constructor creates an object that is
        /// ready to write a credential definitions the ledger
        /// </summary>
        /// <param name="name">The name of the new credential definition</param>
        /// <param name="schemaId">The id of the schema this credential definition will be based on</param>
        /// <param name="tag">An optional tag for the credential definition</param>
        /// <returns>0.6 WriteCredentialDefinition object</returns>
        public static WriteCredentialDefinitionV0_6 v0_6(string name, string schemaId, string tag)
        {
            return new WriteCredentialDefinitionImplV0_6(name, schemaId, tag);
        }

        /// <summary>
        /// Constructor for the 0.6 WriteCredentialDefinition object. This constructor creates an object that is
        /// ready to write a credential definitions the ledger
        /// </summary>
        /// <param name="name">The name of the new credential definition</param>
        /// <param name="schemaId">The id of the schema this credential definition will be based on</param>
        /// <param name="revocation">the revocation object defining revocation support.</param>
        /// <returns>0.6 WriteCredentialDefinition object</returns>
        public static WriteCredentialDefinitionV0_6 v0_6(string name, string schemaId, RevocationRegistryConfig revocation)
        {
            return new WriteCredentialDefinitionImplV0_6(name, schemaId, revocation);
        }

        /// <summary>
        /// Constructor for the 0.6 WriteCredentialDefinition object. This constructor creates an object that is
        /// ready to write a credential definitions the ledger
        /// </summary>
        /// <param name="name">The name of the new credential definition</param>
        /// <param name="schemaId">The id of the schema this credential definition will be based on</param>
        /// <param name="tag">An optional tag for the credential definition</param>
        /// <param name="revocation">the revocation object defining revocation support.</param>
        /// <returns>0.6 WriteCredentialDefinition object</returns>
        public static WriteCredentialDefinitionV0_6 v0_6(string name, string schemaId, string tag, RevocationRegistryConfig revocation)
        {
            return new WriteCredentialDefinitionImplV0_6(name, schemaId, tag, revocation);
        }
    }
}
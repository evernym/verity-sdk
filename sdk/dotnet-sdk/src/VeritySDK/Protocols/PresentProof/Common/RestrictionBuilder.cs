using System.Json;

namespace VeritySDK.Protocols.PresentProof
{
    /// <summary>
    /// A build to help construct a Restriction object
    /// </summary>
    public class RestrictionBuilder
    {
        /// <summary>
        /// Constructs a blank builder 
        /// </summary>
        /// <returns>a blank builder object</returns>
        public static RestrictionBuilder blank()
        {
            return new RestrictionBuilder();
        }

        /// <summary>
        /// Simple constructor
        /// </summary>
        public  RestrictionBuilder()
        {
        }

        private JsonObject data = new JsonObject();

        /// <summary>
        /// The schema id that the prevent attribute must be associated with
        /// </summary>
        /// <param name="val">the schema id</param>
        /// <returns>a partially built builder</returns>
        public RestrictionBuilder schemaId(string val)
        {
            this.data.Add("schema_id", val);
            return this;
        }

        /// <summary>
        /// The public DID of the writer of the schema on the public ledger that the attribute must be associated with 
        /// </summary>
        /// <param name="val">the DID of writer of the schema</param>
        /// <returns>a partially built builder</returns>
        public RestrictionBuilder schemaIssuerDid(string val)
        {
            this.data.Add("schema_issuer_did", val);
            return this;
        }

        /// <summary>
        /// The name of the schema that the attribute must be associated with 
        /// </summary>
        /// <param name="val">the schema name</param>
        /// <returns>a partially built builder</returns>
        public RestrictionBuilder schemaName(string val)
        {
            this.data.Add("schema_name", val);
            return this;
        }

        /// <summary>
        /// The version of the schema that the attribute must be associated with 
        /// </summary>
        /// <param name="val">the schema version</param>
        /// <returns>a partially built builder</returns>
        public RestrictionBuilder schemaVersion(string val)
        {
            this.data.Add("schema_version", val);
            return this;
        }

        /// <summary>
        /// The public DID of the issuer that the attribute must be issued from 
        /// </summary>
        /// <param name="val">the issuer's public DID</param>
        /// <returns>a partially built builder</returns>
        public RestrictionBuilder issuerDid(string val)
        {
            this.data.Add("issuer_did", val);
            return this;
        }

        /// <summary>
        /// The credential definition that the attribute must be issued from 
        /// </summary>
        /// <param name="val">the credential definition identifier</param>
        /// <returns>a partially built builder</returns>
        public RestrictionBuilder credDefId(string val)
        {
            this.data.Add("cred_def_id", val);
            return this;
        }

        /// <summary>
        /// Build the restriction 
        /// </summary>
        /// <returns>the built Restriction</returns>
        public Restriction build()
        {
            return new Restriction(data);
        }
    }
}

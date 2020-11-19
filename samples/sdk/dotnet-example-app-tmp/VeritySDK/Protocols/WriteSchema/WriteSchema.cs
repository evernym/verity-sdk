using System;
using System.Json;

namespace VeritySDK.Protocols.WriteSchema
{
    /// <summary>
    /// Factory for the WriteSchema protocol objects
    /// 
    /// The WriteSchema protocol writes schema to an Indy Identity Ledger (often the Sovrin Ledger). This protocol expect
    /// that the issuer has been setup.
    /// </summary>
    public class WriteSchema
    {
        /// <summary>
        /// Constructor
        /// </summary>
        private WriteSchema() { }

        /// <summary>
        /// Constructor for the 0.6 WriteSchema object. This constructor creates an object that is ready to write a schema the ledger. 
        /// </summary>
        /// <param name="name">The given name for the schema</param>
        /// <param name="version">The given version of the schema</param>
        /// <param name="attrs">A varargs list of attribute name in the schema</param>
        /// <returns>0.6 WriteSchema object</returns>
        public static WriteSchemaV0_6 v0_6(string name, string version, params string[] attrs)
        {
            return new WriteSchemaV0_6(name, version, attrs);
        }

    }
}
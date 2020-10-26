using System;
using System.Json;

namespace VeritySDK
{
    /**
     * Factory for the WriteSchema protocol objects
     * <p/>
     *
     * The WriteSchema protocol writes schema to an Indy Identity Ledger (often the Sovrin Ledger). This protocol expect
     * that the issuer has been setup.
     */
    public class WriteSchema {
        private WriteSchema() { }

        /**
         * Constructor for the 0.6 WriteSchema object. This constructor creates an object that is ready to write a schema
         * the ledger.
         *
         * @param name The given name for the schema
         * @param version The given version of the schema
         * @param attrs A varargs list of attribute name in the schema
         * @return 0.6 WriteSchema object
         */
        public static WriteSchemaV0_6 v0_6(string name, string version, params string[] attrs) {
            return new WriteSchemaImplV0_6(name, version, attrs);
        }

    }
}
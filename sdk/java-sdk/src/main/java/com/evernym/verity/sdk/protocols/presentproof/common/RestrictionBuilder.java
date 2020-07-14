package com.evernym.verity.sdk.protocols.presentproof.common;

import org.json.JSONObject;

/**
 * A build to help construct a Restriction object
 */
public class RestrictionBuilder {
    /**
     * Constructs a blank builder
     * @return a blank builder object
     */
    public static RestrictionBuilder blank() {
        return new RestrictionBuilder();
    }

    private RestrictionBuilder() {

    }

    private final JSONObject data = new JSONObject();

    /**
     * The schema id that the prevent attribute must be associated with
     * @param val the schema id
     * @return a partially built builder
     */
    public RestrictionBuilder schemaId(String val) {
        this.data.put("schema_id", val);
        return this;
    }

    /**
     * The public DID of the writer of the schema on the public ledger that the attribute must be associated with
     * @param val the DID of writer of the schema
     * @return a partially built builder
     */
    public RestrictionBuilder schemaIssuerDid(String val) {
        this.data.put("schema_issuer_did", val);
        return this;
    }

    /**
     * The name of the schema that the attribute must be associated with
     * @param val the schema name
     * @return a partially built builder
     */
    public RestrictionBuilder schemaName(String val) {
        this.data.put("schema_name", val);
        return this;
    }

    /**
     * The version of the schema that the attribute must be associated with
     * @param val the schema version
     * @return a partially built builder
     */
    public RestrictionBuilder schemaVersion(String val) {
        this.data.put("schema_version", val);
        return this;
    }

    /**
     * The public DID of the issuer that the attribute must be issued from
     * @param val the issuer's public DID
     * @return a partially built builder
     */
    public RestrictionBuilder issuerDid(String val) {
        this.data.put("issuer_did", val);
        return this;
    }

    /**
     * The credential definition that the attribute must be issued from
     * @param val the credential definition identifier
     * @return a partially built builder
     */
    public RestrictionBuilder credDefId(String val) {
        this.data.put("cred_def_id", val);
        return this;
    }

    /**
     * Build the restriction
     * @return the built Restriction
     */
    public Restriction build() {
        return new Restriction(data);
    }
}

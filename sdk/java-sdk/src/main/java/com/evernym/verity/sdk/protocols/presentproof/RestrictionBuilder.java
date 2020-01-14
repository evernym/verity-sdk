package com.evernym.verity.sdk.protocols.presentproof;

import org.json.JSONObject;

public class RestrictionBuilder {
    public static RestrictionBuilder start() {
        return new RestrictionBuilder();
    }

    private RestrictionBuilder() {

    }

    private JSONObject data = new JSONObject();

    public RestrictionBuilder schemaId(String val) {
        this.data.put("schema_id", val);
        return this;
    }

    public RestrictionBuilder schemaIssuerDid(String val) {
        this.data.put("schema_issuer_did", val);
        return this;
    }

    public RestrictionBuilder schemaName(String val) {
        this.data.put("schema_name", val);
        return this;
    }

    public RestrictionBuilder schemaVersion(String val) {
        this.data.put("schema_version", val);
        return this;
    }

    public RestrictionBuilder issuerDid(String val) {
        this.data.put("issuer_did", val);
        return this;
    }

    public RestrictionBuilder credDefId(String val) {
        this.data.put("cred_def_id", val);
        return this;
    }

    public Restriction build() {
        return new Restriction(data);
    }
}

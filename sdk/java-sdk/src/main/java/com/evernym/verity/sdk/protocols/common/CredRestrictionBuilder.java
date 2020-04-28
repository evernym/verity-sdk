package com.evernym.verity.sdk.protocols.common;

import java.util.Map;

public abstract class CredRestrictionBuilder<T> extends BaseMsgBuilder<T> {

    public T name(String val) {
        addToJSON("name", val);
        return self();
    }

    public T schemaId(String val) {
        addToJSON("schema_id", val);
        return self();
    }

    public T issuerDid(String val) {
        addToJSON("issuer_did", val);
        return self();
    }

    public T credDefId(String val) {
        addToJSON("cred_def_id", val);
        return self();
    }

    public T schemaIssuerDid(String val) {
        addToJSON("schema_issuer_did", val);
        return self();
    }

    public T schemaName(String val) {
        addToJSON("schema_name", val);
        return self();
    }

    public T schemaVersion(String val) {
        addToJSON("schema_version", val);
        return self();
    }

    public T credValues(Map<String, String> values) {
        addToJSON("credential_values", values);
        return self();
    }
}

package com.evernym.verity.sdk.protocols.common;

import java.util.Map;

public abstract class CredRestrictionBuilder<T> extends BaseMsgBuilder<T> {

    public T comment(String val) {
        addToJSON("comment", val);
        return self();
    }

    public T price(String val) {
        addToJSON("price", val);
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

    public T credValues(Map<String, String> values) {
        addToJSON("credential_values", values);
        return self();
    }
}

package com.evernym.verity.sdk.protocols.common;

public abstract class CredRestrictionBuilder<T> extends BaseMsgBuilder<T> {

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
}

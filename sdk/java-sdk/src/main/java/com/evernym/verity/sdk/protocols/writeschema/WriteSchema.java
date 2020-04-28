package com.evernym.verity.sdk.protocols.writeschema;

import com.evernym.verity.sdk.protocols.writeschema.v0_6.WriteSchemaV0_6;

public class WriteSchema {
    private WriteSchema() {}

    public static WriteSchemaV0_6 v0_6(String name, String version, String ...attrs) {
        return new WriteSchemaImplV0_6(name, version, attrs);
    }

}

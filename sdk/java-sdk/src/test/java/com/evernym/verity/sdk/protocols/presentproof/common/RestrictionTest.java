package com.evernym.verity.sdk.protocols.presentproof.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RestrictionTest {

    @Test
    public void buildTest() {
        Restriction test = RestrictionBuilder.blank()
                .schemaId("ASDFASDF")
                .build();

        assertEquals(test.toJson().toString(), "{\"schema_id\":\"ASDFASDF\"}");
    }

}

package com.evernym.verity.sdk.protocols.presentproof.v0_6;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RestrictionTest {

    @Test
    public void buildTest() {
        Restriction test = RestrictionBuilder.blank()
                .schemaId("ASDFASDF")
                .build();

        assertEquals(test.toJson().toString(), "{\"schema_id\":\"ASDFASDF\"}");
    }

}

package com.evernym.verity.sdk.protocols.presentproof.common;

import com.evernym.verity.sdk.protocols.presentproof.common.Restriction;
import com.evernym.verity.sdk.protocols.presentproof.common.RestrictionBuilder;
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

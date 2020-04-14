package com.evernym.verity.sdk.protocols.presentproof.v0_6;

import org.json.JSONArray;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AttributeTest {
    @Test
    public void nullTest() {
        Attribute r = new Attribute("test", (Restriction) null);
        assertEquals(0, r.data.getJSONArray("restrictions").length());
    }

    @Test
    public void noRestrictionTest() {
        Attribute r = new Attribute("test");
        assertEquals(0, r.data.getJSONArray("restrictions").length());
    }

    @Test public void oneRestrictionTest() {
        Restriction r = RestrictionBuilder.blank()
                .credDefId("SDFSDF")
                .build();
        Attribute a = new Attribute("test", r);
        JSONArray restrictions = a.data.getJSONArray("restrictions");
        assertEquals(1, restrictions.length());
        assertEquals(restrictions.toString(), "[{\"cred_def_id\":\"SDFSDF\"}]");
    }
}

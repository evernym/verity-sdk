package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.exceptions.InvalidMessageTypeException;
import com.evernym.verity.sdk.utils.Util;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageFamilyTest {
    public final static MessageFamily testFamily = new MessageFamily(){
        @Override public String qualifier() {return Util.EVERNYM_MSG_QUALIFIER;}
        @Override public String family() {return "testing";}
        @Override public String version() {return "0.1";}
    };

    @Test
    public void extractMessageName() throws InvalidMessageTypeException {
        String name = testFamily.messageName(Util.EVERNYM_MSG_QUALIFIER + "/testing/0.1/test");
        assertEquals("test", name);

        name = testFamily.messageName(Util.EVERNYM_MSG_QUALIFIER + "/testing/0.1/234asdf234@$");
        assertEquals("234asdf234@$", name);
    }

    @Test(expected = InvalidMessageTypeException.class)
    public void invalidMessageFamily() throws InvalidMessageTypeException {
        testFamily.messageName(Util.COMMUNITY_MSG_QUALIFIER + "/testing/0.1/test");
    }

    @Test(expected = InvalidMessageTypeException.class)
    public void invalidMessageName() throws InvalidMessageTypeException {
        testFamily.messageName(Util.EVERNYM_MSG_QUALIFIER + "/testing/0.1/");
    }

    @Test(expected = InvalidMessageTypeException.class)
    public void invalidMessageName2() throws InvalidMessageTypeException {
        testFamily.messageName(Util.EVERNYM_MSG_QUALIFIER + "c/testing/0.1-test");
    }
}

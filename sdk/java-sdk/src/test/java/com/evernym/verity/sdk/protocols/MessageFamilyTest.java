package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.exceptions.InvalidMessageTypeException;
import com.evernym.verity.sdk.utils.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void invalidMessageFamily() throws InvalidMessageTypeException {
        Assertions.assertThrows(InvalidMessageTypeException.class, () -> {
            testFamily.messageName(Util.COMMUNITY_MSG_QUALIFIER + "/testing/0.1/test");
        });
    }

    @Test
    public void invalidMessageName() throws InvalidMessageTypeException {
        Assertions.assertThrows(InvalidMessageTypeException.class, () -> {
            testFamily.messageName(Util.EVERNYM_MSG_QUALIFIER + "/testing/0.1/");
        });
    }

    @Test
    public void invalidMessageName2() throws InvalidMessageTypeException {
        Assertions.assertThrows(InvalidMessageTypeException.class, () -> {
            testFamily.messageName(Util.EVERNYM_MSG_QUALIFIER + "c/testing/0.1-test");
        });
    }
}

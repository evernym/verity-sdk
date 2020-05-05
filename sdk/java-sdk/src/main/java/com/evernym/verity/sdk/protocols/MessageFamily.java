package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.exceptions.InvalidMessageTypeException;

public interface MessageFamily {

    String qualifier();
    String family();
    String version();

    default boolean matches(String qualifiedMessageType) {
        if(qualifiedMessageType == null) return false;
        return qualifiedMessageType.startsWith(getMessageFamily());
    }

    default String messageName(String qualifiedMessageType) throws InvalidMessageTypeException {
        if(!matches(qualifiedMessageType)) {
            throw new InvalidMessageTypeException("Given qualified message type does not match this MessageFamily");
        }

        String messageFamily = getMessageFamily();

        if(qualifiedMessageType.charAt(messageFamily.length()) != '/'
                || !(qualifiedMessageType.length() > messageFamily.length() + 1)) {
            throw new InvalidMessageTypeException("Given qualified message type does not have a message name");
        }

        return qualifiedMessageType.substring(messageFamily.length()+1);
    }

    default String getMessageFamily() {
        return qualifier() + ";spec/" + family() + "/" + version();
    }

    default String getMessageType(String msgName) {
       return getMessageFamily() + "/" + msgName;
    }
}

package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.exceptions.InvalidMessageTypeException;

/**
 * Interface for message families. Message families are the set of messages used by a protocol. They include
 * three types of messages: protocol messages, control messages and signal messages.
 * <p/>
 * Protocol messages are messages exchange between parties of the protocol. Each party is an independent self-sovereign
 * domain.
 * <p/>
 * Control messages are messages sent by a controller (applications that use verity-sdk are controllers) to the verity
 * application. These messages control the protocol and make decisions for the protocol.
 * <p/>
 * Signal messages are messages sent from the verity-application agent to a controller
 * <p/>
 * Message family messages always have a type. This type has 4 parts: qualifier, family, version and name. Three parts
 * are static and defined by this interface.
 * <p/>
 * Example: did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/problem_report
 * <p/>
 * qualifier: did:sov:BzCbsNYhMrjHiqZDTUASHg
 * <br/>
 * family: connections
 * <br/>
 * version: 1.0
 * <br/>
 * name: problem_report
 *
 */
public interface MessageFamily {

    /**
     * @return the qualifier for the message family
     */
    String qualifier();

    /**
     * @return the family name for the message family
     */
    String family();

    /**
     * @return the version for the message family
     */
    String version();

    /**
     * Tests if a message type matches the message family defined by the interface
     * @param qualifiedMessageType a message type that tested
     * @return true if the given message type matches otherwise returns false
     */
    default boolean matches(String qualifiedMessageType) {
        if(qualifiedMessageType == null) return false;
        return qualifiedMessageType.startsWith(getMessageFamily());
    }

    /**
     * Parse out the message name from the message type
     * @param qualifiedMessageType a message type that is parsed
     * @return the parsed message name
     * @throws InvalidMessageTypeException thrown the given message type is un-parseable
     */
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

    /**
     * Combines the element of this message family into a prefix of the message type, without the message name
     * @return a prefix portion of the message type
     */
    default String messageFamily() {
        return qualifier() + ";spec/" + family() + "/" + version();
    }

    /**
     * @deprecated replaced by {@link #messageFamily()}
     */
    @Deprecated
    default String getMessageFamily() {
        return messageFamily();
    }

    /**
     * Combines the element of this message family with the given message name to build a fully qualified message type
     * @param msgName the message name used in the built message type
     * @return fully qualified message type
     */
    default String messageType(String msgName) {
        return getMessageFamily() + "/" + msgName;
    }

    /**
     * @deprecated replaced by {@link #messageType(String)}
     */
    @Deprecated
    default String getMessageType(String msgName) {
       return messageType(msgName);
    }
}

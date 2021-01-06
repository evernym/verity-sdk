package com.evernym.verity.sdk.protocols.basicmessage;

import com.evernym.verity.sdk.protocols.basicmessage.v1_0.BasicMessageV1_0;


/**
 * Factory for BasicMessage protocol objects.
 * <p/>
 *
 * The BasicMessage protocol allows one self-sovereign party send another self-sovereign party a message.
 * Support for this protocol is EXPERIMENTAL.
 * This protocol is not implemented in the Connect.Me app and the only way it can be used is by building the mobile app using mSDK or using some other Aries compatible wallet that supports BasicMessage
 */
public class BasicMessage {
    private BasicMessage() {}
    /**
     * Constructor for the 1.0 BasicMessage object. This constructor creates an object that is ready to send
     * the given message
     *
     * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
     * @param content The main text of the message
     * @param sent_time The time the message was sent
     * @param localization Language localization code
     * @return 1.0 BasicMessage object
     */
    public static BasicMessageV1_0 v1_0(String forRelationship,
                                           String content,
                                           String sent_time,
                                           String localization) {
        return new BasicMessageImplV1_0(forRelationship, content, sent_time, localization);
    }
}

package com.evernym.verity.sdk.protocols.basicmessage;

import com.evernym.verity.sdk.protocols.basicmessage.v1_0.BasicMessageV1_0;


/**
 * Factory for BasicMessage protocol objects.
 * <p/>
 *
 * The BasicMessage protocol allows one self-sovereign party send another self-sovereign a message.
 *
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
     * @param localization Locale data for localization vector
     * @return 1.0 BasicMessage object
     */
    public static BasicMessageV1_0 v1_0(String forRelationship,
                                           String content,
                                           String sent_time,
                                           String localization) {
        return new BasicMessageImplV1_0(forRelationship, content, sent_time, localization);
    }
}

package com.evernym.verity.sdk.protocols.outofband.v1_0;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

public interface OutOfBandV1_0 extends MessageFamily {
    /**
     * The qualifier for the message family. Uses the community qualifier.
     */
    String QUALIFIER = Util.COMMUNITY_MSG_QUALIFIER;
    /**
     * The name for the message family.
     */
    String FAMILY = "out-of-band";
    /**
     * The version for the message family.
     */
    String VERSION = "1.0";

    /**
     * @see MessageFamily#qualifier()
     */
    default String qualifier() {return QUALIFIER;}
    /**
     * @see MessageFamily#family()
     */
    default String family() { return FAMILY;}
    /**
     * @see MessageFamily#version()
     */
    default String version() {return VERSION;}

    /**
     * Direct the verity-application agent to reuse the relationship given.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void handshakeReuse(Context context) throws IOException, VerityException;
    /**
     * Creates the control message without packaging and sending it.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #handshakeReuse
     */
    JSONObject handshakeReuseMsg(Context context) throws IOException, VerityException;
    /**
     * Creates and packages message without sending it.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #handshakeReuse
     */
    byte[] handshakeReuseMsgPacked(Context context) throws IOException, VerityException;
}

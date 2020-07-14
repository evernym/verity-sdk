package com.evernym.verity.sdk.protocols.updateconfigs.v0_6;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

/**
 * An interface for controlling a 0.6 UpdateConfigs protocol.
 */
public interface UpdateConfigsV0_6 extends MessageFamily {

    /**
     * The qualifier for the message family. Uses Evernym's qualifier.
     */
    String QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
    /**
     * The name for the message family.
     */
    String FAMILY = "update-configs";
    /**
     * The version for the message family.
     */
    String VERSION = "0.6";


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
     * Directs verity-application to update the configuration register with the verity-application
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void update(Context context) throws IOException, VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #updateMsg
     */
    JSONObject updateMsg(Context context) throws UndefinedContextException;

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #updateMsg
     */
    byte[] updateMsgPacked(Context context) throws VerityException;

    /**
     * Ask for status from the verity-application agent
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void status(Context context) throws IOException, VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #status
     */
    JSONObject statusMsg(Context context) throws VerityException;

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #status
     */
    byte[] statusMsgPacked(Context context) throws VerityException;
}
package com.evernym.verity.sdk.protocols.provision.v0_7;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

/**
 * An interface for controlling a 0.7 Provision protocol.
 */
public interface ProvisionV0_7 extends Protocol {
    /**
     * The qualifier for the message family. Uses Evernym's qualifier.
     */
    String QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
    /**
     * The name for the message family.
     */
    String FAMILY = "agent-provisioning";
    /**
     * The version for the message family.
     */
    String VERSION = "0.7";


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
     Name for 'create-edge-agent' control message
     */
    String CREATE_EDGE_AGENT = "create-edge-agent";

    /**
     * Sends provisioning message that directs the creation of an agent to the to verity-application
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     * @return new Context with provisioned details
     */
    Context provision(Context context) throws IOException, VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #provision
     */
    JSONObject provisionMsg(Context context) throws VerityException;

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #provision
     */
    byte[] provisionMsgPacked(Context context) throws VerityException;
}

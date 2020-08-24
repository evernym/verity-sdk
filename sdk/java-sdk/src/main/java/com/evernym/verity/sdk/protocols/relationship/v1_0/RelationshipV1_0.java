package com.evernym.verity.sdk.protocols.relationship.v1_0;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

/**
 * An interface for controlling a 1.0 Relationship protocol.
 */
public interface RelationshipV1_0 extends Protocol {
    /**
     * The qualifier for the message family. Uses Evernym's qualifier.
     */
    String QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
    /**
     * The name for the message family.
     */
    String FAMILY = "relationship";
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
     * Directs verity-application to create a new relationship
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void create(Context context) throws IOException, VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #create
     */
    JSONObject createMsg(Context context) throws IOException, VerityException;
    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #create
     */
    byte[] createMsgPacked(Context context) throws IOException, VerityException;

    /**
     * Ask for aries invitation from the verity-application agent for the relationship created by this protocol
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void connectionInvitation(Context context) throws IOException, VerityException;

    /**
     * Ask for aries invitation from the verity-application agent for the relationship created by this protocol
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param shortInvite decides should short invite be provided as well
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void connectionInvitation(Context context, Boolean shortInvite) throws IOException, VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #connectionInvitation
     */
    JSONObject connectionInvitationMsg(Context context) throws VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param shortInvite decides should short invite be provided as well
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #connectionInvitation
     */
    JSONObject connectionInvitationMsg(Context context, Boolean shortInvite) throws VerityException;

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #connectionInvitation
     */
    byte[] connectionInvitationMsgPacked(Context context) throws VerityException;

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param shortInvite decides should short invite be provided as well
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #connectionInvitation
     */
    byte[] connectionInvitationMsgPacked(Context context, Boolean shortInvite) throws VerityException;

    /**
     * Ask for aries out of band invitation from the verity-application agent for the relationship created by this protocol
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void outOfBandInvitation(Context context) throws IOException, VerityException;

    /**
     * Ask for aries out of band invitation from the verity-application agent for the relationship created by this protocol
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param shortInvite decides should short invite be provided as well
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void outOfBandInvitation(Context context, Boolean shortInvite) throws IOException, VerityException;


    /**
     * Ask for aries out of band invitation from the verity-application agent for the relationship created by this protocol
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param shortInvite decides should short invite be provided as well
     * @param goal the initial intended goal of the relationship (this goal is expressed in the invite)
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void outOfBandInvitation(Context context, Boolean shortInvite, GoalCode goal) throws IOException, VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #connectionInvitation
     */
    JSONObject outOfBandInvitationMsg(Context context) throws VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param shortInvite decides should short invite be provided as well
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #connectionInvitation
     */
    JSONObject outOfBandInvitationMsg(Context context, Boolean shortInvite) throws VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param shortInvite decides should short invite be provided as well
     * @param goal the initial intended goal of the relationship (this goal is expressed in the invite)
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #connectionInvitation
     */
    JSONObject outOfBandInvitationMsg(Context context, Boolean shortInvite, GoalCode goal) throws VerityException;

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #connectionInvitation
     */
    byte[] outOfBandInvitationMsgPacked(Context context) throws VerityException;

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param shortInvite decides should short invite be provided as well
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #connectionInvitation
     */
    byte[] outOfBandInvitationMsgPacked(Context context, Boolean shortInvite) throws VerityException;

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param shortInvite decides should short invite be provided as well
     * @param goal the initial intended goal of the relationship (this goal is expressed in the invite)
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #connectionInvitation
     */
    byte[] outOfBandInvitationMsgPacked(Context context, Boolean shortInvite, GoalCode goal) throws VerityException;
}

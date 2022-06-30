package com.evernym.verity.sdk.protocols.issuersetup.v0_7;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

/**
 * An interface for controlling a 0.7 IssuerSetup protocol.
 */
public interface IssuerSetupV0_7 extends Protocol {
    /**
     * @see MessageFamily#qualifier()
     */
    default String qualifier() {return Util.EVERNYM_MSG_QUALIFIER;}
    /**
     * @see MessageFamily#family()
     */
    default String family() {return "issuer-setup";}
    /**
     * @see MessageFamily#version()
     */
    default String version() {return "0.7";}


    /**
    Name for 'create' control message
     */
    String CREATE = "create";

    /**
     Name for 'current-public-identifier' control message
     */
    String CURRENT_PUBLIC_IDENTIFIER = "current-public-identifier";


    /**
     * Directs verity-application to start and create an issuer identity and set it up
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param ledgerPrefix a string indicating the ledger that the issuer identifier should be created for. Currently supported values are ["did:sov", "did:indy:sovrin:builder", "did:indy:sovrin:staging", "did:indy:sovrin"]
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void create(Context context, String ledgerPrefix) throws IOException, VerityException;

    /**
     * Directs verity-application to start and create an issuer identity and set it up
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param ledgerPrefix a string indicating the ledger that the issuer identifier should be created for. Currently supported values are ["did:sov", "did:indy:sovrin:builder", "did:indy:sovrin:staging", "did:indy:sovrin"]
     * @param endorser Optional: the desired endorser did. If left empty then Verity will attempt to use it's own endorser, otherwise it will return a transaction for manual endorsement
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void create(Context context, String ledgerPrefix, String endorser) throws IOException, VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param ledgerPrefix a string indicating the ledger that the issuer identifier should be created for. Currently supported values are ["did:sov", "did:indy:sovrin:builder", "did:indy:sovrin:staging", "did:indy:sovrin"]
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #create
     */
    JSONObject createMsg(Context context, String ledgerPrefix) throws VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param ledgerPrefix a string indicating the ledger that the issuer identifier should be created for. Currently supported values are ["did:sov", "did:indy:sovrin:builder", "did:indy:sovrin:staging", "did:indy:sovrin"]
     * @param endorser Optional: the desired endorser did. If left empty then Verity will attempt to use it's own endorser, otherwise it will return a transaction for manual endorsement
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #create
     */
    JSONObject createMsg(Context context, String ledgerPrefix, String endorser) throws VerityException;

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param ledgerPrefix a string indicating the ledger that the issuer identifier should be created for. Currently supported values are ["did:sov", "did:indy:sovrin:builder", "did:indy:sovrin:staging", "did:indy:sovrin"]
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #create
     */
    byte[]  createMsgPacked(Context context, String ledgerPrefix) throws VerityException;

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param ledgerPrefix a string indicating the ledger that the issuer identifier should be created for. Currently supported values are ["did:sov", "did:indy:sovrin:builder", "did:indy:sovrin:staging", "did:indy:sovrin"]
     * @param endorser Optional: the desired endorser did. If left empty then Verity will attempt to use it's own endorser, otherwise it will return a transaction for manual endorsement
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #create
     */
    byte[]  createMsgPacked(Context context, String ledgerPrefix, String endorser) throws VerityException;

    /**
     * Asks the verity-application for the current issuer identity that is setup.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void currentPublicIdentifier(Context context) throws IOException, VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #currentPublicIdentifier
     */
    JSONObject currentPublicIdentifierMsg(Context context);

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #currentPublicIdentifier
     */
    byte[]  currentPublicIdentifierMsgPacked(Context context) throws VerityException;
}

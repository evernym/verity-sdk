package com.evernym.verity.sdk.protocols.presentproof.v1_0;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.presentproof.common.Attribute;
import com.evernym.verity.sdk.protocols.presentproof.common.Predicate;
import com.evernym.verity.sdk.protocols.presentproof.common.ProposedAttribute;
import com.evernym.verity.sdk.protocols.presentproof.common.ProposedPredicate;
import com.evernym.verity.sdk.protocols.presentproof.common.Restriction;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

/**
 * An interface for controlling a 1.0 PresentProof protocol.
 *
 * @see <a href="https://github.com/hyperledger/aries-rfcs/tree/4fae574c03f9f1013db30bf2c0c676b1122f7149/features/0037-present-proof" target="_blank" rel="noopener noreferrer">Aries 0037: Present Proof Protocol 1.0</a>
 */
@SuppressWarnings("CPD-START")
public interface PresentProofV1_0 extends Protocol {
    /**
     * The qualifier for the message family. Uses the community qualifier.
     */
    String QUALIFIER = Util.COMMUNITY_MSG_QUALIFIER;
    /**
     * The name for the message family.
     */
    String FAMILY = "present-proof";
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
     * Creates an attribute restriction for a presentation of proof
     * @param name the attribute name to apply the restriction
     * @param restrictions an array restrictions to be used
     * @return An Attribute with the given name and restrictions
     */
    static Attribute attribute(String name, Restriction... restrictions) {
        return new Attribute(name, restrictions);
    }

    /**
     * Creates a predicate restriction for a presentation of proof. Indy Anoncreds only supports, so the
     * value should be expressed as a greater than predicate.
     *
     * @param name the attribute name to apply the restriction
     * @param value the value the attribute must be greater than
     * @param restrictions an array restrictions to be used
     * @return A Predicate with the given name, value and restrictions
     */
    static Predicate predicate(String name, int value, Restriction... restrictions) {
        return new Predicate(name, value, restrictions);
    }

    /**
     * Creates a proposed attribute value for a presentation of proof.
     *
     * @param name the attribute name
     * @param credDefId cred def id of credential being used for this attribute. If null, than self attested.
     * @param value the value of the attribute. If null, then value want be revealed.
     * @return A ProposedAttribute with the given name, credDefId and value
     */
    static ProposedAttribute proposedAttribute(String name, String credDefId, String value) {
        return new ProposedAttribute(name, credDefId, value);
    }

    /**
     * Creates a proposed predicate for a presentation of proof. Indy Anoncreds only supports, so the
     * value should be expressed as a greater than predicate.
     *
     * @param name the predicate name
     * @param credDefId cred def id of credential being used for proposed predicate.
     * @param treshold the value the predicate must be greater than
     * @return A ProposedPredicate with the given name, credDefId, predicate type and treshold
     */
    static ProposedPredicate proposedPredicate(String name, String credDefId, int treshold) {
        return new ProposedPredicate(name, credDefId, treshold);
    }

    /**
     * Directs verity-application to request a presentation of proof.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void request(Context context) throws IOException, VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #request
     */
    JSONObject requestMsg(Context context) throws VerityException;

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #request
     */
    byte[] requestMsgPacked(Context context) throws VerityException;

    /**
     * Directs verity-application to propose a presentation of proof.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void propose(Context context) throws IOException, VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #propose
     */
    JSONObject proposeMsg(Context context) throws VerityException;

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #propose
     */
    byte[] proposeMsgPacked(Context context) throws VerityException;

    /**
     * Directs verity-application to accept the request to present proof.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void acceptRequest(Context context) throws IOException, VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #acceptRequest
     */
    JSONObject acceptRequestMsg(Context context) throws VerityException;

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #acceptRequest
     */
    byte[] acceptRequestMsgPacked(Context context) throws VerityException;

    /**
     * Directs verity-application to accept the proposal for present proof.
     * verity-application will send presentation request based on the proposal.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void acceptProposal(Context context) throws IOException, VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #acceptProposal
     */
    JSONObject acceptProposalMsg(Context context) throws VerityException;

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #acceptProposal
     */
    byte[] acceptProposalMsgPacked(Context context) throws VerityException;


    /**
     * Directs verity-application to reject this presentation proof protocol
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param reason an human readable reason for the rejection
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void reject(Context context, String reason) throws IOException, VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #reject
     */
    JSONObject rejectMsg(Context context, String reason) throws VerityException, IOException;

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #reject
     */
    byte[] rejectMsgPacked(Context context, String reason) throws VerityException, IOException;

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
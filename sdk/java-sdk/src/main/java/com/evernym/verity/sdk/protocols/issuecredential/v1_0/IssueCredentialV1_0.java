package com.evernym.verity.sdk.protocols.issuecredential.v1_0;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

/**
 * An interface for controlling a 1.0 IssueCredential protocol.
 *
 * @see <a href="https://github.com/hyperledger/aries-rfcs/tree/bb42a6c35e0d5543718fb36dd099551ab192f7b0/features/0036-issue-credential" target="_blank" rel="noopener noreferrer">Aries 0036: Issue Credential Protocol 1.0</a>
 */
public interface IssueCredentialV1_0 extends MessageFamily {
    /**
     * The qualifier for message family. Uses the community qualifier.
     */
    String QUALIFIER = Util.COMMUNITY_MSG_QUALIFIER;
    /**
     * The name for message family.
     */
    String FAMILY = "issue-credential";
    /**
     * The version for message family.
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

//    /**
//     * Directs verity-application to send credential proposal.
//     * @param context an instance of the Context object initialized to a verity-application agent
//     * @throws IOException when the HTTP library fails to post to the agency endpoint
//     * @throws VerityException when wallet operations fails or given invalid context
//     */
//    void proposeCredential(Context context) throws IOException, VerityException;
//
//    /**
//     * Creates the control message without packaging and sending it.
//     * @param context an instance of the Context object initialized to a verity-application agent
//     * @return the constructed message (JSON object)
//     * @throws VerityException when given invalid context
//     *
//     * @see #proposeCredential
//     */
//    JSONObject proposeCredentialMsg(Context context) throws VerityException;
//
//    /**
//     * Creates and packages message without sending it.
//     * @param context an instance of the Context object initialized to a verity-application agent
//     * @return the byte array ready for transport
//     * @throws VerityException when wallet operations fails or given invalid context
//     *
//     * @see #proposeCredential
//     */
//    byte[] proposeCredentialMsgPacked(Context context) throws VerityException;

    /**
     * Directs verity-application to send credential offer.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void offerCredential(Context context) throws IOException, VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #offerCredential
     */
    JSONObject offerCredentialMsg(Context context) throws VerityException;

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #offerCredential
     */
    byte[] offerCredentialMsgPacked(Context context) throws VerityException;

//    /**
//     * Directs verity-application to send credential request.
//     *
//     * @param context
//     * @throws IOException
//     * @throws VerityException
//     */
//    void requestCredential(Context context) throws IOException, VerityException;
//
//    /**
//     * Creates the control message without packaging and sending it.
//     * @param context an instance of the Context object initialized to a verity-application agent
//     * @return the constructed message (JSON object)
//     * @throws VerityException when given invalid context
//     *
//     * @see #requestCredential
//     */
//    JSONObject requestCredentialMsg(Context context) throws VerityException;
//
//    /**
//     * Creates and packages message without sending it.
//     * @param context an instance of the Context object initialized to a verity-application agent
//     * @return the byte array ready for transport
//     * @throws VerityException when wallet operations fails or given invalid context
//     *
//     * @see #requestCredential
//     */
//    byte[] requestCredentialMsgPacked(Context context) throws VerityException;

    /**
     * Directs verity-application to issue credential and send it
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void issueCredential(Context context) throws IOException, VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #issueCredential
     */
    JSONObject issueCredentialMsg(Context context) throws VerityException;

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #issueCredential
     */
    byte[] issueCredentialMsgPacked(Context context) throws VerityException;

    /**
     * Directs verity-application to reject the credential protocol
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void reject(Context context) throws IOException, VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #reject
     */
    JSONObject rejectMsg(Context context) throws VerityException;

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #reject
     */
    byte[] rejectMsgPacked(Context context) throws VerityException;

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
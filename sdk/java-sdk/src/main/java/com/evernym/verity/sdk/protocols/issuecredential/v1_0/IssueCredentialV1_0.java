package com.evernym.verity.sdk.protocols.issuecredential.v1_0;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

public interface IssueCredentialV1_0 extends MessageFamily {
    String QUALIFIER = Util.COMMUNITY_MSG_QUALIFIER;
    String FAMILY = "issue-credential";
    String VERSION = "1.0";

    default String qualifier() {return QUALIFIER;}
    default String family() { return FAMILY;}
    default String version() {return VERSION;}

    /**
     * Sends the proposal message to the connection
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    void proposeCredential(Context context) throws IOException, VerityException;

    JSONObject proposeCredentialMsg(Context context) throws VerityException;

    byte[] proposeCredentialMsgPacked(Context context) throws VerityException;

    /**
     * Sends the credential offer message to the connection
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    void offerCredential(Context context) throws IOException, VerityException;

    JSONObject offerCredentialMsg(Context context) throws VerityException;

    byte[] offerCredentialMsgPacked(Context context) throws VerityException;

    /**
     * @param context
     * @throws IOException
     * @throws VerityException
     */
    void requestCredential(Context context) throws IOException, VerityException;

    JSONObject requestCredentialMsg(Context context) throws VerityException;

    byte[] requestCredentialMsgPacked(Context context) throws VerityException;

    /**
     * Sends the issue credential message to the connection
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    void issueCredential(Context context) throws IOException, VerityException;

    JSONObject issueCredentialMsg(Context context) throws VerityException;

    byte[] issueCredentialMsgPacked(Context context) throws VerityException;


    /**
     * Reject the credential
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    void reject(Context context) throws IOException, VerityException;

    JSONObject rejectMsg(Context context) throws VerityException;

    byte[] rejectMsgPacked(Context context) throws VerityException;

    /**
     * Sends the get status message to the connection
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    void status(Context context) throws IOException, VerityException;

    JSONObject statusMsg(Context context) throws VerityException;

    byte[] statusMsgPacked(Context context) throws VerityException;
}
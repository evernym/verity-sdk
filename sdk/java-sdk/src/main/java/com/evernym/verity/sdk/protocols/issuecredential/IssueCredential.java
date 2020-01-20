package com.evernym.verity.sdk.protocols.issuecredential;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public interface IssueCredential extends MessageFamily {
    default String qualifier() {return Util.EVERNYM_MSG_QUALIFIER;}
    default String family() {return "issue-credential";}
    default String version() {return "0.6";}


    String OFFER_CREDENTIAL = "send-offer";
    String ISSUE_CREDENTIAL = "issue-credential";
    String GET_STATUS = "get-status";

    static IssueCredential v0_6(String forRelationship,
                                String name,
                                Map<String, String> values,
                                String credDefId) {
        return new IssueCredentialImpl(forRelationship, name, values, credDefId);
    }

    static IssueCredential v0_6(String forRelationship,
                                String threadId) {
        return new IssueCredentialImpl(forRelationship, threadId);
    }


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
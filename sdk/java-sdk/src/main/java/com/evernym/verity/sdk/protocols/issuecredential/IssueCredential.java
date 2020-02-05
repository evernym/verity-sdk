package com.evernym.verity.sdk.protocols.issuecredential;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.protocols.issuecredential.v_0_6.IssueCredentialImpl;
import com.evernym.verity.sdk.protocols.issuecredential.v_1_0.cred_preview.CredPreviewAttribute;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IssueCredential extends MessageFamily {


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

    static IssueCredential v1_0(String forRelationship, List<CredPreviewAttribute> attributes,
                                String comment, String schemaIssuerId, String schemaId, String schemaName,
                                String schemaVersion, String credDefId, String issuerDID) {
        return new com.evernym.verity.sdk.protocols.issuecredential.v_1_0.IssueCredentialImpl(
                forRelationship, attributes, comment, schemaIssuerId, schemaId, schemaName, schemaVersion, credDefId, issuerDID);
    }

    static IssueCredential v1_0(String forRelationship,
                                String threadId) {
        return new com.evernym.verity.sdk.protocols.issuecredential.v_1_0.IssueCredentialImpl(forRelationship, threadId);
    }

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
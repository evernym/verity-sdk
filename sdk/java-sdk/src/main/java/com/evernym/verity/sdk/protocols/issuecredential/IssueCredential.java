package com.evernym.verity.sdk.protocols.issuecredential;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.protocols.issuecredential.v_0_6.IssueCredential_0_6;
import com.evernym.verity.sdk.protocols.issuecredential.v_1_0.IssueCredential_1_0;
import com.evernym.verity.sdk.protocols.issuecredential.v_1_0.cred_preview.CredPreviewAttribute;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IssueCredential extends MessageFamily {


    static IssueCredential_0_6 v0_6(String forRelationship,
                                String name,
                                Map<String, String> values,
                                String credDefId) {
        return new IssueCredential_0_6(forRelationship, name, values, credDefId);
    }

    static IssueCredential_0_6 v0_6(String forRelationship,
                                String threadId) {
        return new IssueCredential_0_6(forRelationship, threadId);
    }

    static IssueCredential_1_0 v1_0(String forRelationship, String name, String credDefId,
                                    List<CredPreviewAttribute> attributes, String comment,
                                    String schemaIssuerId, String schemaId, String schemaName,
                                    String schemaVersion, String issuerDID, String price) {
        return new IssueCredential_1_0(
                forRelationship, name, credDefId, attributes, comment, schemaIssuerId,
                schemaId, schemaName, schemaVersion, issuerDID, price);
    }

    static IssueCredential_1_0 v1_0(String forRelationship,
                                String threadId) {
        return new IssueCredential_1_0(forRelationship, threadId);
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
package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Builds and sends a message asking Verity to issue a credential to a connection
 */
public class IssueCredential extends Protocol {

    final private static String MSG_QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
    private static String MSG_FAMILY = "issue-credential";
    private static String MSG_FAMILY_VERSION = "0.6";

    // Messages
    @SuppressWarnings("WeakerAccess")
    public static String OFFER_CREDENTIAL = "send-offer";
    public static String ISSUE_CREDENTIAL = "issue-credential";
    public static String GET_STATUS = "get-status";

    // Status definitions
    public static Integer OFFER_SENT_STATUS = 0;
    public static Integer OFFER_ACCEPTED_BY_USER_STATUS = 1;
    public static Integer CREDENTIAL_SENT_TO_USER_STATUS = 2;

    String forRelationship;
    String credentialName;
    String credDefId;
    JSONObject credentialValues;
    String price;

    /**
     * Creates a new credential
     * @param credentialName The name of credential.
     * @param credDefId The credDefId of the credential definition being used
     * @param credentialValues key-value pairs of credential attribute fields with the specified params defined in the credential definition
     * @param price The cost of the credential for the user.
     */
    @SuppressWarnings("WeakerAccess")
    public IssueCredential(String forRelationship, String credentialName, String credDefId, JSONObject credentialValues, String price) {
        super();
        this.forRelationship = forRelationship;
        this.credentialName = credentialName;
        this.credDefId = credDefId;
        this.credentialValues = credentialValues;
        this.price = price;
        defineMessages();
    }

    public static String getMessageType(String msgName) {
        return Util.getMessageType(MSG_QUALIFIER, MSG_FAMILY, MSG_FAMILY_VERSION, msgName);
    }

    public static String getProblemReportMessageType() {
        return Util.getProblemReportMessageType(MSG_QUALIFIER, MSG_FAMILY, MSG_FAMILY_VERSION);
    }

    public static String getStatusMessageType() {
        return Util.getStatusMessageType(MSG_QUALIFIER, MSG_FAMILY, MSG_FAMILY_VERSION);
    }

    @Override
    protected void defineMessages() {
        JSONObject offerMessage = new JSONObject();
        offerMessage.put("@type", IssueCredential.getMessageType(OFFER_CREDENTIAL));
        offerMessage.put("@id", IssueCredential.getNewId());
        addThread(offerMessage);
        offerMessage.put("~for_relationship", this.forRelationship);
        offerMessage.put("name", this.credentialName);
        offerMessage.put("credDefId", this.credDefId);
        offerMessage.put("credentialValues", this.credentialValues);
        offerMessage.put("price", this.price);
        this.messages.put(OFFER_CREDENTIAL, offerMessage);

        JSONObject issueMessage = new JSONObject();
        issueMessage.put("@type", IssueCredential.getMessageType(ISSUE_CREDENTIAL));
        issueMessage.put("@id", IssueCredential.getNewId());
        addThread(issueMessage);
        issueMessage.put("~for_relationship", this.forRelationship);
        this.messages.put(ISSUE_CREDENTIAL, issueMessage);

        JSONObject statusMessage = new JSONObject();
        statusMessage.put("@type", IssueCredential.getMessageType(GET_STATUS));
        statusMessage.put("@id", IssueCredential.getNewId());
        addThread(statusMessage);
        statusMessage.put("~for_relationship", this.forRelationship);
        this.messages.put(GET_STATUS, statusMessage);
    }


    /**
     * Sends the credential offer message to the connection
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    @SuppressWarnings("WeakerAccess")
    public byte[] offerCredential(Context context) throws IOException, UndefinedContextException, WalletException {
        return this.send(context, this.messages.getJSONObject(OFFER_CREDENTIAL));
    }

    /**
     * Sends the issue credential message to the connection
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    @SuppressWarnings("WeakerAccess")
    public byte[] issueCredential(Context context) throws IOException, UndefinedContextException, WalletException {
        return this.send(context, this.messages.getJSONObject(ISSUE_CREDENTIAL));
    }

    /**
     * Sends the get status message to the connection
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    public byte[] status(Context context) throws IOException, UndefinedContextException, WalletException {
        return this.send(context, this.messages.getJSONObject(GET_STATUS));
    }
}
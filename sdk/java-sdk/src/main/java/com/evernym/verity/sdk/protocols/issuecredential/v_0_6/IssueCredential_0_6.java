package com.evernym.verity.sdk.protocols.issuecredential.v_0_6;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.issuecredential.IssueCredential;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * Builds and sends a message asking Verity to issue a credential to a connection
 */
public class IssueCredential_0_6 extends Protocol implements IssueCredential {

    public String qualifier() {return Util.EVERNYM_MSG_QUALIFIER;}
    public String family() { return "issue-credential";}
    public String version() {return "0.6";}

    String OFFER_CREDENTIAL = "send-offer";
    String ISSUE_CREDENTIAL = "issue-credential";
    String GET_STATUS = "get-status";

    // flag if this instance started the interaction
    boolean created = false;

    String forRelationship;
    String name;
    String credDefId;
    Map<String, String> values;
    String price;

    //FIXME: only needed 'Undefined interface' in agency?
    public IssueCredential_0_6() {
    }

    /**
     * Creates a new credential
     * @param name The name of credential.
     * @param credDefId The credDefId of the credential definition being used
     * @param values key-value pairs of credential attribute fields with the specified params defined in the credential definition
     */
    public IssueCredential_0_6(String forRelationship, String name, Map<String, String> values, String credDefId) {
        super();
        this.forRelationship = forRelationship;
        this.name = name;
        this.credDefId = credDefId;
        this.values = values;
        this.price = "0";
        this.created = true;
    }

    public IssueCredential_0_6(String forRelationship, String threadId) {
        super(threadId);
        this.forRelationship = forRelationship;
    }

    /**
     * Sends the proposal message to the connection
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     */

    public void proposeCredential(Context context) {
        throw new UnsupportedOperationException();
    }

    public JSONObject proposeCredentialMsg(Context context) {
        throw new UnsupportedOperationException();
    }

    public byte[] proposeCredentialMsgPacked(Context context) {
        throw new UnsupportedOperationException();
    }

    /**
     * Sends the credential offer message to the connection
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    @SuppressWarnings("WeakerAccess")
    public void offerCredential(Context context) throws IOException, VerityException {
        send(context, offerCredentialMsg(context));
    }

    @Override
    public JSONObject offerCredentialMsg(Context context) {
        if(!created) {
            throw new IllegalArgumentException("Unable to offer credentials when NOT starting the interaction");
        }

        JSONObject msg = new JSONObject();
        msg.put("@type", getMessageType(OFFER_CREDENTIAL));
        msg.put("@id", getNewId());
        addThread(msg);
        msg.put("~for_relationship", forRelationship);
        msg.put("name", name);
        msg.put("credDefId", credDefId);
        msg.put("credentialValues", values);
        msg.put("price", price);
        return msg;
    }

    @Override
    public byte[] offerCredentialMsgPacked(Context context) throws VerityException {
        return packMsg(context, offerCredentialMsg(context));
    }

    @Override
    public void requestCredential(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JSONObject requestCredentialMsg(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] requestCredentialMsgPacked(Context context) {
        throw new UnsupportedOperationException();
    }

    /**
     * Sends the issue credential message to the connection
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    public void issueCredential(Context context) throws IOException, VerityException {
        send(context, issueCredentialMsg(context));
    }

    @Override
    public JSONObject issueCredentialMsg(Context context) {
        JSONObject msg = new JSONObject();
        msg.put("@type", getMessageType(ISSUE_CREDENTIAL));
        msg.put("@id", getNewId());
        addThread(msg);
        msg.put("~for_relationship", forRelationship);

        return msg;
    }

    @Override
    public byte[] issueCredentialMsgPacked(Context context) throws VerityException {
        return packMsg(context, issueCredentialMsg(context));
    }

    /**
     * Sends the get status message to the connection
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    public void status(Context context) throws IOException, VerityException {
        send(context, statusMsg(context));
    }

    @Override
    public JSONObject statusMsg(Context context) {
        JSONObject msg = new JSONObject();
        msg.put("@type", getMessageType(GET_STATUS));
        msg.put("@id", getNewId());
        addThread(msg);
        msg.put("~for_relationship", forRelationship);

        return msg;
    }

    @Override
    public byte[] statusMsgPacked(Context context) throws VerityException {
        return packMsg(context, statusMsg(context));
    }
}
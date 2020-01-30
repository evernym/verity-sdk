package com.evernym.verity.sdk.protocols.issuecredential.v_1_0;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.issuecredential.IssueCredential;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Builds and sends a message asking Verity to issue a credential to a connection
 */
public class IssueCredentialImpl extends Protocol implements IssueCredential {

    public String qualifier() {return Util.COMMUNITY_MSG_QUALIFIER;}
    public String family() { return "issue-credential";}
    public String version() {return "1.0";}

    // flag if this instance started the interaction
    boolean created = false;

    String PROPOSE_CREDENTIAL = "send-proposal";

    String forRelationship;
    String comment;
    JSONObject credentialProposal;
    String schemaIssuerId;
    String schemaId;
    String schemaName;
    String schemaVersion;
    String credDefId;
    String issuerDID;


    /**
     *
     * @param forRelationship pairwise relationship identifier
     */
    public IssueCredentialImpl(String forRelationship, JSONObject credentialProposal, String comment, String schemaIssuerId,
                               String schemaId, String schemaName, String schemaVersion, String credDefId, String issuerDID) {
        super();

        checkRequiredField(forRelationship, "forRelationship");

        ArrayList optionalFields = new ArrayList();
        optionalFields.add(credentialProposal);
        optionalFields.add(comment);
        optionalFields.add(schemaIssuerId);
        optionalFields.add(schemaId);
        optionalFields.add(schemaName);
        optionalFields.add(schemaVersion);
        optionalFields.add(credDefId);
        optionalFields.add(issuerDID);
        checkOneOptionalFieldExists(optionalFields);


        this.forRelationship = forRelationship;
        this.comment = comment;
        this.credentialProposal = credentialProposal;
        this.schemaIssuerId = schemaIssuerId;
        this.schemaId = schemaId;
        this.schemaName = schemaName;
        this.schemaVersion = schemaVersion;
        this.credDefId = credDefId;
        this.issuerDID = issuerDID;
        this.created = true;
    }

    public IssueCredentialImpl(String forRelationship, String threadId) {
        super(threadId);
        this.forRelationship = forRelationship;
    }

    /**
     * Sends the proposal message to the connection
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     */
    @Override
    public void proposeCredential(Context context) throws IOException, VerityException {
        send(context, proposeCredentialMsg(context));
    }

    @Override
    public JSONObject proposeCredentialMsg(Context context) {
        if(!created) {
            throw new IllegalArgumentException("Unable to propose credentials when NOT starting the interaction");
        }

        JSONObject msg = new JSONObject();
        msg.put("@type", getMessageType(PROPOSE_CREDENTIAL));
        msg.put("@id", getNewId());
        addThread(msg);

        msg.put("~for_relationship", forRelationship);
        msg.put("credential_proposal", credentialProposal);
        msg.put("schema_issuer_did", schemaIssuerId);
        msg.put("schema_id", schemaId);
        msg.put("schema_name", schemaName);
        msg.put("schema_version", schemaVersion);
        msg.put("cred_def_id", credDefId);
        msg.put("issuer_did", issuerDID);

        return msg;
    }

    private void checkOneOptionalFieldExists(ArrayList allOptionalInputs) {
        boolean result = allOptionalInputs.stream().allMatch(Objects::isNull);
        if (result) {
            throw new IllegalArgumentException("one of the optional input parameters must be supplied");
        }
    }

    private <E> void checkRequiredField(E field, String fieldName) {
        if (field == null) {
            throw new IllegalArgumentException(String.format("'%s' field is required", fieldName));
        }
    }

    @Override
    public byte[] proposeCredentialMsgPacked(Context context) throws VerityException {
        return packMsg(context, proposeCredentialMsg(context));
    }

    /**
     * Sends the credential offer message to the connection
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     */
    @Override
    public void offerCredential(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JSONObject offerCredentialMsg(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] offerCredentialMsgPacked(Context context) {
        throw new UnsupportedOperationException();
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
     */
    @Override
    public void issueCredential(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JSONObject issueCredentialMsg(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] issueCredentialMsgPacked(Context context) {
        throw new UnsupportedOperationException();
    }

    /**
     * Sends the get status message to the connection
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     */
    @Override
    public void status(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JSONObject statusMsg(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] statusMsgPacked(Context context) {
        throw new UnsupportedOperationException();
    }
}
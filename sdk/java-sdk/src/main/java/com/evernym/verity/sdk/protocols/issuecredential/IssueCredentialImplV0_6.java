package com.evernym.verity.sdk.protocols.issuecredential;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.issuecredential.v0_6.IssueCredentialV0_6;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * Builds and sends a message asking Verity to issue a credential to a connection
 */
class IssueCredentialImplV0_6 extends Protocol implements IssueCredentialV0_6 {
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
//
//    // FIXME: only needed 'Undefined interface' in agency?
//    public IssueCredentialImplV0_6() {
//    }


    IssueCredentialImplV0_6(String forRelationship, String name, Map<String, String> values, String credDefId) {
        super();
        this.forRelationship = forRelationship;
        this.name = name;
        this.credDefId = credDefId;
        this.values = values;
        this.price = "0";
        this.created = true;
    }

    IssueCredentialImplV0_6(String forRelationship, String threadId) {
        super(threadId);
        this.forRelationship = forRelationship;
    }

    @Override
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
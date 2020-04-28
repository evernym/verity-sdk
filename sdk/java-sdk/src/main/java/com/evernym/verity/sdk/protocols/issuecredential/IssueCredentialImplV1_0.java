package com.evernym.verity.sdk.protocols.issuecredential;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.issuecredential.v1_0.*;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.ValidationUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * Builds and sends a message asking Verity to issue a credential to a connection
 */
class IssueCredentialImplV1_0 extends Protocol implements IssueCredentialV1_0 {

    // flag if this instance started the interaction
    boolean created = false;

    String PROPOSE = "propose";
    String OFFER = "offer";
    String REQUEST = "request";
    String ISSUE = "issue";
    String REJECT = "reject";
    String STATUS = "status";

    String forRelationship;
    String credDefId;
    Map<String, String> values;
    String comment;
    String price;

    IssueCredentialImplV1_0(String forRelationship,
                            String credDefId,
                            Map<String, String> values,
                            String comment,
                            String price) {
        super();
        ValidationUtil.checkRequiredField(forRelationship, "forRelationship");
        ValidationUtil.checkRequiredField(credDefId, "credDefId");

        this.forRelationship = forRelationship;
        this.credDefId = credDefId;
        this.values = values;
        this.comment = comment;
        this.price = price;
        this.created = true;
    }

    IssueCredentialImplV1_0(String forRelationship, String threadId) {
        super(threadId);
        this.forRelationship = forRelationship;
    }


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
        msg.put("@type", getMessageType(PROPOSE));
        msg.put("@id", getNewId());
        msg.put("~for_relationship", forRelationship);
        addThread(msg);

        msg.put("cred_def_id", credDefId);
        msg.put("credential_values", values);
        msg.put("comment", comment);

        return msg;
    }


    @Override
    public byte[] proposeCredentialMsgPacked(Context context) throws VerityException {
        return packMsg(context, proposeCredentialMsg(context));
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
        msg.put("@type", getMessageType(OFFER));
        msg.put("@id", getNewId());
        msg.put("~for_relationship", forRelationship);
        addThread(msg);

        msg.put("cred_def_id", credDefId);
        msg.put("credential_values", values);
        msg.put("comment", comment);
        msg.put("price", price);

        return msg;

    }

    @Override
    public byte[] offerCredentialMsgPacked(Context context) throws VerityException {
        return packMsg(context, offerCredentialMsg(context));
    }

    @Override
    public void requestCredential(Context context) throws IOException, VerityException {
        send(context, requestCredentialMsg(context));
    }

    @Override
    public JSONObject requestCredentialMsg(Context context) {
        if(!created) {
            throw new IllegalArgumentException("Unable to request credential when NOT starting the interaction");
        }

        JSONObject msg = new JSONObject();
        msg.put("@type", getMessageType(REQUEST));
        msg.put("@id", getNewId());
        msg.put("~for_relationship", forRelationship);
        addThread(msg);

        msg.put("cred_def_id", credDefId);
        msg.put("comment", comment);

        return msg;
    }

    @Override
    public byte[] requestCredentialMsgPacked(Context context) throws VerityException {
        return packMsg(context, requestCredentialMsg(context));
    }

    @Override
    public void issueCredential(Context context) throws IOException, VerityException{
        send(context, issueCredentialMsg(context));
    }

    @Override
    public JSONObject issueCredentialMsg(Context context) {

        JSONObject msg = new JSONObject();
        msg.put("@type", getMessageType(ISSUE));
        msg.put("@id", getNewId());
        msg.put("~for_relationship", forRelationship);
        addThread(msg);

        msg.put("comment", comment);

        return msg;
    }

    @Override
    public byte[] issueCredentialMsgPacked(Context context) throws VerityException{
        return packMsg(context, issueCredentialMsg(context));
    }

    @Override
    public void reject(Context context) throws IOException, VerityException {
        send(context, rejectMsg(context));
    }

    @Override
    public JSONObject rejectMsg(Context context) {
        if(!created) {
            throw new IllegalArgumentException("Unable to reject when NOT starting the interaction");
        }

        JSONObject msg = new JSONObject();
        msg.put("@type", getMessageType(REJECT));
        msg.put("@id", getNewId());
        msg.put("~for_relationship", forRelationship);
        addThread(msg);

        msg.put("comment", comment);

        return msg;
    }

    @Override
    public byte[] rejectMsgPacked(Context context) throws VerityException {
        return packMsg(context, rejectMsg(context));
    }

    @Override
    public void status(Context context) throws IOException, VerityException {
        send(context, statusMsg(context));
    }

    @Override
    public JSONObject statusMsg(Context context) {

        JSONObject msg = new JSONObject();
        msg.put("@type", getMessageType(STATUS));
        msg.put("@id", getNewId());
        msg.put("~for_relationship", forRelationship);
        addThread(msg);
        return msg;
    }

    @Override
    public byte[] statusMsgPacked(Context context) throws VerityException {
        return packMsg(context, statusMsg(context));
    }
}
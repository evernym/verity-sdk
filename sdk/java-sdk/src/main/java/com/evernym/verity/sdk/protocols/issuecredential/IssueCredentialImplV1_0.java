package com.evernym.verity.sdk.protocols.issuecredential;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.AbstractProtocol;
import com.evernym.verity.sdk.protocols.issuecredential.v1_0.IssueCredentialV1_0;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.DbcUtil;
import org.apache.commons.lang3.NotImplementedException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;


@SuppressWarnings("CPD-START")

/*
 * NON_VISIBLE
 *
 * This is an implementation of IssueCredentialV1_0 but is not viable to user of Verity SDK. Created using the
 * static IssueCredential class
 */
class IssueCredentialImplV1_0 extends AbstractProtocol implements IssueCredentialV1_0 {

    // flag if this instance started the interaction
    boolean created = false;

    final String PROPOSE = "propose";
    final String OFFER = "offer";
    final String REQUEST = "request";
    final String ISSUE = "issue";
    final String REJECT = "reject";
    final String STATUS = "status";

    final String forRelationship;
    String credDefId;
    Map<String, String> values;
    String comment;
    String price;
    Boolean autoIssue;

    IssueCredentialImplV1_0(String forRelationship,
                            String credDefId,
                            Map<String, String> values,
                            String comment,
                            String price,
                            Boolean autoIssue) {
        super();
        DbcUtil.requireNotNull(forRelationship, "forRelationship");
        DbcUtil.requireNotNull(credDefId, "credDefId");

        this.forRelationship = forRelationship;
        this.credDefId = credDefId;
        this.values = values;
        this.comment = comment;
        this.price = price;
        this.autoIssue = autoIssue;
        this.created = true;
    }

    IssueCredentialImplV1_0(String forRelationship, String threadId) {
        super(threadId);
        this.forRelationship = forRelationship;
    }


    @Override
    public void proposeCredential(Context context) throws IOException, VerityException {
//        send(context, proposeCredentialMsg(context));
        throw new NotImplementedException("This API has not been implemented");
    }

    @Override
    public JSONObject proposeCredentialMsg(Context context) {
//        if(!created) {
//            throw new IllegalArgumentException("Unable to propose credentials when NOT starting the interaction");
//        }
//
//        JSONObject msg = new JSONObject();
//        msg.put("@type", messageType(PROPOSE));
//        msg.put("@id", getNewId());
//        msg.put("~for_relationship", forRelationship);
//        addThread(msg);
//
//        msg.put("cred_def_id", credDefId);
//        msg.put("credential_values", values);
//        msg.put("comment", comment);
//
//        return msg;
        throw new NotImplementedException("This API has not been implemented");
    }


    @Override
    public byte[] proposeCredentialMsgPacked(Context context) throws VerityException {
//        return packMsg(context, proposeCredentialMsg(context));
        throw new NotImplementedException("This API has not been implemented");
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
        msg.put("@type", messageType(OFFER));
        msg.put("@id", getNewId());
        msg.put("~for_relationship", forRelationship);
        addThread(msg);

        msg.put("cred_def_id", credDefId);
        msg.put("credential_values", values);
        msg.put("comment", comment);
        msg.put("price", price);
        msg.put("auto_issue", autoIssue);

        return msg;

    }

    @Override
    public byte[] offerCredentialMsgPacked(Context context) throws VerityException {
        return packMsg(context, offerCredentialMsg(context));
    }

    @Override
    public void requestCredential(Context context) throws IOException, VerityException {
//        send(context, requestCredentialMsg(context));
        throw new NotImplementedException("This API has not been implemented");
    }

    @Override
    public JSONObject requestCredentialMsg(Context context) {
//        if(!created) {
//            throw new IllegalArgumentException("Unable to request credential when NOT starting the interaction");
//        }
//
//        JSONObject msg = new JSONObject();
//        msg.put("@type", messageType(REQUEST));
//        msg.put("@id", getNewId());
//        msg.put("~for_relationship", forRelationship);
//        addThread(msg);
//
//        msg.put("cred_def_id", credDefId);
//        msg.put("comment", comment);
//
//        return msg;
        throw new NotImplementedException("This API has not been implemented");
    }

    @Override
    public byte[] requestCredentialMsgPacked(Context context) throws VerityException {
//        return packMsg(context, requestCredentialMsg(context));
        throw new NotImplementedException("This API has not been implemented");
    }

    @Override
    public void issueCredential(Context context) throws IOException, VerityException{
        send(context, issueCredentialMsg(context));
    }

    @Override
    public JSONObject issueCredentialMsg(Context context) {

        JSONObject msg = new JSONObject();
        msg.put("@type", messageType(ISSUE));
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
        if(!created) {
            throw new IllegalArgumentException("Unable to reject when NOT starting the interaction");
        }

        send(context, rejectMsg(context));
    }

    @Override
    public JSONObject rejectMsg(Context context) {
        JSONObject msg = new JSONObject();
        msg.put("@type", messageType(REJECT));
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
        msg.put("@type", messageType(STATUS));
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

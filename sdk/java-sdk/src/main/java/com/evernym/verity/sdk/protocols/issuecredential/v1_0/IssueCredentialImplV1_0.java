package com.evernym.verity.sdk.protocols.issuecredential.v1_0;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.ValidationUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Builds and sends a message asking Verity to issue a credential to a connection
 */
public class IssueCredentialImplV1_0 extends Protocol implements IssueCredentialV1_0 {
    // flag if this instance started the interaction
    boolean created = false;

    String SEND_PROPOSAL = "send-proposal";
    String SEND_OFFER = "send-offer";
    String SEND_REQ_CRED = "send-req-cred";

    String forRelationship;
    String name;
    String comment;
    List<CredPreviewAttribute> attributes;
    String schemaIssuerId;
    String schemaId;
    String schemaName;
    String schemaVersion;
    String credDefId;
    String issuerDID;
    String price;


    /**
     *
     * @param forRelationship pairwise relationship identifier
     */
    public IssueCredentialImplV1_0(String forRelationship,
                                   String name,
                                   String credDefId,
                                   List<CredPreviewAttribute> attributes,
                                   String comment,
                                   String schemaIssuerId,
                                   String schemaId,
                                   String schemaName,
                                   String schemaVersion,
                                   String issuerDID,
                                   String price) {
        super();
        ValidationUtil.checkRequiredField(forRelationship, "forRelationship");
        ValidationUtil.checkRequiredField(name, "name");
        ValidationUtil.checkRequiredField(credDefId, "credDefId");

        this.forRelationship = forRelationship;
        this.name = name;
        this.credDefId = credDefId;

        this.attributes = attributes;
        this.comment = comment;
        this.schemaIssuerId = schemaIssuerId;
        this.schemaId = schemaId;
        this.schemaName = schemaName;
        this.schemaVersion = schemaVersion;
        this.issuerDID = issuerDID;
        this.price = price;
        this.created = true;
    }

    public IssueCredentialImplV1_0(String forRelationship, String threadId) {
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

        JSONObject js = ProposeCredBuilder
                .blank()
                .forRelationship(forRelationship)
                .type(getMessageType(SEND_PROPOSAL))
                .id(getNewId())
                .comment(comment)
                .schemaIssuerDid(schemaIssuerId)
                .schemaId(schemaId)
                .schemaName(schemaName)
                .schemaVersion(schemaVersion)
                .credDefId(credDefId)
                .issuerDid(issuerDID)
                .proposal(attributes, this)
                .build()
                .toJson();
        addThread(js);
        return js;
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
    public void offerCredential(Context context) throws IOException, VerityException {
        send(context, offerCredentialMsg(context));
    }

    @Override
    public JSONObject offerCredentialMsg(Context context) {
        if(!created) {
            throw new IllegalArgumentException("Unable to offer credentials when NOT starting the interaction");
        }

        JSONObject js = OfferCredBuilder
                .blank()
                .forRelationship(forRelationship)
                .type(getMessageType(SEND_OFFER))
                .id(getNewId())
                .name(name)
                .credDefId(credDefId)
                .comment(comment)
                .price(price)
                .credentialPreview(attributes, this)
                .build()
                .toJson();

        addThread(js);

        return js;

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

        JSONObject js = CredReqBuilder
                .blank()
                .forRelationship(forRelationship)
                .type(getMessageType(SEND_REQ_CRED))
                .id(getNewId())
                .credDefId(credDefId)
                .comment(comment)
                .build()
                .toJson();

        addThread(js);

        return js;
    }

    @Override
    public byte[] requestCredentialMsgPacked(Context context) throws VerityException {
        return packMsg(context, requestCredentialMsg(context));
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

    @Override
    public void reject(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JSONObject rejectMsg(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] rejectMsgPacked(Context context) {
        throw new UnsupportedOperationException();
    }

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
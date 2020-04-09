package com.evernym.verity.sdk.protocols.issuecredential.v_1_0;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.issuecredential.IssueCredential;
import com.evernym.verity.sdk.protocols.issuecredential.v_1_0.cred_preview.CredPreviewAttribute;
import com.evernym.verity.sdk.protocols.issuecredential.v_1_0.offer.OfferCredBuilder;
import com.evernym.verity.sdk.protocols.issuecredential.v_1_0.proposal.ProposeCredBuilder;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import com.evernym.verity.sdk.utils.ValidationUtil;

import java.io.IOException;
import java.util.*;

/**
 * Builds and sends a message asking Verity to issue a credential to a connection
 */
public class IssueCredential_1_0 extends Protocol implements IssueCredential {

    public String qualifier() {return Util.COMMUNITY_MSG_QUALIFIER;}
    public String family() { return "issue-credential";}
    public String version() {return "1.0";}

    // flag if this instance started the interaction
    boolean created = false;

    String SEND_PROPOSAL = "send-proposal";
    String SEND_OFFER = "send-offer";

    String forRelationship;
    String comment;
    List<CredPreviewAttribute> attributes;
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
    public IssueCredential_1_0(String forRelationship,
                               List<CredPreviewAttribute> attributes,
                               String comment,
                               String schemaIssuerId,
                               String schemaId,
                               String schemaName,
                               String schemaVersion,
                               String credDefId,
                               String issuerDID) {
        super();
        ValidationUtil.checkRequiredField(forRelationship, "forRelationship");
        ValidationUtil.checkAtLeastOneOptionalFieldExists(Arrays.asList(
                attributes, comment, schemaIssuerId, schemaId, schemaName, schemaVersion, credDefId, issuerDID)
        );

        this.forRelationship = forRelationship;
        this.attributes = attributes;
        this.comment = comment;
        this.schemaIssuerId = schemaIssuerId;
        this.schemaId = schemaId;
        this.schemaName = schemaName;
        this.schemaVersion = schemaVersion;
        this.credDefId = credDefId;
        this.issuerDID = issuerDID;
        this.created = true;
    }

    public IssueCredential_1_0(String forRelationship, String threadId) {
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
                .comment(comment)
                .credential
        Previous(attributes, this)
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
package com.evernym.verity.sdk.protocols.presentproof;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.AbstractProtocol;
import com.evernym.verity.sdk.protocols.presentproof.common.Attribute;
import com.evernym.verity.sdk.protocols.presentproof.common.Predicate;
import com.evernym.verity.sdk.protocols.presentproof.common.ProposedAttribute;
import com.evernym.verity.sdk.protocols.presentproof.common.ProposedPredicate;
import com.evernym.verity.sdk.protocols.presentproof.v1_0.PresentProofV1_0;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;

import static com.evernym.verity.sdk.utils.JsonUtil.makeArray;
import static com.evernym.vdrtools.StringUtils.isNullOrWhiteSpace;

/*
 * NON_VISIBLE
 *
 * This is an implementation of PresentProofImplV1_0 but is not viable to user of Verity SDK. Created using the
 * static PresentProof class
 */
@SuppressWarnings("CPD-START")
class PresentProofImplV1_0 extends AbstractProtocol implements PresentProofV1_0 {

    final String PROOF_REQUEST = "request";
    final String STATUS = "status";
    final String REJECT = "reject";
    final String ACCEPT_PROPOSAL = "accept-proposal";
    final String PROTOCOL_INVITATION = "protocol-invitation";

    // flag if this instance started the interaction
    boolean created = false;

    final String forRelationship;
    String name;
    Attribute[] proofAttrs;
    Predicate[] proofPredicates;
    ProposedAttribute[] proposedAttributes;
    ProposedPredicate[] proposedPredicates;
    Boolean byInvitation;


    PresentProofImplV1_0(String forRelationship, String threadId) {
        super(threadId);
        this.forRelationship = forRelationship;
    }


    PresentProofImplV1_0(String forRelationship, String name, Attribute[] proofAttrs) {
        this(forRelationship, name, proofAttrs, null);
    }

    PresentProofImplV1_0(String forRelationship, String name, Predicate[] proofPredicates) {
        this(forRelationship, name, null, proofPredicates);
    }


    PresentProofImplV1_0(String forRelationship, String name, Attribute[] proofAttrs, Predicate[] proofPredicates) {
        super();
        this.forRelationship = forRelationship;
        this.name = name;
        this.proofAttrs = proofAttrs;
        this.proofPredicates = proofPredicates;
        this.byInvitation = false;
        this.created = true;
    }

    PresentProofImplV1_0(String forRelationship, String name, Attribute[] proofAttrs, Predicate[] proofPredicates, Boolean byInvitation) {
        super();
        this.forRelationship = forRelationship;
        this.name = name;
        this.proofAttrs = proofAttrs;
        this.proofPredicates = proofPredicates;
        this.byInvitation = byInvitation;
        this.created = true;
    }

    PresentProofImplV1_0(String forRelationship, String threadId, ProposedAttribute[] attrs, ProposedPredicate[] predicates) {
        super(threadId);
        this.forRelationship = forRelationship;
        this.proposedAttributes = attrs;
        this.proposedPredicates = predicates;
        this.created = true;
    }

    public void request(Context context) throws IOException, VerityException {
        send(context, requestMsg(context));
    }

    @Override
    public JSONObject requestMsg(Context context) {
        if(!created) {
            throw new IllegalArgumentException("Unable to request presentation when NOT starting the interaction");
        }

        JSONObject msg = new JSONObject();
        msg.put("@type", messageType(PROOF_REQUEST));
        msg.put("@id", getNewId());
        addThread(msg);
        msg.put("~for_relationship", this.forRelationship);
        msg.put("name", this.name);
        msg.put("proof_attrs", makeArray(proofAttrs));
        if (this.proofPredicates != null)
            msg.put("proof_predicates", makeArray(proofPredicates));
        msg.put("by_invitation", this.byInvitation);
        
        return msg;
    }

    @Override
    public byte[] requestMsgPacked(Context context) throws VerityException {
        return packMsg(context, requestMsg(context));
    }

    @Override
    public void propose(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JSONObject proposeMsg(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] proposeMsgPacked(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void acceptRequest(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JSONObject acceptRequestMsg(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] acceptRequestMsgPacked(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void acceptProposal(Context context) throws IOException, VerityException {
        send(context, acceptProposalMsg(context));
    }

    @Override
    public JSONObject acceptProposalMsg(Context context) {
        JSONObject msg = new JSONObject();
        msg.put("@type", messageType(ACCEPT_PROPOSAL));
        msg.put("@id", getNewId());
        addThread(msg);
        msg.put("~for_relationship", this.forRelationship);
        return msg;
    }

    @Override
    public byte[] acceptProposalMsgPacked(Context context) throws VerityException {
        return packMsg(context, acceptProposalMsg(context));
    }

    @Override
    public void reject(Context context, String reason) throws IOException, VerityException {
        send(context, rejectMsg(context, reason));
    }

    @Override
    public JSONObject rejectMsg(Context context, String reason) {
        JSONObject msg = new JSONObject();
        msg.put("@type", messageType(REJECT));
        msg.put("@id", getNewId());
        addThread(msg);
        msg.put("~for_relationship", this.forRelationship);
        if(!isNullOrWhiteSpace(reason)) msg.put("reason", reason);
        return msg;
    }

    @Override
    public byte[] rejectMsgPacked(Context context, String reason) throws VerityException {
        return packMsg(context, rejectMsg(context, reason));
    }


    public void status(Context context) throws IOException, VerityException {
        send(context, statusMsg(context));
    }

    @Override
    public JSONObject statusMsg(Context context) {
        JSONObject msg = new JSONObject();
        msg.put("@type", messageType(STATUS));
        msg.put("@id", getNewId());
        addThread(msg);
        msg.put("~for_relationship", this.forRelationship);
        return msg;
    }

    @Override
    public byte[] statusMsgPacked(Context context) throws VerityException {
        return packMsg(context, statusMsg(context));
    }
}

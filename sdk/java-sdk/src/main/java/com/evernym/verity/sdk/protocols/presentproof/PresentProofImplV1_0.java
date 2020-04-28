package com.evernym.verity.sdk.protocols.presentproof;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.presentproof.common.Attribute;
import com.evernym.verity.sdk.protocols.presentproof.common.Predicate;
import com.evernym.verity.sdk.protocols.presentproof.v1_0.PresentProofV1_0;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;

import static com.evernym.verity.sdk.utils.JsonUtil.makeArray;
import static org.hyperledger.indy.sdk.StringUtils.isNullOrWhiteSpace;

/**
 * Builds and sends a message to Verity asking it to send a Proof Request to a connection
 */
@SuppressWarnings("CPD-START")
class PresentProofImplV1_0 extends Protocol implements PresentProofV1_0 {

    final String PROOF_REQUEST = "request";
    final String STATUS = "status";
    final String REJECT = "reject";

    // flag if this instance started the interaction
    boolean created = false;

    final String forRelationship;
    String name;
    Attribute[] proofAttrs;
    Predicate[] proofPredicates;


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

    /**
     * Initializes the proof request object
     * @param forRelationship DID of relationship where proof request will be sent to
     * @param name The name of the proof request
     * @param proofAttrs The requested attributes of the proof request
     * @param proofPredicates The requested predicates of the proof request
     */
    PresentProofImplV1_0(String forRelationship, String name, Attribute[] proofAttrs, Predicate[] proofPredicates) {
        super();
        this.forRelationship = forRelationship;
        this.name = name;
        this.proofAttrs = proofAttrs;
        this.proofPredicates = proofPredicates;
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
        msg.put("@type", getMessageType(PROOF_REQUEST));
        msg.put("@id", getNewId());
        addThread(msg);
        msg.put("~for_relationship", this.forRelationship);
        msg.put("name", this.name);
        msg.put("proof_attrs", makeArray(proofAttrs));
        if (this.proofPredicates != null)
            msg.put("proof_predicates", makeArray(proofPredicates));
        return msg;
    }

    @Override
    public byte[] requestMsgPacked(Context context) throws VerityException {
        return packMsg(context, requestMsg(context));
    }

    @Override
    public void accept(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JSONObject acceptMsg(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] acceptMsgPacked(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reject(Context context, String reason) throws IOException, VerityException {
        send(context, rejectMsg(context, reason));
    }

    @Override
    public JSONObject rejectMsg(Context context, String reason) {
        JSONObject msg = new JSONObject();
        msg.put("@type", getMessageType(REJECT));
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
        msg.put("@type", getMessageType(STATUS));
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
package com.evernym.verity.sdk.protocols.relationship;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.relationship.v1_0.RelationshipV1_0;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.ValidationUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hyperledger.indy.sdk.StringUtils.isNullOrWhiteSpace;

class RelationshipImplV1_0 extends Protocol implements RelationshipV1_0 {
    final static String CREATE = "create";

    String PREPARE_INVITE = "prepare-invite";

    String forRelationship;
    String did;
    String label;
    List<String> recipientKeys;
    List<String> routingKeys;

    RelationshipImplV1_0(String forRelationship, String threadId, String label, List<String> recipientKeys, List<String> routingKeys, String did) {
        super(threadId);
        if(recipientKeys != null && recipientKeys.isEmpty()) recipientKeys = null;
        ValidationUtil.checkOnlyOneOptionalFieldExists(Arrays.asList(did, recipientKeys));
        this.forRelationship = forRelationship;
        this.did = did;
        this.label = label;
        this.recipientKeys = recipientKeys;
        this.routingKeys = routingKeys;
    }

    @Override
    public JSONObject createMsg(Context context) {
        JSONObject rtn = new JSONObject()
            .put("@type", getMessageType(CREATE))
            .put("@id", getNewId());
        return rtn;
    }

    @Override
    public void create(Context context) throws IOException, VerityException {
        send(context, createMsg(context));
    }

    @Override
    public byte[] createMsgPacked(Context context) throws VerityException {
        return packMsg(context, createMsg(context));
    }

    @Override
    public JSONObject prepareInvitationMsg(Context context) {
        JSONObject rtn = new JSONObject()
                .put("@type", getMessageType(PREPARE_INVITE))
                .put("@id", getNewId())

                ;
        if(!isNullOrWhiteSpace(did)) rtn.put("did", did);
        if(!isNullOrWhiteSpace(label)) rtn.put("label", label);
        if(!isNullOrWhiteSpace(forRelationship)) rtn.put("~for_relationship", forRelationship);
        if(recipientKeys != null) rtn.put("recipient_keys", recipientKeys);
        if(routingKeys != null) rtn.put("routing_keys", routingKeys);
        return rtn;
    }

    @Override
    public void prepareInvitation(Context context) throws IOException, VerityException {
        send(context, prepareInvitationMsg(context));
    }

    @Override
    public byte[] prepareInvitationMsgPacked(Context context) throws VerityException {
        return packMsg(context, prepareInvitationMsg(context));
    }
}

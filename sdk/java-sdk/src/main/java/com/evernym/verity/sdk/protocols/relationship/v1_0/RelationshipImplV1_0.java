package com.evernym.verity.sdk.protocols.relationship.v1_0;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.connecting.v1_0.InvitationBuilder;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.ValidationUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RelationshipImplV1_0 extends Protocol implements RelationshipV1_0 {
    final static String CREATE_KEY = "create-key";

    String PREPARE_WITH_DID = "prepare-with-did";
    String PREPARE_WITH_KEY = "prepare-with-key";

    String forRelationship;
    String did;
    String label;
    List<String> recipientKeys;
    List<String> routingKeys;

    public RelationshipImplV1_0() {
    }

    /**
     * used by inviter to prepare a invitation message
     * @param forRelationship relationship identifier
     * @param threadId thread identifier
     * @param label required, suggested label for the connection
     * @param recipientKeys required, recipient keys
     * @param routingKeys optional, routing keys
     * @param did optional, invitation with public DID
     */
    public RelationshipImplV1_0(String forRelationship, String threadId, String label, List<String> recipientKeys, List<String> routingKeys, String did) {
        super(threadId);
        ValidationUtil.checkOnlyOneOptionalFieldExists(Arrays.asList(did, recipientKeys));
        this.forRelationship = forRelationship;
        this.did = did;
        this.label = label;
        this.recipientKeys = recipientKeys;
        this.routingKeys = routingKeys;
    }

    @Override
    public JSONObject createKeyMsg(Context context) {
        JSONObject js = InvitationBuilder
                .blank()
                .type(getMessageType(CREATE_KEY))
                .id(getNewId())
                .build()
                .toJson();
        return js;
    }

    @Override
    public void createKey(Context context) throws IOException, VerityException {
        send(context, createKeyMsg(context));
    }

    @Override
    public byte[] createKeyMsgPacked(Context context) throws VerityException {
        return packMsg(context, createKeyMsg(context));
    }

    @Override
    public JSONObject prepareInvitationMsg(Context context) {
        JSONObject js = InvitationBuilder
                .blank()
                .type(prepareMessageType())
                .id(getNewId())
                .did(did)
                .label(label)
                .forRelationship(forRelationship)
                .recipientKeys(recipientKeys)
                .routingKeys(routingKeys)
                .build()
                .toJson();
        return addThread(js);
    }

    @Override
    public void prepareInvitation(Context context) throws IOException, VerityException {
        send(context, prepareInvitationMsg(context));
    }

    @Override
    public byte[] prepareInvitationMsgPacked(Context context) throws VerityException {
        return packMsg(context, prepareInvitationMsg(context));
    }

    private String prepareMessageType() {
        if (did != null) {
            return getMessageType(PREPARE_WITH_DID);
        } else {
            return getMessageType(PREPARE_WITH_KEY);
        }
    }

}

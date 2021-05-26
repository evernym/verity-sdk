package com.evernym.verity.sdk.protocols.relationship;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.AbstractProtocol;
import com.evernym.verity.sdk.protocols.relationship.v1_0.GoalCode;
import com.evernym.verity.sdk.protocols.relationship.v1_0.RelationshipV1_0;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import static org.hyperledger.indy.sdk.StringUtils.isNullOrWhiteSpace;

/*
 * NON_VISIBLE
 *
 * This is an implementation of RelationshipImplV1_0 but is not viable to user of Verity SDK. Created using the
 * static Relationship class
 */
class RelationshipImplV1_0 extends AbstractProtocol implements RelationshipV1_0 {
    final static String CREATE = "create";
    final static String CONNECTION_INVITATION = "connection-invitation";
    final static String SMS_CONNECTION_INVITATION = "sms-connection-invitation";
    final static String OUT_OF_BAND_INVITATION = "out-of-band-invitation";
    final static String SMS_OUT_OF_BAND_INVITATION = "sms-out-of-band-invitation";

    String forRelationship;
    String label;
    URL logoUrl = null;

    // flag if this instance started the interaction
    boolean created = false;

    RelationshipImplV1_0() {
        this.created = true;
    }

    RelationshipImplV1_0(String label) {
        this.label = label;
        this.created = true;
    }

    RelationshipImplV1_0(String label, URL logoUrl) {
        this.label = label;
        this.logoUrl = logoUrl;

        this.created = true;
    }

    RelationshipImplV1_0(String forRelationship, String threadId) {
        super(threadId);
        this.forRelationship = forRelationship;
    }

    @Override
    public JSONObject createMsg(Context context) {
        if(!created) {
            throw new IllegalArgumentException("Unable to create relationship when NOT starting the interaction");
        }

        JSONObject rtn = new JSONObject()
                .put("@type", messageType(CREATE))
                .put("@id", getNewId());
        if (label != null)
            rtn.put("label", label.toString());
        if (logoUrl != null)
            rtn.put("logoUrl", logoUrl.toString());
        addThread(rtn);
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
    public void connectionInvitation(Context context) throws IOException, VerityException {
        send(context, connectionInvitationMsg(context));
    }

    @Override
    public void connectionInvitation(Context context, Boolean shortInvite) throws IOException, VerityException {
        send(context, connectionInvitationMsg(context, shortInvite));
    }

    @Override
    public JSONObject connectionInvitationMsg(Context context) throws VerityException {
        return connectionInvitationMsg(context, null);
    }

    @Override
    public JSONObject connectionInvitationMsg(Context context, Boolean shortInvite) {
        JSONObject rtn = new JSONObject()
                .put("@type", messageType(CONNECTION_INVITATION))
                .put("@id", getNewId())
                .put("shortInvite", shortInvite);

        if(!isNullOrWhiteSpace(forRelationship)) rtn.put("~for_relationship", forRelationship);

        addThread(rtn);
        return rtn;
    }

    @Override
    public byte[] connectionInvitationMsgPacked(Context context) throws VerityException {
        return packMsg(context, connectionInvitationMsg(context));
    }

    @Override
    public byte[] connectionInvitationMsgPacked(Context context, Boolean shortInvite) throws VerityException {
        return packMsg(context, connectionInvitationMsg(context, shortInvite));
    }

    @Override
    public void smsConnectionInvitation(Context context, String phoneNumber) throws IOException, VerityException {
        send(context, smsConnectionInvitationMsg(context, phoneNumber));
    }

    @Override
    public JSONObject smsConnectionInvitationMsg(Context context, String phoneNumber) {
        if (isNullOrWhiteSpace(phoneNumber))
            throw new IllegalArgumentException("phoneNumber must be defined");

        JSONObject rtn = new JSONObject()
                .put("@type", messageType(SMS_CONNECTION_INVITATION))
                .put("@id", getNewId())
                .put("phoneNumber", phoneNumber);

        if(!isNullOrWhiteSpace(forRelationship)) rtn.put("~for_relationship", forRelationship);

        addThread(rtn);
        return rtn;
    }

    @Override
    public byte[] smsConnectionInvitationMsgPacked(Context context, String phoneNumber) throws VerityException {
        return packMsg(context, smsConnectionInvitationMsg(context, phoneNumber));
    }

    @Override
    public void outOfBandInvitation(Context context) throws IOException, VerityException {
        send(context, outOfBandInvitationMsg(context));
    }

    @Override
    public void outOfBandInvitation(Context context, Boolean shortInvite) throws IOException, VerityException {
        send(context, outOfBandInvitationMsg(context, shortInvite));
    }

    @Override
    public void outOfBandInvitation(Context context, Boolean shortInvite, GoalCode goal) throws IOException, VerityException {
        send(context, outOfBandInvitationMsg(context, shortInvite, goal));
    }

    @Override
    public JSONObject outOfBandInvitationMsg(Context context) throws VerityException {
        return outOfBandInvitationMsg(context, null, null);
    }

    @Override
    public JSONObject outOfBandInvitationMsg(Context context, Boolean shortInvite) throws VerityException {
        return outOfBandInvitationMsg(context, shortInvite, null);
    }

    @Override
    public JSONObject outOfBandInvitationMsg(Context context, Boolean shortInvite, GoalCode goal) {
        if (isNullOrWhiteSpace(forRelationship))
            throw new IllegalArgumentException("forRelationship must be defined");

        JSONObject rtn = new JSONObject()
                .put("@type", messageType(OUT_OF_BAND_INVITATION))
                .put("@id", getNewId());

        if(goal != null) {
            rtn.put("goalCode", goal.code())
                    .put("goal", goal.goalName());
        }

        if(shortInvite != null) {
            rtn.put("shortInvite", shortInvite);
        }

        if(!isNullOrWhiteSpace(forRelationship)) rtn.put("~for_relationship", forRelationship);
        addThread(rtn);
        return rtn;
    }

    @Override
    public byte[] outOfBandInvitationMsgPacked(Context context) throws VerityException {
        return packMsg(context, outOfBandInvitationMsg(context));
    }


    @Override
    public byte[] outOfBandInvitationMsgPacked(Context context, Boolean shortInvite) throws VerityException {
        return packMsg(context, outOfBandInvitationMsg(context, shortInvite));
    }

    @Override
    public byte[] outOfBandInvitationMsgPacked(Context context, Boolean shortInvite, GoalCode goal) throws VerityException {
        return packMsg(context, outOfBandInvitationMsg(context, shortInvite, goal));
    }

    @Override
    public void smsOutOfBandInvitation(Context context, String phoneNumber) throws IOException, VerityException {
        send(context, smsOutOfBandInvitationMsg(context, phoneNumber));
    }

    @Override
    public void smsOutOfBandInvitation(Context context, String phoneNumber, GoalCode goal) throws IOException, VerityException {
        send(context, smsOutOfBandInvitationMsg(context, phoneNumber, goal));
    }

    @Override
    public JSONObject smsOutOfBandInvitationMsg(Context context, String phoneNumber) throws VerityException {
        return smsOutOfBandInvitationMsg(context, phoneNumber, null);
    }

    @Override
    public JSONObject smsOutOfBandInvitationMsg(Context context, String phoneNumber, GoalCode goal) {
        if (isNullOrWhiteSpace(phoneNumber))
            throw new IllegalArgumentException("phoneNumber must be defined");

        JSONObject rtn = new JSONObject()
                .put("@type", messageType(SMS_OUT_OF_BAND_INVITATION))
                .put("@id", getNewId())
                .put("phoneNumber", phoneNumber);

        if(goal != null) {
            rtn.put("goalCode", goal.code())
                    .put("goal", goal.goalName());
        }

        if(!isNullOrWhiteSpace(forRelationship)) rtn.put("~for_relationship", forRelationship);
        addThread(rtn);
        return rtn;
    }

    @Override
    public byte[] smsOutOfBandInvitationMsgPacked(Context context, String phoneNumber) throws VerityException {
        return packMsg(context, smsOutOfBandInvitationMsg(context, phoneNumber));
    }

    @Override
    public byte[] smsOutOfBandInvitationMsgPacked(Context context, String phoneNumber, GoalCode goal) throws VerityException {
        return packMsg(context, smsOutOfBandInvitationMsg(context, phoneNumber, goal));
    }
}

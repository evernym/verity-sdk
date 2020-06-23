package com.evernym.verity.sdk.protocols.outofband;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.outofband.v1_0.OutOfBandV1_0;
import com.evernym.verity.sdk.protocols.outofband.v1_0.RequestAttach;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import static org.hyperledger.indy.sdk.StringUtils.isNullOrWhiteSpace;

@SuppressWarnings("CPD-START")
class OutOfBandImplV1_0 extends Protocol implements OutOfBandV1_0 {
    final static String CREATE = "create";
    final static String CONNECTION_INVITATION = "connection-invitation";

    String forRelationship;
    String label;
    String goalCode;
    String goal;
    List<String> handshakeProtocols;
    List<RequestAttach> requestAttach;

    // flag if this instance started the interaction
    boolean created = false;

    OutOfBandImplV1_0(String label, String goalCode, String goal,
                      List<String> handshakeProtocols, List<RequestAttach> request) {
        if (!isNullOrWhiteSpace(label))
            this.label = label;
        else
            this.label = "";
        this.goalCode = goalCode;
        this.goal = goal;
        this.handshakeProtocols = handshakeProtocols;
        this.requestAttach = request;

        this.created = true;
    }

    OutOfBandImplV1_0(String threadId) {
        super(threadId);
    }

    @Override
    public JSONObject createMsg(Context context) {
        if(!created) {
            throw new IllegalArgumentException("Unable to create relationship when NOT starting the interaction");
        }

        JSONObject rtn = new JSONObject()
                .put("@type", getMessageType(CREATE))
                .put("@id", getNewId())
                .put("label", label)
                .put("goal_code", goalCode)
                .put("goal", goal)
                .put("handshake_protocols", handshakeProtocols)
                .put("request~attach", requestAttach);
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
    public JSONObject outOfBandInvitationMsg(Context context) {
        JSONObject rtn = new JSONObject()
                .put("@type", getMessageType(CONNECTION_INVITATION))
                .put("@id", getNewId());

        addThread(rtn);
        return rtn;
    }

    @Override
    public void outOfBandInvitation(Context context) throws IOException, VerityException {
        send(context, outOfBandInvitationMsg(context));
    }

    @Override
    public byte[] outOfBandInvitationMsgPacked(Context context) throws VerityException {
        return packMsg(context, outOfBandInvitationMsg(context));
    }
}
package com.evernym.verity.sdk.protocols.connecting;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.connecting.v1_0.ConnectionsV1_0;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;


class ConnectionsImplV1_0 extends Protocol implements ConnectionsV1_0 {

    String forRelationship;
    String label;
    String base64InviteURL;

    String SEND_ACCEPT_INVITE = "accept";
    String GET_STATUS = "status";

    ConnectionsImplV1_0() {

    }

    ConnectionsImplV1_0(String threadId) {
        super(threadId);
    }

    ConnectionsImplV1_0(String label, String base64InviteURL) {
        this.label = label;
        this.base64InviteURL = base64InviteURL;
    }

    @Override
    public void status(Context context) throws IOException, VerityException {
        send(context, statusMsg(context));
    }

    @Override
    public JSONObject statusMsg(Context context) {
        JSONObject msg = new JSONObject();
        msg.put("@type", getMessageType(GET_STATUS));
        msg.put("@id", getNewId());
        msg.put("~for_relationship", forRelationship);
        addThread(msg);

        return msg;
    }

    @Override
    public byte[] statusMsgPacked(Context context) throws VerityException {
        return packMsg(context, statusMsg(context));
    }

    @Override
    public JSONObject acceptMsg(Context context) {
        JSONObject msg = new JSONObject();
        msg.put("@type", getMessageType(SEND_ACCEPT_INVITE));
        msg.put("@id", getNewId());
        msg.put("~for_relationship", forRelationship);
        addThread(msg);

        msg.put("label", label);
        msg.put("invite_url", base64InviteURL);

        return msg;
    }

    @Override
    public void accept(Context context) throws VerityException, IOException {
        send(context, acceptMsg(context));
    }

    @Override
    public byte[] acceptMsgPacked(Context context) throws VerityException {
        return packMsg(context, acceptMsg(context));
    }
}
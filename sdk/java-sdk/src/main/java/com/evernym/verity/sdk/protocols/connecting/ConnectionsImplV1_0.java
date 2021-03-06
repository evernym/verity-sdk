package com.evernym.verity.sdk.protocols.connecting;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.AbstractProtocol;
import com.evernym.verity.sdk.protocols.connecting.v1_0.ConnectionsV1_0;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;

@SuppressWarnings("CPD-START")
/*
 * NON_VISIBLE
 *
 * This is an implementation of ConnectionsV1_0 but is not viable to user of Verity SDK. Created using the
 * static Connecting class
 */
class ConnectionsImplV1_0 extends AbstractProtocol implements ConnectionsV1_0 {

    final String forRelationship;
    final String label;
    final String base64InviteURL;

    final String ACCEPT_INVITE = "accept";
    final String STATUS = "status";

    ConnectionsImplV1_0(String forRelationship, String threadId) {
        super(threadId);
        this.forRelationship = forRelationship;
        this.label = null;
        this.base64InviteURL = null;
    }

    ConnectionsImplV1_0(String forRelationship, String label, String base64InviteURL) {
        this.forRelationship = forRelationship;
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

    @Override
    public JSONObject acceptMsg(Context context) {
        JSONObject msg = new JSONObject();
        msg.put("@type", messageType(ACCEPT_INVITE));
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
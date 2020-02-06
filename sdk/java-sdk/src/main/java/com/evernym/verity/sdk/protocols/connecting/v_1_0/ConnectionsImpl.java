package com.evernym.verity.sdk.protocols.connecting.v_1_0;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.connecting.Connecting;
import com.evernym.verity.sdk.protocols.connecting.v_1_0.invite_with_did.InviteWithDIDBuilder;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;


/**
 * Builds and sends a new encrypted agent message for the Connections protocol.
 */
public class ConnectionsImpl extends Protocol implements Connecting {

    public String qualifier() {return Util.COMMUNITY_MSG_QUALIFIER;}
    public String family() {return "connections";}
    public String version() {return "1.0";}

    String INVITATION = "invitation";

    String did;
    String label;

    /**
     * Create connection without phone number
     * @param did required optional param that is used during preparing invitation with public DID message
     */
    public ConnectionsImpl(String did, String label) {
        this.did = did;
        this.label = label;
    }


    @Override
    public JSONObject invitationMsg(Context context) {
        JSONObject js = InviteWithDIDBuilder
                .blank()
                .type(getMessageType(INVITATION))
                .id(getNewId())
                .did(did)
                .label(label)
                .build()
                .toJson();
        addThread(js);
        return js;
    }

    @Override
    public void invitation(Context context) throws IOException, VerityException {
        send(context, invitationMsg(context));
    }

    @Override
    public byte[] invitationMsgPacked(Context context) throws IOException, VerityException {
        return packMsg(context, invitationMsg(context));
    }


    //non supported

    @Override
    public void connect(Context context){
        throw new UnsupportedOperationException();
    }

    @Override
    public JSONObject connectMsg(Context context) { throw new UnsupportedOperationException(); }

    @Override
    public byte[] connectMsgPacked(Context context){
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
    public byte[] statusMsgPacked(Context context){
        throw new UnsupportedOperationException();
    }

    @Override
    public void accept(Context context) { throw new UnsupportedOperationException(); }

    @Override
    public JSONObject acceptMsg(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] acceptMsgPacked(Context context) {
        throw new UnsupportedOperationException();
    }

}
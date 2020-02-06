package com.evernym.verity.sdk.protocols.connecting.v_1_0;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.connecting.Connecting;
import com.evernym.verity.sdk.protocols.connecting.v_1_0.invitation.InvitationBuilder;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import com.evernym.verity.sdk.utils.ValidationUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


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
    String serviceEndpoint;
    ArrayList<String> recipientKeys;
    ArrayList<String> routingKeys;

    /**
     * this is used by inviter to prepare invite with DID
     * @param did invite sender's DID
     * @param label optional label which will help invitee identifying inviter
     */
    public ConnectionsImpl(String did, String label) {
        ValidationUtil.checkRequiredField(did, "did");
        this.did = did;
        this.label = label;
    }

    /**
     * this is used by inviter to prepare invite with keys
     * @param serviceEndpoint inviter's service endpoint
     * @param recipientKeys inviter's recipient keys
     * @param routingKeys inviter's routing keys
     * @param label optional label which will help invitee identifying inviter
     */
    public ConnectionsImpl(String serviceEndpoint, ArrayList<String> recipientKeys, ArrayList<String> routingKeys, String label) {
        this.serviceEndpoint = serviceEndpoint;
        this.recipientKeys = recipientKeys;
        this.routingKeys = routingKeys;
        this.label = label;
    }


    @Override
    public JSONObject invitationMsg(Context context) {
        JSONObject js = InvitationBuilder
                .blank()
                .type(getMessageType(INVITATION))
                .id(getNewId())
                .did(did)
                .label(label)
                .serviceEndpoint(serviceEndpoint)
                .recipientKeys(recipientKeys)
                .routingKeys(routingKeys)
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
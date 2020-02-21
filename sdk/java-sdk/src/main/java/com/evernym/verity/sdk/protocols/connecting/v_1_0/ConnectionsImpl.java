package com.evernym.verity.sdk.protocols.connecting.v_1_0;

import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.connecting.Connecting;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

/**
 * Builds and sends a new encrypted agent message for the Connections protocol.
 */
public class ConnectionsImpl extends Protocol implements Connecting {

    public String qualifier() {return Util.COMMUNITY_MSG_QUALIFIER;}
    public String family() {return "connections";}
    public String version() {return "1.0";}

    String parentThreadId;
    String base64InviteURL;
    String label;

    /**
     * this is used by invitee to respond to an invitation
     * @param parentThreadId id of invitation message
     * @param base64InviteURL received invitation's url
     */
    public ConnectionsImpl(String parentThreadId, String label, String base64InviteURL) {
        this.parentThreadId = parentThreadId;
        this.base64InviteURL = base64InviteURL;
        this.label = label;
    }

    /**
     * can be used by either inviter or invitee once it knows thread id
     * @param threadId
     */
    public ConnectionsImpl(String threadId) {
        super(threadId);
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
    public JSONObject acceptMsg(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void accept(Context context) { throw new UnsupportedOperationException(); }

    @Override
    public byte[] acceptMsgPacked(Context context) { throw new UnsupportedOperationException(); }
}
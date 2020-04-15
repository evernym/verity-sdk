package com.evernym.verity.sdk.protocols.connecting;

import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.connecting.v1_0.ConnectionsV1_0;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;


class ConnectionsImplV1_0 extends Protocol implements ConnectionsV1_0 {
    String parentThreadId;
    String base64InviteURL;
    String label;

    ConnectionsImplV1_0() {

    }


    ConnectionsImplV1_0(String parentThreadId, String label, String base64InviteURL) {
        this.parentThreadId = parentThreadId;
        this.base64InviteURL = base64InviteURL;
        this.label = label;
    }

    ConnectionsImplV1_0(String threadId) {
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
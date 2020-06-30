package com.evernym.verity.sdk.protocols.outofband;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.outofband.v1_0.OutOfBandV1_0;
import com.evernym.verity.sdk.protocols.relationship.v1_0.RequestAttach;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import static org.hyperledger.indy.sdk.StringUtils.isNullOrWhiteSpace;

@SuppressWarnings("CPD-START")
class OutOfBandImplV1_0 extends Protocol implements OutOfBandV1_0 {
    final static String CREATE = "create";
    final static String CONNECTION_INVITATION = "handshake-reuse";

    OutOfBandImplV1_0(String threadId) {
        super(threadId);
    }

    @Override
    public JSONObject handshakeReuseMsg(Context context) {
        JSONObject rtn = new JSONObject()
                .put("@type", getMessageType(CONNECTION_INVITATION))
                .put("@id", getNewId());

        addThread(rtn);
        return rtn;
    }

    @Override
    public void handshakeReuse(Context context) throws IOException, VerityException {
        send(context, handshakeReuseMsg(context));
    }

    @Override
    public byte[] handshakeReuseMsgPacked(Context context) throws VerityException {
        return packMsg(context, handshakeReuseMsg(context));
    }
}
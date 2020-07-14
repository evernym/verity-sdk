package com.evernym.verity.sdk.protocols.outofband;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.outofband.v1_0.OutOfBandV1_0;
import com.evernym.verity.sdk.protocols.relationship.v1_0.RequestAttach;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import static org.hyperledger.indy.sdk.StringUtils.isNullOrWhiteSpace;

@SuppressWarnings("CPD-START")
class OutOfBandImplV1_0 extends Protocol implements OutOfBandV1_0 {
    final static String CONNECTION_INVITATION = "reuse";

    private final String parentThreadId;

    OutOfBandImplV1_0(String threadId, String inviteUrl) {
        super(threadId);

        if(isNullOrWhiteSpace(inviteUrl)) {
            parentThreadId = null;
            return;
        }
        String[] encoded = inviteUrl.split("c_i=", 2);
        byte[] decoded = Base64.getDecoder().decode(encoded[encoded.length - 1]);
        JSONObject invite = new JSONObject(new String(decoded));

        parentThreadId = invite.has("@id") ? invite.get("@id").toString() : null;
    }

    @Override
    public JSONObject handshakeReuseMsg(Context context) {
        JSONObject rtn = new JSONObject()
                .put("@type", messageType(CONNECTION_INVITATION))
                .put("@id", getNewId());

        JSONObject threadBlock = new JSONObject();
        threadBlock.put("thid", getThreadId());
        threadBlock.put("pthid", parentThreadId);
        rtn.put("~thread", threadBlock);
        return rtn;
    }

    @Override
    public void handshakeReuse(Context context) throws IOException, VerityException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
//        send(context, handshakeReuseMsg(context));
    }

    @Override
    public byte[] handshakeReuseMsgPacked(Context context) throws VerityException {
        return packMsg(context, handshakeReuseMsg(context));
    }
}
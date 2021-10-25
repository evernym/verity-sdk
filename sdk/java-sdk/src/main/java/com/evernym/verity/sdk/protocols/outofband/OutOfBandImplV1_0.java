package com.evernym.verity.sdk.protocols.outofband;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.AbstractProtocol;
import com.evernym.verity.sdk.protocols.outofband.v1_0.OutOfBandV1_0;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;
import static com.evernym.vdrtools.StringUtils.isNullOrWhiteSpace;

@SuppressWarnings("CPD-START")
/*
 * NON_VISIBLE
 *
 * This is an implementation of OutOfBandImplV1_0 but is not viable to user of Verity SDK. Created using the
 * static PresentProof class
 */
class OutOfBandImplV1_0 extends AbstractProtocol implements OutOfBandV1_0 {
    final static String CONNECTION_INVITATION = "reuse";
    final static String RELATIONSHIP_REUSED = "relationship-reused";

    private final String inviteUrl;
    private final String forRelationship;

    OutOfBandImplV1_0(String forRelationship, String inviteUrl) {
        this.forRelationship = forRelationship;
        this.inviteUrl = isNullOrWhiteSpace(inviteUrl) ? null : inviteUrl;
    }

    @Override
    public JSONObject handshakeReuseMsg(Context context) {
        JSONObject rtn = new JSONObject()
                .put("@type", messageType(CONNECTION_INVITATION))
                .put("@id", getNewId())
                .put("inviteUrl", this.inviteUrl);

        addThread(rtn);

        if(!isNullOrWhiteSpace(this.forRelationship)) rtn.put("~for_relationship", this.forRelationship);

        return rtn;
    }

    @Override
    public void handshakeReuse(Context context) throws IOException, VerityException, UnsupportedOperationException {
        send(context, handshakeReuseMsg(context));
    }

    @Override
    public byte[] handshakeReuseMsgPacked(Context context) throws VerityException {
        return packMsg(context, handshakeReuseMsg(context));
    }
}
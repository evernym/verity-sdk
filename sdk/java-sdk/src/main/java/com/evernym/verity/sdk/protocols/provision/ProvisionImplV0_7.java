package com.evernym.verity.sdk.protocols.provision;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.provision.v0_7.ProvisionV0_7;
import com.evernym.verity.sdk.transports.HTTPTransport;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

class ProvisionImplV0_7 extends Protocol implements ProvisionV0_7 {

    protected JSONObject sendToVerity(Context context, byte[] packedMessage) throws WalletException, UndefinedContextException, IOException {
        HTTPTransport transport = new HTTPTransport();
        byte[] respBytes = transport.sendSyncMessage(context.verityUrl(), packedMessage);

        return Util.unpackMessage(context, respBytes);
    }

    public Context provision(Context context) throws IOException, UndefinedContextException, WalletException {
        JSONObject resp = sendToVerity(context, provisionMsgPacked(context));

        String domainDID = resp.getString("selfDID");
        String verityAgentVerKey = resp.getString("agentVerKey");

        return context.toContextBuilder()
                .domainDID(domainDID)
                .verityAgentVerKey(verityAgentVerKey)
                .build();
    }

    @Override
    public JSONObject provisionMsg(Context context) throws UndefinedContextException {
        return new JSONObject()
                .put("@id", Protocol.getNewId())
                .put("@type", getMessageType(CREATE_EDGE_AGENT))
                .put("requesterVk", context.sdkVerKey());
    }

    @Override
    public byte[] provisionMsgPacked(Context context) throws UndefinedContextException, WalletException {
        return Util.packMessageForVerity(
            context.walletHandle(),
            provisionMsg(context),
            context.verityPublicDID(),
            context.verityPublicVerKey(),
            context.sdkVerKey(),
            context.verityPublicVerKey()
        );
    }
}

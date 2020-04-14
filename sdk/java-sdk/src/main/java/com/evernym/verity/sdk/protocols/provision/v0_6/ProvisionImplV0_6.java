package com.evernym.verity.sdk.protocols.provision.v0_6;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.transports.HTTPTransport;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

public class ProvisionImplV0_6 extends Protocol implements ProvisionV0_6 {
    public ProvisionImplV0_6() {
        super();
    }

    /**
     * Sends the connection create message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws WalletException when there are issues with encryption and decryption
     * @throws UndefinedContextException when the context don't have enough information for this operation
     * @return new Context with provisioned details
     */
    public Context provisionSdk(Context context) throws IOException, UndefinedContextException, WalletException {
        HTTPTransport transport = new HTTPTransport();
        byte[] respBytes = transport.sendSyncMessage(context.verityUrl(), provisionSdkMsgPacked(context));

        JSONObject resp = Util.unpackMessage(context, respBytes);
        String verityPairwiseDID = resp.getString("withPairwiseDID");
        String verityPairwiseVerKey = resp.getString("withPairwiseDIDVerKey");

        return context.toContextBuilder()
                .domainDID(verityPairwiseDID)
                .verityAgentVerKey(verityPairwiseVerKey)
                .build();
    }

    @Override
    public JSONObject provisionSdkMsg(Context context) throws UndefinedContextException {
        JSONObject message = new JSONObject();
        message.put("@id", Protocol.getNewId());
        message.put("@type", getMessageType(CREATE_AGENT));
        message.put("fromDID", context.sdkVerKeyId());
        message.put("fromDIDVerKey", context.sdkVerKey());

        return message;
    }

    @Override
    public byte[] provisionSdkMsgPacked(Context context) throws UndefinedContextException, WalletException {
        return Util.packMessageForVerity(
            context.walletHandle(),
            provisionSdkMsg(context),
            context.verityPublicDID(),
            context.verityPublicVerKey(),
            context.sdkVerKey(),
            context.verityPublicVerKey()
        );
    }
}

package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.transports.HTTPTransport;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

public class Provision extends Protocol {
    final private static String MSG_QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
    final private static String MSG_FAMILY = "agent-provisioning";
    final private static String MSG_FAMILY_VERSION = "0.6";

    // Messages
    public static String CREATE_CONNECTION = "CREATE_AGENT";


    public Provision() {
        super();
    }

    @Override
    protected void defineMessages() {
        throw new UnsupportedOperationException("Context is required to build messages");
    }


    public JSONObject createAgentMsg(Context context) throws UndefinedContextException {
        JSONObject message = new JSONObject();
        message.put("@id", Provision.getNewId());
        message.put("@type", Util.getMessageType(MSG_QUALIFIER, MSG_FAMILY, MSG_FAMILY_VERSION, CREATE_CONNECTION));
        message.put("fromDID", context.sdkPairwiseDID());
        message.put("fromDIDVerKey", context.sdkPairwiseVerkey());

        return message;
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
        byte[] msg = Util.packMessageForVerity(
                context.walletHandle(),
                createAgentMsg(context),
                context.verityPublicDID(),
                context.verityPublicVerkey(),
                context.sdkPairwiseVerkey(),
                context.verityPublicVerkey()
        );
        HTTPTransport transport = new HTTPTransport();
        byte[] respBytes = transport.sendSyncMessage(context.verityUrl(), msg);

        JSONObject resp = Util.unpackMessage(context, respBytes);
        String verityPairwiseDID = resp.getString("withPairwiseDID");
        String verityPairwiseVerKey = resp.getString("withPairwiseDIDVerKey");

        return context.toContextBuilder()
                .verityPairwiseDID(verityPairwiseDID)
                .verityPairwiseVerkey(verityPairwiseVerKey)
                .build();
    }

    @Override
    public String toString() {
        return "";
    } // TODO figure what this should be
}

package com.evernym.verity.sdk.protocols.provision;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

public interface Provision extends MessageFamily {
    String MSG_QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
    String MSG_FAMILY = "agent-provisioning";
    String MSG_FAMILY_VERSION = "0.6";

    default String qualifier() {return MSG_QUALIFIER;}
    default String family() {return MSG_FAMILY;}
    default String version() {return MSG_FAMILY_VERSION;}

    // Messages
    String CREATE_AGENT = "CREATE_AGENT";

    static Provision v0_6() {
        return new ProvisionImpl();
    }

    /**
     * Sends the connection create message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws WalletException when there are issues with encryption and decryption
     * @throws UndefinedContextException when the context don't have enough information for this operation
     * @return new Context with provisioned details
     */
    Context provisionSdk(Context context) throws IOException, UndefinedContextException, WalletException;
    JSONObject provisionSdkMsg(Context context) throws UndefinedContextException;
    byte[]  provisionSdkMsgPacked(Context context) throws UndefinedContextException, WalletException;
}

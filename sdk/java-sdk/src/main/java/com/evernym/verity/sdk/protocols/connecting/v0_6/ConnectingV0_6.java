package com.evernym.verity.sdk.protocols.connecting.v0_6;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.protocols.connecting.v1_0.ConnectionsImplV1_0;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

public interface ConnectingV0_6 extends MessageFamily {
    String QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
    String FAMILY = "connecting";
    String VERSION = "0.6";

    default String qualifier() {return QUALIFIER;}
    default String family() { return FAMILY;}
    default String version() {return VERSION;}

    String CREATE_CONNECTION = "CREATE_CONNECTION";
    String GET_STATUS = "get-status";

    static ConnectingImplV0_6 v0_6(String sourceId) {
        return new ConnectingImplV0_6(sourceId);
    }

    static ConnectingImplV0_6 v0_6(String sourceId, boolean includePublicDID) {
        return new ConnectingImplV0_6(sourceId, includePublicDID);
    }

    static ConnectingImplV0_6 v0_6(String sourceId, String phoneNo) {
        return new ConnectingImplV0_6(sourceId, phoneNo);
    }

    static ConnectingImplV0_6 v0_6(String sourceId, String phoneNo, boolean includePublicDID) {
        return new ConnectingImplV0_6(sourceId, phoneNo, includePublicDID);
    }

    static ConnectionsImplV1_0 v1_0(String parentThreadId, String label, String base64InviteURL) {
        return new ConnectionsImplV1_0(parentThreadId, label, base64InviteURL);
    }

    /**
     * Sends the connection create message to Verity
     *
     * @param context an instance of the Context object configured to a Verity Application
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    void connect(Context context) throws IOException, VerityException;

    /**
     *
     * @return
     * @throws VerityException
     */
    JSONObject connectMsg(Context context) throws VerityException;

    /**
     *
     * @param context an instance of the Context object configured to a Verity Application
     * @return
     * @throws VerityException
     */
    byte[] connectMsgPacked(Context context) throws VerityException;

    /**
     * Sends the get status message to the connection
     * @param context an instance of the Context object configured to a Verity Application
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    void status(Context context) throws IOException, VerityException;

    /**
     *
     * @return
     * @throws VerityException
     */
    JSONObject statusMsg(Context context) throws VerityException;

    /**
     *
     * @param context an instance of the Context object configured to a Verity Application
     * @return
     * @throws VerityException
     */
    byte[] statusMsgPacked(Context context) throws VerityException;

    void accept(Context context) throws IOException, VerityException;
    JSONObject acceptMsg(Context context) throws IOException, VerityException;
    byte[] acceptMsgPacked(Context context) throws IOException, VerityException;

}

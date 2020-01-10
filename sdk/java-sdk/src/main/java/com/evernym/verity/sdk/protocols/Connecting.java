package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

public interface Connecting extends MessageFamily {
    String MSG_QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
    String MSG_FAMILY = "connecting";
    String MSG_FAMILY_VERSION = "0.6";

    String CREATE_CONNECTION = "CREATE_CONNECTION";
    String GET_STATUS = "get-status";

    // Status definitions
    @Deprecated
    Integer AWAITING_RESPONSE_STATUS = 0;
    @Deprecated
    Integer INVITE_ACCEPTED_STATUS = 1;

    default String qualifier() {return MSG_QUALIFIER;}
    default String family() {return MSG_FAMILY;}
    default String version() {return MSG_FAMILY_VERSION;}

    static Connecting newConnection(String sourceId) {
        return new ConnectingImpl(sourceId);
    }

    static Connecting newConnection(String sourceId, boolean includePublicDID) {
        return new ConnectingImpl(sourceId, includePublicDID);
    }

    static Connecting newConnection(String sourceId, String phoneNo) {
        return new ConnectingImpl(sourceId, phoneNo);
    }

    static Connecting newConnection(String sourceId, String phoneNo, boolean includePublicDID) {
        return new ConnectingImpl(sourceId, phoneNo, includePublicDID);
    }

    static Connecting interaction(String threadId) {
        return new ConnectingImpl(null); // FIXME
    }

    String sourceId();
    String phoneNumber();
    boolean includePublicDID();

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
    void acceptMsg(Context context) throws IOException, VerityException;
    void acceptMsgPacked(Context context) throws IOException, VerityException;
}
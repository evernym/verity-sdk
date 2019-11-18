package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Builds and sends a new encrypted agent message for the Connection protocol.
 */
public class Connecting extends Protocol {

    final private static String MSG_QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
    final private static String MSG_FAMILY = "connecting";
    final private static String MSG_FAMILY_VERSION = "0.6";

    // Messages
    @SuppressWarnings("WeakerAccess")
    public static String CREATE_CONNECTION = "CREATE_CONNECTION";
    public static String GET_STATUS = "get-status";
    
    // Status definitions
    public static Integer AWAITING_RESPONSE_STATUS = 0;
    public static Integer INVITE_ACCEPTED_STATUS = 1;

    String sourceId;
    String phoneNumber;
    boolean includePublicDID = false;
    
    /**
    * Create connection without phone number
    * @param sourceId required optional param that sets an id of the connection
    */
    @SuppressWarnings("WeakerAccess")
    public Connecting(String sourceId) {
        this(sourceId, null, false);
    }

    /**
    * Create connection without a phone number that uses a public DID.
    * @param sourceId required param that sets an id of the connection
    * @param includePublicDID optional param that indicates the connection invite should use the institution's public DID.
    */
    @SuppressWarnings("WeakerAccess")
    public Connecting(String sourceId, boolean includePublicDID) {
        this(sourceId, null, includePublicDID);
    }

    /**
    * Create connection with phone number
    * @param sourceId required param that sets an id of the connection
    * @param phoneNo optional param that sets the sms phone number for an identity holder 
    */
    @SuppressWarnings("WeakerAccess")
    public Connecting(String sourceId, String phoneNo) {
        this(sourceId, phoneNo, false);
    }

    /**
    * Create connection with phone number that uses a public DID
    * @param sourceId required param that sets an id of the connection
    * @param phoneNo optional param that sets the sms phone number for an identity holder 
    * @param includePublicDID optional param that indicates the connection invite should use the institution's public DID.
    */
    @SuppressWarnings("WeakerAccess")
    public Connecting(String sourceId, String phoneNo, boolean includePublicDID) {
        super();
        this.sourceId = sourceId;
        this.phoneNumber = phoneNo;
        this.includePublicDID = includePublicDID;
        defineMessages();
    }

    public static String getMessageType(String msgName) {
        return Util.getMessageType(MSG_QUALIFIER, MSG_FAMILY, MSG_FAMILY_VERSION, msgName);
    }

    public static String getProblemReportMessageType() {
        return Util.getProblemReportMessageType(MSG_QUALIFIER, MSG_FAMILY, MSG_FAMILY_VERSION);
    }

    public static String getStatusMessageType() {
        return Util.getStatusMessageType(MSG_QUALIFIER, MSG_FAMILY, MSG_FAMILY_VERSION);
    }

    @Override
    protected void defineMessages() {
        JSONObject createConnectionMessage = new JSONObject();
        createConnectionMessage.put("@type", Connecting.getMessageType(CREATE_CONNECTION));
        createConnectionMessage.put("@id", Connecting.getNewId());
        createConnectionMessage.put("sourceId", this.sourceId);
        createConnectionMessage.put("phoneNo", this.phoneNumber);
        createConnectionMessage.put("includePublicDID", this.includePublicDID);
        this.messages.put(Connecting.CREATE_CONNECTION, createConnectionMessage);

        JSONObject statusMessage = new JSONObject();
        statusMessage.put("@type", Connecting.getMessageType(Connecting.GET_STATUS));
        statusMessage.put("@id", Connecting.getNewId());
        this.messages.put(Connecting.GET_STATUS, statusMessage);
    }

    /**
     * Sends the connection create message to Verity
     * 
     * @param context an instance of Context configured with the results of the
     *                provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    @SuppressWarnings("WeakerAccess")
    public byte[] connect(Context context) throws IOException, UndefinedContextException, WalletException {
        return this.send(context, this.messages.getJSONObject(CREATE_CONNECTION));
    }

    /**
     * Sends the get status message to the connection
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    @SuppressWarnings("WeakerAccess")
    public byte[] status(Context context) throws IOException, UndefinedContextException, WalletException {
        return this.send(context, this.messages.getJSONObject(Connecting.GET_STATUS));
    }
}
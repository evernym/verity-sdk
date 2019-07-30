package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.utils.Context;

import com.evernym.verity.sdk.utils.Util;
import org.hyperledger.indy.sdk.IndyException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Builds and sends an encrypted agent message to Verity asking Verity to 
 * write a new Schema to the ledger on behalf of the 
 * SDK/enterprise.
 */

public class WriteSchema extends Protocol {

    private static String MSG_FAMILY = "write-schema";
    private static String MSG_FAMILY_VERSION = "0.1";

    // Messages
    @SuppressWarnings("WeakerAccess")
    public static String WRITE_SCHEMA = "write";

    // Status Definitions
    public static Integer WRITE_SUCCESSFUL_STATUS = 0;

    String name;
    String version;
    String[] attrs;

    /**
     * Creates a Schema from a list of attributes
     * @param attrs
     */
    @SuppressWarnings("WeakerAccess")
    public WriteSchema(String name, String version, String ...attrs) {
        super();
        this.name = name;
        this.version = version;
        this.attrs = attrs;

        defineMessages();
    }

    public static String getMessageType(String msgName) {
        return Util.getMessageType(WriteSchema.MSG_FAMILY, WriteSchema.MSG_FAMILY_VERSION, msgName);
    }

    public static String getProblemReportMessageType() {
        return Util.getProblemReportMessageType(WriteSchema.MSG_FAMILY, WriteSchema.MSG_FAMILY_VERSION);
    }

    public static String getStatusMessageType() {
        return Util.getStatusMessageType(WriteSchema.MSG_FAMILY, WriteSchema.MSG_FAMILY_VERSION);
    }

    @Override
    protected void defineMessages() {
        JSONObject message = new JSONObject();
        message.put("@type", WriteSchema.getMessageType(WriteSchema.WRITE_SCHEMA));
        message.put("@id", WriteSchema.getNewId());
        message.put("name", this.name);
        message.put("version", this.version);
        message.put("attrNames", new JSONArray(attrs));
        this.messages.put(WriteSchema.WRITE_SCHEMA, message);
    }


    /**
     * Sends the write request message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws WalletException when there are issues with encryption and decryption
     * @throws UndefinedContextException when the context don't have enough information for this operation
     */
    @SuppressWarnings("WeakerAccess")
    public byte[] write(Context context) throws IOException, UndefinedContextException, WalletException {
        return this.send(context, this.messages.getJSONObject(WriteSchema.WRITE_SCHEMA));
    }

}
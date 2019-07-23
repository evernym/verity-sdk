package com.evernym.verity.sdk.protocols;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.utils.Context;

import org.hyperledger.indy.sdk.IndyException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Builds and sends an encrypted agent message to Verity asking Verity to 
 * write a new Schema to the ledger on behalf of the 
 * SDK/enterprise.
 */

public class WriteSchema extends Protocol {

    private static String MSG_FAMILY = "schema";
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
        super(MSG_FAMILY, MSG_FAMILY_VERSION);
        this.name = name;
        this.version = version;
        this.attrs = attrs;

        defineMessages();
    }

    @Override
    protected void defineMessages() {
        JSONObject message = new JSONObject();
        message.put("@type", this.getMessageType(WriteSchema.WRITE_SCHEMA));
        message.put("@id", WriteSchema.getNewId());
        JSONObject schema = new JSONObject();
        schema.put("name", this.name);
        schema.put("version", this.version);
        schema.put("attrNames", new JSONArray(attrs));
        message.put("schema", schema);
        this.messages.put(WriteSchema.WRITE_SCHEMA, message);
    }


    /**
     * Sends the write request message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    @SuppressWarnings("WeakerAccess")
    public byte[] write(Context context) throws IOException, InterruptedException, ExecutionException, IndyException {
        return this.send(context, this.messages.getJSONObject(WriteSchema.WRITE_SCHEMA));
    }

}
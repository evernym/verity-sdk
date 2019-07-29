package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Builds and sends an encrypted agent message to Verity asking Verity to 
 * write a new Schema to the ledger on behalf of the 
 * SDK/enterprise.
 */

public class Schema extends Protocol {

    // Message type definitions
    public static String WRITE_SCHEMA_MESSAGE_TYPE = "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/write-schema/0.1.0/write";
    public static String PROBLEM_REPORT_MESSAGE_TYPE = "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/write-schema/0.1.0/problem-report";
    public static String STATUS_MESSAGE_TYPE = "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/write-schema/0.1.0/status";

    // Status Definitions
    public static Integer WRITE_SUCCESSFUL_STATUS = 0;

    private String name;
    private String version;
    private String[] attrs;

    /**
     * Creates a Schema from a list of attributes
     * @param attrs
     */
    public Schema(String name, String version, String ...attrs) {
        super();
        this.name = name;
        this.version = version;
        this.attrs = attrs;
    }

    @Override
    public String toString() {
        JSONObject message = new JSONObject();
        message.put("@type", Schema.WRITE_SCHEMA_MESSAGE_TYPE);
        message.put("@id", this.id);
//        JSONObject schema = new JSONObject();
//        schema.put("name", this.name);
//        schema.put("version", this.version);
//        schema.put("attrNames", new JSONArray(attrs));
//        message.put("schema", schema);
        message.put("name", this.name);
        message.put("version", this.version);
        message.put("attrNames", new JSONArray(attrs));
        return message.toString();
    }

    /**
     * Sends the write request message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws WalletException when there are issues with encryption and decryption
     * @throws UndefinedContextException when the context don't have enough information for this operation
     */
    public void write(Context context) throws IOException, UndefinedContextException, WalletException {
        this.sendMessage(context);
    }

}
package com.evernym.verity.sdk.protocols;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.utils.Context;

import org.hyperledger.indy.sdk.IndyException;
import org.json.JSONObject;

/**
 * Builds and sends an encrypted agent message to Verity asking Verity to 
 * write a new Credential Definition to the ledger on behalf of the 
 * SDK/enterprise.
 */
public class CredDef extends Protocol {

    // Message Type Definitions
    public static String WRITE_CRED_DEF_MESSAGE_TYPE = "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/cred-def/0.1/write";
    public static String PROBLEM_REPORT_MESSAGE_TYPE = "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/cred-def/0.1/problem-report";
    public static String STATUS_MESSAGE_TYPE = "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/cred-def/0.1/status";

    // Status Definitions
    public static Integer WRITE_SUCCESSFUL_STATUS = 0;

    private String name;
    private String schemaId;
    private String tag;
    private JSONObject revocationDetails;

    /**
     * Initializes the CredDef object
     * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     */
    public CredDef(String name, String schemaId) {
        super();
        this.name = name;
        this.schemaId = schemaId;
    }

    /**
     * Initializes the CredDef object
      * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     * @param tag An optional tag for the credential definition
     */
    public CredDef(String name, String schemaId, String tag) {
        super();
        this.name = name;
        this.schemaId = schemaId;
        this.tag = tag;
    }

    /**
     * Initializes the CredDef object
     * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     * @param revocationDetails the revocationDetails object defining revocation support. See libvcx documentation for more details.
     */
    public CredDef(String name, String schemaId, JSONObject revocationDetails) {
        super();
        this.name = name;
        this.schemaId = schemaId;
        this.revocationDetails = revocationDetails;
    }

    /**
     * Initializes the CredDef object
     * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     * @param tag An optional tag for the credential definition
     * @param revocationDetails the revocationDetails object defining revocation support. See libvcx documentation for more details.
     */
    public CredDef(String name, String schemaId, String tag, JSONObject revocationDetails) {
        super();
        this.name = name;
        this.schemaId = schemaId;
        this.tag = tag;
        this.revocationDetails = revocationDetails;
    }

    @Override
    public String toString() {
        JSONObject message = new JSONObject();
        message.put("@type", CredDef.WRITE_CRED_DEF_MESSAGE_TYPE);
        message.put("@id", this.id);
        message.put("name", this.name);
        message.put("schemaId", this.schemaId);
        if(this.tag != null) {
            message.put("tag", this.tag);
        }
        if(this.revocationDetails != null) {
            message.put("revocationDetails", this.revocationDetails);
        }
        return message.toString();
    }

    /**
     * Sends the write request message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public void write(Context context) throws IOException, InterruptedException, ExecutionException, IndyException {
        this.sendMessage(context);
    }
}

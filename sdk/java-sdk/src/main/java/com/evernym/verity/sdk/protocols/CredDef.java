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

    private String schemaId;

    /**
     * Creates a CredDef from a given schemaId.
     * @param schemaId
     */
    public CredDef(String schemaId) {
        super();
        this.schemaId = schemaId;
    }

    @Override
    public String toString() {
        JSONObject message = new JSONObject();
        message.put("@type", CredDef.WRITE_CRED_DEF_MESSAGE_TYPE);
        message.put("@id", this.id);
        message.put("schemaId", this.schemaId);
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

package com.evernym.verity.sdk.protocols;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.utils.VerityConfig;

import org.hyperledger.indy.sdk.IndyException;
import org.json.JSONObject;

/**
 * Builds and sends an encrypted agent message to Verity asking Verity to 
 * write a new Credential Definition to the ledger on behalf of the 
 * SDK/enterprise.
 */
public class CredDef extends Protocol {

    // Message Type Definitions
    public static String WRITE_CRED_DEF_MESSAGE_TYPE = "vs.service/cred-def/0.1/write";
    public static String PROBLEM_REPORT_MESSAGE_TYPE = "vs.service/cred-def/0.1/problem-report";
    public static String STATUS_MESSAGE_TYPE = "vs.service/cred-def/0.1/status";

    // Status Definitions
    public static Integer WRITE_REQUEST_RECEIVED_STATUS = 0;
    public static Integer WRITE_SUCCESSFUL_STATUS = 1;

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
     * @param verityConfig an instance of VerityConfig configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public void write(VerityConfig verityConfig) throws IOException, InterruptedException, ExecutionException, IndyException {
        try {
            this.sendMessage(verityConfig);
        } catch(MethodNotSupportedException ex) {
            // do nothing. We haven't disabled this method.
        } catch(Exception ex) {
            throw ex;
        }
    }
}

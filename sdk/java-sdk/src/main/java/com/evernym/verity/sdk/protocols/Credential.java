package com.evernym.verity.sdk.protocols;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.utils.Context;

import org.hyperledger.indy.sdk.IndyException;
import org.json.JSONObject;

public class Credential extends Protocol {

    // Message type definitions
    public static String CREDENTIAL_MESSAGE_TYPE = "vs.service/credential/0.1/credential";
    public static String PROBLEM_REPORT_MESSAGE_TYPE = "vs.service/credential/0.1/problem-report";
    public static String STATUS_MESSAGE_TYPE = "vs.service/credential/0.1/status";

    // Status definitions
    public static Integer OFFER_SENT_STATUS = 0;
    public static Integer OFFER_ACCEPTED_BY_USER_STATUS = 1;
    public static Integer CREDENTIAL_SENT_TO_USER_STATUS = 2;
    public static Integer CREDENTIAL_ACCEPTED_BY_USER_STATUS = 3;

    String credentialDataId;
    String connectionId;
    String credDefId;
    JSONObject credentialValues;
    Integer price;

    /**
     * Creates a new credential
     * @param connectionId The pairwise DID of the connection you would like to send the Credential to.
     * @param credDefId The credDefId of the credential definition being used
     * @param credentialValues key-value pairs of credential attribute fields with the specified params defined in the credential definition
     * @param price The cost of the credential for the user.
     */
    public Credential(String connectionId, String credDefId, JSONObject credentialValues, Integer price) {
        this.credentialDataId = UUID.randomUUID().toString();
        this.connectionId = connectionId;
        this.credDefId = credDefId;
        this.credentialValues = credentialValues;
        this.price = price;
    }

    /**
     * Sends the credential message to the connection
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public void send(Context context) throws IOException, InterruptedException, ExecutionException, IndyException {
        this.sendMessage(context);
    }

    @Override
    public String toString() {
        JSONObject message = new JSONObject();
        message.put("@type", Credential.CREDENTIAL_MESSAGE_TYPE);
        message.put("@id", this.id);
        message.put("connectionId", this.connectionId);
        JSONObject credentialData = new JSONObject();
        credentialData.put("id", credentialDataId);
        credentialData.put("credDefId", this.credDefId);
        credentialData.put("credentialValues", this.credentialValues);
        credentialData.put("price", this.price);
        message.put("credentialData", credentialData);
        return message.toString();
    }
}
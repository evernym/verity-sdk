package com.evernym.verity.sdk.protocols;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.utils.VerityConfig;

import org.hyperledger.indy.sdk.IndyException;
import org.json.JSONObject;

/**
 * Builds and sends a new encrypted agent message for the Connection protocol.
 */
public class Connection extends Protocol {

    // Message type definitions
    public static String NEW_CONNECTION_MESSAGE_TYPE = "did:sov:123456789abcdefghi1234;spec/connecting/0.6/CREATE_CONNECTION";
    public static String PROBLEM_REPORT_MESSAGE_TYPE = "vs.service/connection/0.1/problem_report";
    public static String STATUS_MESSAGE_TYPE = "vs.service/connection/0.1/status";
    
    // Status definitions
    public static Integer AWAITING_RESPONSE_STATUS = 0;
    public static Integer ACCEPTED_BY_USER_STATUS = 1;

    private String sourceId;
    private String phoneNumber = null;
    
    /**
    * Create connection without phone number
    * @param sourceId optional param that sets an id of the connection
    */
    public Connection(String sourceId) {
        super();
        this.sourceId = sourceId;
    }

    /**
    * Create connection with phone number
    * @param sourceId optional param that sets an id of the connection
    * @param phoneNo optional param that sets the sms phone number for an identity holder 
    */
    public Connection(String sourceId, String phoneNo) {
        super();
        this.sourceId = sourceId;
        this.phoneNumber = phoneNo;
    }

    /**
     * Prints the JSON structure of the Connection message
     */
    @Override
    public String toString() {
        JSONObject message = new JSONObject();
        message.put("@type", Connection.NEW_CONNECTION_MESSAGE_TYPE);
        message.put("sourceId", this.sourceId);
        message.put("phoneNo", this.phoneNumber);
        return message.toString();
    }

    /**
     * Sends the connection create message to Verity
     * @param verityConfig an instance of VerityConfig configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public void create(VerityConfig verityConfig) throws IOException, InterruptedException, ExecutionException, IndyException {
        this.sendMessage(verityConfig);
    }
}
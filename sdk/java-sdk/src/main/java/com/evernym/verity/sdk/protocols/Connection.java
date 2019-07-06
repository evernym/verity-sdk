package com.evernym.verity.sdk.protocols;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.utils.Context;

import org.hyperledger.indy.sdk.IndyException;
import org.json.JSONObject;

/**
 * Builds and sends a new encrypted agent message for the Connection protocol.
 */
public class Connection extends Protocol {

    // Message type definitions
    public static String NEW_CONNECTION_MESSAGE_TYPE = "did:sov:123456789abcdefghi1234;spec/connecting/0.6/CREATE_CONNECTION";
//    public static String ACCEPT_INVITATION_MESSAGE_TYPE = "did:sov:123456789abcdefghi1234;spec/connecting/0.6/ACCEPT_CONN_REQ";
    public static String PROBLEM_REPORT_MESSAGE_TYPE = "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/connection/0.1/problem_report";
    public static String STATUS_MESSAGE_TYPE = "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/connection/0.1/status";
    
    // Status definitions
    public static Integer AWAITING_RESPONSE_STATUS = 0;
    public static Integer ACCEPTED_BY_USER_STATUS = 1;

    private String sourceId;
    private String phoneNumber = null;
    private boolean usePublicDid = false;

    // FIXME: Add Connection constructor without params, add error checking to create.
    
    /**
    * Create connection without phone number
    * @param sourceId required optional param that sets an id of the connection
    */
    public Connection(String sourceId) {
        super();
        this.sourceId = sourceId;
    }

    /**
    * Create connection without a phone number that uses a public DID.
    * @param sourceId required param that sets an id of the connection
    * @param usePublicDid optional param that indicates the connection invite should use the institution's public DID.
    */
    public Connection(String sourceId, boolean usePublicDid) {
        super();
        this.sourceId = sourceId;
        this.usePublicDid = usePublicDid;
    }

    /**
    * Create connection with phone number
    * @param sourceId required param that sets an id of the connection
    * @param phoneNo optional param that sets the sms phone number for an identity holder 
    */
    public Connection(String sourceId, String phoneNo) {
        super();
        this.sourceId = sourceId;
        this.phoneNumber = phoneNo;
    }

    /**
    * Create connection with phone number that uses a public DID
    * @param sourceId required param that sets an id of the connection
    * @param phoneNo optional param that sets the sms phone number for an identity holder 
    * @param usePublicDid optional param that indicates the connection invite should use the institution's public DID.
    */
    public Connection(String sourceId, String phoneNo, boolean usePublicDid) {
        super();
        this.sourceId = sourceId;
        this.phoneNumber = phoneNo;
        this.usePublicDid = usePublicDid;
    }

    /**
     * Prints the JSON structure of the Connection message
     */
    @Override
    public String toString() {
        JSONObject message = new JSONObject();
        message.put("@id", this.id);
        message.put("@type", Connection.NEW_CONNECTION_MESSAGE_TYPE);
        message.put("sourceId", this.sourceId);
        if(this.phoneNumber != null) {
            message.put("phoneNo", this.phoneNumber);
        }
        message.put("usePublicDid", this.usePublicDid);
        return message.toString();
    }

    /**
     * Sends the connection create message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public void create(Context context) throws IOException, InterruptedException, ExecutionException, IndyException {
        this.sendMessage(context);
    }
}
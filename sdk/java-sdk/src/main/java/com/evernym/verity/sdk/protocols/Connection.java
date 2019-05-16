package com.evernym.verity.sdk.protocols;

import org.json.JSONObject;

/**
 * Builds a new encrypted agent message with the following format:
 * {
 *   "@type": "vs.service/connect/0.1/new-connection",
 *   "@id": "abcdefg-1234-5678-hijk",
 *   "connectionDetail":{
 *       "sourceId": "CONN_iAmAConnId",
 *       "phoneNo": "8001112222"
 *   }
 * }
 */
public class Connection extends Protocol {
    private static String type = "vs.service/connection/0.1/new_connection";
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
        message.put("@type", Connection.type);
        message.put("@id", this.id);
        JSONObject connectionDetail = new JSONObject();
        connectionDetail.put("sourceId", this.sourceId);
        if(this.phoneNumber != null) {
            connectionDetail.put("phoneNo", this.phoneNumber);
        }
        message.put("connectionDetail", connectionDetail);
        return message.toString();
    }
}
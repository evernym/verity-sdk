package com.evernym.verity.sdk.protocols.connection;

public class Connection {

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
     * @param id optional param that sets an id of the connection
     * @param phoneNo optional param that sets the sms phone number for an identity holder 
     */
    public void newConnection(String id, String phoneNo) {
        System.out.println("Creating new connection" + id + phoneNo);
    }
}
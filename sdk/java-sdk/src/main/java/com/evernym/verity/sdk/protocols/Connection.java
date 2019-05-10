package com.evernym.verity.sdk.protocols;

import org.hyperledger.indy.sdk.IndyException;
import org.json.JSONObject;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.utils.MessagePackaging;
import com.evernym.verity.sdk.utils.WalletContents;

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
public class Connection {
    String type = "vs.service/connect/0.1/new-connection";
    String id;
    String sourceId;
    String phoneNumber = null;

    /*
    * Create connection without phone number
    */
    public Connection() {
        this.id = UUID.randomUUID().toString();
        this.sourceId = UUID.randomUUID().toString();
    }
    
    /*
    * Create connection without phone number
    * @param id optional param that sets an id of the connection
    */
    public Connection(String sourceId) {
        this.id = UUID.randomUUID().toString();
        this.sourceId = sourceId;
    }

    /*
    * Create connection with phone number
    * @param id optional param that sets an id of the connection
    * @param phoneNo optional param that sets the sms phone number for an identity holder 
    */
    public Connection(String sourceId, String phoneNo) {
        this.id = UUID.randomUUID().toString();
        this.sourceId = sourceId;
        this.phoneNumber = phoneNo;
    }

    public byte[] encrypt(WalletContents walletContents) throws InterruptedException, ExecutionException, IndyException {
        return MessagePackaging.packMessageForAgency(walletContents, toString());
    }

    @Override
    public String toString() {
        JSONObject message = new JSONObject();
        message.put("@type", this.type);
        message.put("@id", this.id);
        JSONObject connectionDetail = new JSONObject();
        connectionDetail.put("sourceId", this.sourceId);
        if(this.phoneNumber != null) {
            connectionDetail.put("phoneNumber", this.phoneNumber);
        }
        message.put("connectionDetail", connectionDetail);
        return message.toString();
    }
}
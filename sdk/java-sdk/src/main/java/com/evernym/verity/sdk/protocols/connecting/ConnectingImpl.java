package com.evernym.verity.sdk.protocols.connecting;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Builds and sends a new encrypted agent message for the Connection protocol.
 */
class ConnectingImpl extends Protocol implements Connecting {
    String sourceId;
    String phoneNumber;
    boolean includePublicDID;

    @Override public String sourceId() {return sourceId;}
    @Override public String phoneNumber() {return phoneNumber;}
    @Override public boolean includePublicDID() {return includePublicDID;}
    
    /**
    * Create connection without phone number
    * @param sourceId required optional param that sets an id of the connection
    */
    ConnectingImpl(String sourceId) {
        this(sourceId, null, false);
    }

    /**
    * Create connection without a phone number that uses a public DID.
    * @param sourceId required param that sets an id of the connection
    * @param includePublicDID optional param that indicates the connection invite should use the institution's public DID.
    */
    ConnectingImpl(String sourceId, boolean includePublicDID) {
        this(sourceId, null, includePublicDID);
    }

    /**
    * Create connection with phone number
    * @param sourceId required param that sets an id of the connection
    * @param phoneNo optional param that sets the sms phone number for an identity holder 
    */
    ConnectingImpl(String sourceId, String phoneNo) {
        this(sourceId, phoneNo, false);
    }

    /**
    * Create connection with phone number that uses a public DID
    * @param sourceId required param that sets an id of the connection
    * @param phoneNo optional param that sets the sms phone number for an identity holder 
    * @param includePublicDID optional param that indicates the connection invite should use the institution's public DID.
    */
    ConnectingImpl(String sourceId, String phoneNo, boolean includePublicDID) {
        super();
        this.sourceId = sourceId;
        this.phoneNumber = phoneNo;
        this.includePublicDID = includePublicDID;
    }

    @Override
    public void connect(Context context) throws IOException, VerityException {
        send(context, connectMsg(context));
    }

    @Override
    public JSONObject connectMsg(Context context) {
        JSONObject msg = new JSONObject();
        msg.put("@type", getMessageType(CREATE_CONNECTION));
        msg.put("@id", getNewId());
        addThread(msg);
        msg.put("sourceId", this.sourceId);
        msg.put("phoneNo", this.phoneNumber);
        msg.put("includePublicDID", this.includePublicDID);
        return msg;
    }

    @Override
    public byte[] connectMsgPacked(Context context) throws VerityException {
        return packMsg(context, connectMsg(context));
    }

    @Override
    public void status(Context context) throws IOException, VerityException {
        send(context, statusMsg(context));
    }

    @Override
    public JSONObject statusMsg(Context context) {
        JSONObject msg = new JSONObject();
        msg.put("@type", getMessageType(GET_STATUS));
        msg.put("@id", getNewId());
        msg.put("sourceId", this.sourceId);
        return msg;
    }

    @Override
    public byte[] statusMsgPacked(Context context) throws VerityException {
        return packMsg(context, statusMsg(context));
    }

    @Override
    public void accept(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void acceptMsg(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void acceptMsgPacked(Context context) {
        throw new UnsupportedOperationException();
    }
}
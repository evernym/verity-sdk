package com.evernym.verity.sdk.protocols.connecting;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.connecting.v0_6.ConnectingV0_6;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Builds and sends a new encrypted agent message for the Connection protocol.
 */
class ConnectingImplV0_6 extends Protocol implements ConnectingV0_6 {
    String sourceId;
    String phoneNumber;
    boolean includePublicDID;

    public String sourceId() {return sourceId;}
    public String phoneNumber() {return phoneNumber;}
    public boolean includePublicDID() {return includePublicDID;}

    ConnectingImplV0_6() {
    }

    ConnectingImplV0_6(String sourceId) {
        this(sourceId, null, false);
    }


    ConnectingImplV0_6(String sourceId, boolean includePublicDID) {
        this(sourceId, null, includePublicDID);
    }


    ConnectingImplV0_6(String sourceId, String phoneNo) {
        this(sourceId, phoneNo, false);
    }


    ConnectingImplV0_6(String sourceId, String phoneNo, boolean includePublicDID) {
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
    public JSONObject acceptMsg(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] acceptMsgPacked(Context context) {
        throw new UnsupportedOperationException();
    }

}
package com.evernym.verity.sdk.protocols.updateendpoint;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;

public class UpdateEndpointImpl extends Protocol implements UpdateEndpoint {

    UpdateEndpointImpl() {
        super();
    }

    @Override
    public void update(Context context) throws IOException, VerityException {
        send(context, updateMsg(context));
    }

    @Override
    public JSONObject updateMsg(Context context) throws UndefinedContextException {
        int COM_METHOD_TYPE = 2;

        JSONObject message = new JSONObject();

        message.put("@type", getMessageType(UPDATE_ENDPOINT));
        message.put("@id", getNewId());
        JSONObject comMethod = new JSONObject();
        comMethod.put("id", "webhook");
        comMethod.put("type", COM_METHOD_TYPE);
        comMethod.put("value", context.endpointUrl());
        message.put("comMethod", comMethod);
        return message;
    }

    @Override
    public byte[] updateMsgPacked(Context context) throws VerityException {
        return packMsg(context, updateMsg(context));
    }
}
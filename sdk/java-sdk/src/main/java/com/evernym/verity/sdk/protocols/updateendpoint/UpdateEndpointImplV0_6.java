package com.evernym.verity.sdk.protocols.updateendpoint;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.updateendpoint.v0_6.UpdateEndpointV0_6;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

class UpdateEndpointImplV0_6 extends Protocol implements UpdateEndpointV0_6 {

    UpdateEndpointImplV0_6() {
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
        JSONObject packaging = new JSONObject();
        packaging.put("pkgType", "1.0");
        JSONArray recipientKeys = new JSONArray();
        recipientKeys.put(context.sdkVerKey());
        packaging.put("recipientKeys", recipientKeys);
        comMethod.put("packaging", packaging);
        message.put("comMethod", comMethod);
        return message;
    }

    @Override
    public byte[] updateMsgPacked(Context context) throws VerityException {
        return packMsg(context, updateMsg(context));
    }
}
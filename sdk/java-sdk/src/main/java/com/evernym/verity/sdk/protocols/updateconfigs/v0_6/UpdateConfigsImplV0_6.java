package com.evernym.verity.sdk.protocols.updateconfigs.v0_6;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class UpdateConfigsImplV0_6 extends Protocol implements UpdateConfigsV0_6 {
    String name;
    String logoUrl;

    public UpdateConfigsImplV0_6(String name, String logoUrl) {
        super();
        this.name = name;
        this.logoUrl = logoUrl;
    }

    @Override
    public void update(Context context) throws IOException, VerityException {
        send(context, updateMsg(context));
    }

    @Override
    public JSONObject updateMsg(Context context) {
        JSONObject message = new JSONObject();
        JSONObject type = new JSONObject();
        type.put("name", UPDATE_CONFIGS);
        type.put("ver", "1.0");
        message.put("@type", type);
        JSONArray configs = new JSONArray();
        JSONObject item1 = new JSONObject();
        item1.put("name", "name");
        item1.put("value", this.name);
        configs.put(item1);
        JSONObject item2 = new JSONObject();
        item2.put("name", "logoUrl");
        item2.put("value", this.logoUrl);
        configs.put(item2);
        message.put("configs", configs);
        return message;
    }

    @Override
    public byte[] updateMsgPacked(Context context) throws VerityException {
        return packMsg(context, updateMsg(context));
    }
}
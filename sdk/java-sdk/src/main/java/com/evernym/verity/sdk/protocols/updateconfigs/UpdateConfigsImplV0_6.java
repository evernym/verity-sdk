package com.evernym.verity.sdk.protocols.updateconfigs;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.AbstractProtocol;
import com.evernym.verity.sdk.protocols.updateconfigs.v0_6.UpdateConfigsV0_6;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/*
 * NON_VISIBLE
 *
 * This is an implementation of UpdateConfigsImplV0_6 but is not viable to user of Verity SDK. Created using the
 * static UpdateConfigs class
 */
class UpdateConfigsImplV0_6 extends AbstractProtocol implements UpdateConfigsV0_6 {

    final String UPDATE_CONFIGS = "update";
    final String GET_STATUS = "get-status";

    String name;
    String logoUrl;

    UpdateConfigsImplV0_6(String name, String logoUrl) {
        super();
        this.name = name;
        this.logoUrl = logoUrl;
    }

    UpdateConfigsImplV0_6() {
        super();
    }

    @Override
    public void update(Context context) throws IOException, VerityException {
        send(context, updateMsg(context));
    }

    @Override
    public JSONObject updateMsg(Context context) {
        JSONObject message = new JSONObject();
        message.put("@type", messageType(UPDATE_CONFIGS));
        message.put("@id", getNewId());
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

    public void status(Context context) throws IOException, VerityException {
        send(context, statusMsg(context));
    }

    @Override
    public JSONObject statusMsg(Context context) {
        JSONObject msg = new JSONObject();
        msg.put("@type", messageType(GET_STATUS));
        msg.put("@id", getNewId());
        addThread(msg);

        return msg;
    }

    @Override
    public byte[] statusMsgPacked(Context context) throws VerityException {
        return packMsg(context, statusMsg(context));
    }
}
package com.evernym.verity.sdk.protocols.updateconfigs;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class UpdateConfigsImpl extends Protocol implements UpdateConfigs {
    String name;
    String logoUrl;

    UpdateConfigsImpl(String name, String logoUrl) {
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
        message.put("@type", getMessageType(UPDATE_CONFIGS));
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

    /**
     * Sends the get status message to the connection
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    public void status(Context context) throws IOException, VerityException {
        send(context, statusMsg(context));
    }

    @Override
    public JSONObject statusMsg(Context context) {
        JSONObject msg = new JSONObject();
        msg.put("@type", getMessageType(GET_STATUS));
        msg.put("@id", getNewId());
        addThread(msg);

        return msg;
    }

    @Override
    public byte[] statusMsgPacked(Context context) throws VerityException {
        return packMsg(context, statusMsg(context));
    }
}
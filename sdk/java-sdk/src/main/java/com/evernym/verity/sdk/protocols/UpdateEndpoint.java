package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

public class UpdateEndpoint extends Protocol {

    private static String MSG_FAMILY = "configs";
    private static String MSG_FAMILY_VERSION = "0.6";

    // Messages
    @SuppressWarnings("WeakerAccess")
    public static String UPDATE_ENDPOINT = "UPDATE_COM_METHOD";

    final private Context context;
    final String endpointUrl;

    public UpdateEndpoint(Context context) throws UndefinedContextException {
        super();
        this.context = context;
        this.endpointUrl = this.context.endpointUrl();

        defineMessages();
    }

    @Override
    protected void defineMessages() {
        int COM_METHOD_TYPE = 2;

        JSONObject message = new JSONObject();
        message.put("@type", UpdateEndpoint.getMessageType(UpdateEndpoint.UPDATE_ENDPOINT));
        message.put("@id", UpdateEndpoint.getNewId());
            JSONObject comMethod = new JSONObject();
            comMethod.put("id", "webhook");
            comMethod.put("type", COM_METHOD_TYPE);
            comMethod.put("value", this.endpointUrl);
        message.put("comMethod", comMethod);
        this.messages.put(UpdateEndpoint.UPDATE_ENDPOINT, message);
    }

    public static String getMessageType(String msgName) {
        return Util.getMessageType(UpdateEndpoint.MSG_FAMILY, UpdateEndpoint.MSG_FAMILY_VERSION, msgName);
    }

    public static String getProblemReportMessageType() {
        return Util.getProblemReportMessageType(UpdateEndpoint.MSG_FAMILY, UpdateEndpoint.MSG_FAMILY_VERSION);
    }

    public static String getStatusMessageType() {
        return Util.getStatusMessageType(UpdateEndpoint.MSG_FAMILY, UpdateEndpoint.MSG_FAMILY_VERSION);
    }

    public byte[] update() throws IOException, UndefinedContextException, WalletException {
        return this.send(this.context, this.messages.getJSONObject(UpdateEndpoint.UPDATE_ENDPOINT));
    }
}
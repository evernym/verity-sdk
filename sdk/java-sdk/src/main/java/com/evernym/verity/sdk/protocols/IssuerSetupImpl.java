package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;

public class IssuerSetupImpl extends Protocol implements IssuerSetup{

    IssuerSetupImpl() {
        super();
    }

    @Override
    protected void defineMessages() {
        throw new UnsupportedOperationException("DO NOT USE");
    }

    public void create(Context context) throws IOException, VerityException {
        this.send(context, createMsg(context));
    }

    @Override
    public JSONObject createMsg(Context context) {
        JSONObject message = new JSONObject();
        message.put("@type", getMessageType(CREATE));
        message.put("@id", getNewId());
        return message;
    }

    @Override
    public byte[] createMsgPacked(Context context) throws VerityException {
        return this.packMsg(context, createMsg(context));
    }

    @Override
    public void currentPublicIdentifier(Context context) throws IOException, VerityException {
        this.send(context, currentPublicIdentifierMsg(context));
    }

    @Override
    public JSONObject currentPublicIdentifierMsg(Context context) {
        JSONObject message = new JSONObject();
        message.put("@type", getMessageType(CURRENT_PUBLIC_IDENTIFIER));
        message.put("@id", getNewId());
        return message;
    }

    @Override
    public byte[] currentPublicIdentifierMsgPacked(Context context) throws VerityException {
        return this.packMsg(context, currentPublicIdentifierMsg(context));
    }
}

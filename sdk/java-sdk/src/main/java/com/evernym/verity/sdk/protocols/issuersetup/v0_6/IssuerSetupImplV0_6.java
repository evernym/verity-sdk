package com.evernym.verity.sdk.protocols.issuersetup.v0_6;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;

public class IssuerSetupImplV0_6 extends Protocol implements IssuerSetupV0_6 {

    public IssuerSetupImplV0_6() {
        super();
    }

    public void create(Context context) throws IOException, VerityException {
        send(context, createMsg(context));
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
        return packMsg(context, createMsg(context));
    }

    @Override
    public void currentPublicIdentifier(Context context) throws IOException, VerityException {
        send(context, currentPublicIdentifierMsg(context));
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
        return packMsg(context, currentPublicIdentifierMsg(context));
    }
}

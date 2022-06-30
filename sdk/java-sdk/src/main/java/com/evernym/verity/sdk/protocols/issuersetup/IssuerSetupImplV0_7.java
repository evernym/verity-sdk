package com.evernym.verity.sdk.protocols.issuersetup;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.AbstractProtocol;
import com.evernym.verity.sdk.protocols.issuersetup.v0_7.IssuerSetupV0_7;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;

/*
 * NON_VISIBLE
 *
 * This is an implementation of IssuerSetupImplV0_7 but is not visible to user of Verity SDK. Created using the
 * static IssuerSetup class
 */
class IssuerSetupImplV0_7 extends AbstractProtocol implements IssuerSetupV0_7 {

    IssuerSetupImplV0_7() {
        super();
    }

    public void create(Context context, String ledgerPrefix) throws IOException, VerityException {
        send(context, createMsg(context, ledgerPrefix));
    }

    public void create(Context context, String ledgerPrefix, String endorser) throws IOException, VerityException {
        send(context, createMsg(context, ledgerPrefix, endorser));
    }

    @Override
    public JSONObject createMsg(Context context, String ledgerPrefix) {
        JSONObject message = new JSONObject();
        message.put("@type", messageType(CREATE));
        message.put("@id", getNewId());
        message.put("ledgerPrefix", ledgerPrefix);
        addThread(message);
        return message;
    }

    @Override
    public JSONObject createMsg(Context context, String ledgerPrefix, String endorser) {
        JSONObject message = new JSONObject();
        message.put("@type", messageType(CREATE));
        message.put("@id", getNewId());
        message.put("ledgerPrefix", ledgerPrefix);
        message.put("endorser", endorser);
        addThread(message);
        return message;
    }

    @Override
    public byte[] createMsgPacked(Context context, String ledgerPrefix) throws VerityException {
        return packMsg(context, createMsg(context, ledgerPrefix));
    }

    @Override
    public byte[] createMsgPacked(Context context, String ledgerPrefix, String endorser) throws VerityException {
        return packMsg(context, createMsg(context, ledgerPrefix, endorser));
    }

    @Override
    public void currentPublicIdentifier(Context context) throws IOException, VerityException {
        send(context, currentPublicIdentifierMsg(context));
    }

    @Override
    public JSONObject currentPublicIdentifierMsg(Context context) {
        JSONObject message = new JSONObject();
        message.put("@type", messageType(CURRENT_PUBLIC_IDENTIFIER));
        message.put("@id", getNewId());
        addThread(message);
        return message;
    }

    @Override
    public byte[] currentPublicIdentifierMsgPacked(Context context) throws VerityException {
        return packMsg(context, currentPublicIdentifierMsg(context));
    }
}

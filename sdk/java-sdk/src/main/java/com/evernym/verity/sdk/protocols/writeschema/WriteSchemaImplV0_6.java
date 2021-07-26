package com.evernym.verity.sdk.protocols.writeschema;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.AbstractProtocol;
import com.evernym.verity.sdk.protocols.writeschema.v0_6.WriteSchemaV0_6;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.DbcUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/*
 * NON_VISIBLE
 *
 * This is an implementation of IssuerSetupImplV0_6 but is not viable to user of Verity SDK. Created using the
 * static IssuerSetup class
 */
class WriteSchemaImplV0_6 extends AbstractProtocol implements WriteSchemaV0_6 {

    final String name;
    final String version;
    final String[] attrs;


    public WriteSchemaImplV0_6(String name, String version, String ...attrs) {
        super();

        DbcUtil.requireStringNotNullOrEmpty(name, "name");
        DbcUtil.requireStringNotNullOrEmpty(version, "version");
        DbcUtil.requireArrayNotContainNull(attrs, "attrs");

        this.name = name;
        this.version = version;
        this.attrs = attrs;
    }

    public void write(Context context) throws IOException, VerityException {
        send(context, writeMsg(context));
    }

    @Override
    public JSONObject writeMsg(Context context) {
        JSONObject message = new JSONObject();
        message.put("@type", messageType(WRITE_SCHEMA));
        message.put("@id", getNewId());
        addThread(message);
        message.put("name", this.name);
        message.put("version", this.version);
        message.put("attrNames", new JSONArray(attrs));
        return message;
    }

    @Override
    public byte[] writeMsgPacked(Context context) throws VerityException {
        return packMsg(context, writeMsg(context));
    }

    @Override
    public void write(Context context, String endorserDID) throws IOException, VerityException {
        send(context, writeMsg(context, endorserDID));
    }

    @Override
    public JSONObject writeMsg(Context context, String endorserDID) {
        JSONObject message = writeMsg(context);
        message.put("endorserDID", endorserDID);
        return message;
    }

    @Override
    public byte[] writeMsgPacked(Context context, String endorserDID) throws VerityException {
        return packMsg(context, writeMsg(context, endorserDID));
    }

}
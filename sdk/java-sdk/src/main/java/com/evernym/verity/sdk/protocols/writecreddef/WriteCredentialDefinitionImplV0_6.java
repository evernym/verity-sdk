package com.evernym.verity.sdk.protocols.writecreddef;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.protocols.writecreddef.v0_6.RevocationRegistryConfig;
import com.evernym.verity.sdk.protocols.writecreddef.v0_6.WriteCredentialDefinitionV0_6;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;


class WriteCredentialDefinitionImplV0_6 extends Protocol implements WriteCredentialDefinitionV0_6 {

    final String name;
    protected final String schemaId;
    final String tag;
    final RevocationRegistryConfig revocationConfig;

    WriteCredentialDefinitionImplV0_6(String name, String schemaId) {
        this(name, schemaId, null, null);
    }


    WriteCredentialDefinitionImplV0_6(String name,
                                      String schemaId,
                                      String tag) {
        this(name, schemaId, tag, null);
    }


    WriteCredentialDefinitionImplV0_6(String name, String schemaId, RevocationRegistryConfig revocation) {
        this(name, schemaId, null, revocation);
    }


    WriteCredentialDefinitionImplV0_6(String name,
                                      String schemaId,
                                      String tag,
                                      RevocationRegistryConfig revocation) {
        super();
        this.name = name;
        this.schemaId = schemaId;
        this.tag = tag;
        this.revocationConfig = revocation;
    }


    @SuppressWarnings("WeakerAccess")
    public void write(Context context) throws IOException, VerityException {
        send(context, writeMsg(context));
    }

    @Override
    public JSONObject writeMsg(Context context) {
        JSONObject message = new JSONObject();
        message.put("@type", getMessageType(WRITE_CRED_DEF));
        message.put("@id", WriteCredentialDefinitionImplV0_6.getNewId());
        message.put("name", this.name);
        message.put("schemaId", this.schemaId);
        if(this.tag != null) message.put("tag", this.tag);
        if(this.revocationConfig != null) message.put("revocationDetails", this.revocationConfig.toJson());
        return message;
    }

    @Override
    public byte[] writeMsgPacked(Context context) throws VerityException {
        return packMsg(context, writeMsg(context));
    }
}

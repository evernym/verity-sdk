package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Builds and sends an encrypted agent message to Verity asking Verity to 
 * write a new Credential Definition to the ledger on behalf of the 
 * SDK/enterprise.
 */
public class WriteCredentialDefinitionImpl extends Protocol implements WriteCredentialDefinition {

    String name;
    protected String schemaId;
    String tag;
    JSONObject revocationDetails;

    /**
     * Initializes the CredDef object
     * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     */
    WriteCredentialDefinitionImpl(String name, String schemaId) {
        this(name, schemaId, null, null);
    }

    /**
     * Initializes the CredDef object
      * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     * @param tag An optional tag for the credential definition
     */
    WriteCredentialDefinitionImpl(String name, String schemaId, String tag) {
        this(name, schemaId, tag, null);
    }

    /**
     * Initializes the CredDef object
     * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     * @param revocationDetails the revocationDetails object defining revocation support. See libvcx documentation for more details.
     */
    WriteCredentialDefinitionImpl(String name, String schemaId, JSONObject revocationDetails) {
        this(name, schemaId, null, revocationDetails);
    }

    /**
     * Initializes the CredDef object
     * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     * @param tag An optional tag for the credential definition
     * @param revocationDetails the revocationDetails object defining revocation support. See libvcx documentation for more details.
     */
    WriteCredentialDefinitionImpl(String name, String schemaId, String tag, JSONObject revocationDetails) {
        super();
        this.name = name;
        this.schemaId = schemaId;
        this.tag = tag;
        this.revocationDetails = revocationDetails;
    }

    @Override
    protected void defineMessages() {
        throw new UnsupportedOperationException("DO NOT USE");
    }

    /**
     * Sends the write request message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    @SuppressWarnings("WeakerAccess")
    public void write(Context context) throws IOException, VerityException {
        this.send(context, writeMsg(context));
    }

    @Override
    public JSONObject writeMsg(Context context) {
        JSONObject message = new JSONObject();
        message.put("@type", getMessageType(WRITE_CRED_DEF));
        message.put("@id", WriteCredentialDefinitionImpl.getNewId());
        message.put("name", this.name);
        message.put("schemaId", this.schemaId);
        message.put("tag", this.tag);
        message.put("revocationDetails", this.revocationDetails);
        return message;
    }

    @Override
    public byte[] writeMsgPacked(Context context) throws VerityException {
        return this.packMsg(context, writeMsg(context));
    }
}

package com.evernym.verity.sdk.protocols;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.utils.Context;

import com.evernym.verity.sdk.utils.Util;
import org.hyperledger.indy.sdk.IndyException;
import org.json.JSONObject;

/**
 * Builds and sends an encrypted agent message to Verity asking Verity to 
 * write a new Credential Definition to the ledger on behalf of the 
 * SDK/enterprise.
 */
public class WriteCredentialDefinition extends Protocol {

    private static String MSG_FAMILY = "cred-def";
    private static String MSG_FAMILY_VERSION = "0.1";

    // Messages
    @SuppressWarnings("WeakerAccess")
    public static String WRITE_CRED_DEF = "write";

    // Status Definitions
    public static Integer WRITE_SUCCESSFUL_STATUS = 0;

    String name;
    protected String schemaId;
    String tag;
    JSONObject revocationDetails;

    /**
     * Initializes the CredDef object
     * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     */
    public WriteCredentialDefinition(String name, String schemaId) {
        this(name, schemaId, null, null);
    }

    /**
     * Initializes the CredDef object
      * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     * @param tag An optional tag for the credential definition
     */
    public WriteCredentialDefinition(String name, String schemaId, String tag) {
        this(name, schemaId, tag, null);
    }

    /**
     * Initializes the CredDef object
     * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     * @param revocationDetails the revocationDetails object defining revocation support. See libvcx documentation for more details.
     */
    public WriteCredentialDefinition(String name, String schemaId, JSONObject revocationDetails) {
        this(name, schemaId, null, revocationDetails);
    }

    /**
     * Initializes the CredDef object
     * @param name The name of the new credential definition
     * @param schemaId The id of the schema this credential definition will be based on
     * @param tag An optional tag for the credential definition
     * @param revocationDetails the revocationDetails object defining revocation support. See libvcx documentation for more details.
     */
    @SuppressWarnings("WeakerAccess")
    public WriteCredentialDefinition(String name, String schemaId, String tag, JSONObject revocationDetails) {
        super();
        this.name = name;
        this.schemaId = schemaId;
        this.tag = tag;
        this.revocationDetails = revocationDetails;

        defineMessages();
    }

    public static String getMessageType(String msgName) {
        return Util.getMessageType(WriteCredentialDefinition.MSG_FAMILY, WriteCredentialDefinition.MSG_FAMILY_VERSION, msgName);
    }

    public static String getProblemReportMessageType() {
        return Util.getProblemReportMessageType(WriteCredentialDefinition.MSG_FAMILY, WriteCredentialDefinition.MSG_FAMILY_VERSION);
    }

    public static String getStatusMessageType() {
        return Util.getStatusMessageType(WriteCredentialDefinition.MSG_FAMILY, WriteCredentialDefinition.MSG_FAMILY_VERSION);
    }

    @Override
    protected void defineMessages() {
        JSONObject message = new JSONObject();
        message.put("@type", WriteCredentialDefinition.getMessageType(WriteCredentialDefinition.WRITE_CRED_DEF));
        message.put("@id", WriteCredentialDefinition.getNewId());
        message.put("name", this.name);
        message.put("schemaId", this.schemaId);
        message.put("tag", this.tag);
        message.put("revocationDetails", this.revocationDetails);
        this.messages.put(WriteCredentialDefinition.WRITE_CRED_DEF, message);
    }

    /**
     * Sends the write request message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    @SuppressWarnings("WeakerAccess")
    public byte[] write(Context context) throws IOException, InterruptedException, ExecutionException, IndyException {
        return this.send(context, this.messages.getJSONObject(WriteCredentialDefinition.WRITE_CRED_DEF));
    }
}

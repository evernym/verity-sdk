package com.evernym.verity.sdk.protocols.writecreddef;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Builds and sends an encrypted agent message to Verity asking Verity to 
 * write a new Credential Definition to the ledger on behalf of the 
 * SDK/enterprise.
 */
public interface WriteCredentialDefinition extends MessageFamily {
    String MSG_QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
    String MSG_FAMILY = "write-cred-def";
    String MSG_FAMILY_VERSION = "0.6";

    default String qualifier() {return MSG_QUALIFIER;}
    default String family() {return MSG_FAMILY;}
    default String version() {return MSG_FAMILY_VERSION;}

    String WRITE_CRED_DEF = "write";

    static WriteCredentialDefinition v0_6(String name, String schemaId) {
        return new WriteCredentialDefinitionImpl(name, schemaId);
    }

    static WriteCredentialDefinition v0_6(String name, String schemaId, String tag) {
        return new WriteCredentialDefinitionImpl(name, schemaId, tag);
    }

    static WriteCredentialDefinition v0_6(String name, String schemaId, RevocationRegistryConfig revocation) {
        return new WriteCredentialDefinitionImpl(name, schemaId, revocation);
    }

    static WriteCredentialDefinition v0_6(String name, String schemaId, String tag, RevocationRegistryConfig revocation) {
        return new WriteCredentialDefinitionImpl(name,schemaId, tag, revocation);
    }

    static RevocationRegistryConfig disabledRegistryConfig() {
        JSONObject json = new JSONObject();
        json.put("support_revocation", false);
        return new RevocationRegistryConfig(json);
    }

    static RevocationRegistryConfig revocationRegistryConfig(String tailsFile, int totalCredentials) {
        JSONObject json = new JSONObject();
        json.put("support_revocation", true);
        json.put("tails_file", tailsFile);
        json.put("max_creds", totalCredentials);
        return new RevocationRegistryConfig(json);
    }

    /**
     * Sends the write request message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    void write(Context context) throws IOException, VerityException;

    /**
     *
     * @param context
     * @return
     * @throws UndefinedContextException
     */
    JSONObject writeMsg(Context context) throws UndefinedContextException;

    /**
     *
     * @param context
     * @return
     * @throws VerityException
     */
    byte[] writeMsgPacked(Context context) throws VerityException;
}
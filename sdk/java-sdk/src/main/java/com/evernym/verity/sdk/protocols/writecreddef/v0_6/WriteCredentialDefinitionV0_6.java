package com.evernym.verity.sdk.protocols.writecreddef.v0_6;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

/**
 * An interface for controlling a 0.6 WriteCredentialDefinition protocol.
 */
public interface WriteCredentialDefinitionV0_6 extends MessageFamily {
    /**
     * The qualifier for the message family. Uses Evernym's qualifier.
     */
    String QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
    /**
     * The name for the message family.
     */
    String FAMILY = "write-cred-def";
    /**
     * The version for the message family.
     */
    String VERSION = "0.6";


    /**
     * @see MessageFamily#qualifier()
     */
    default String qualifier() {return QUALIFIER;}
    /**
     * @see MessageFamily#family()
     */
    default String family() { return FAMILY;}
    /**
     * @see MessageFamily#version()
     */
    default String version() {return VERSION;}

    /**
     Name for 'write' control message
     */
    String WRITE_CRED_DEF = "write";


    /**
     * Creates a RevocationRegistryConfig object that effectively disables the revocation registry
     *
     * @return a RevocationRegistryConfig object
     */
    static RevocationRegistryConfig disabledRegistryConfig() {
        JSONObject json = new JSONObject();
        json.put("support_revocation", false);
        return new RevocationRegistryConfig(json);
    }

//    static RevocationRegistryConfig revocationRegistryConfig(String tailsFile, int totalCredentials) {
//        JSONObject json = new JSONObject();
//        json.put("support_revocation", true);
//        json.put("tails_file", tailsFile);
//        json.put("max_creds", totalCredentials);
//        return new RevocationRegistryConfig(json);
//    }

    /**
     * Directs verity-application to write the specified Credential Definition to the Ledger
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws VerityException when wallet operations fails or given invalid context
     */
    void write(Context context) throws IOException, VerityException;

    /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     * @throws VerityException when given invalid context
     *
     * @see #write
     */
    JSONObject writeMsg(Context context) throws UndefinedContextException;

    /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     * @throws VerityException when wallet operations fails or given invalid context
     *
     * @see #write
     */
    byte[] writeMsgPacked(Context context) throws VerityException;
}
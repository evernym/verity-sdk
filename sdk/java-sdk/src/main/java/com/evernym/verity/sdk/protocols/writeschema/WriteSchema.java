package com.evernym.verity.sdk.protocols.writeschema;

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
 * write a new Schema to the ledger on behalf of the
 * SDK/enterprise.
 */
public interface WriteSchema extends MessageFamily {
    default String qualifier() {return Util.EVERNYM_MSG_QUALIFIER;}
    default String family() {return "write-schema";}
    default String version() {return "0.6";}

    // Messages
    String WRITE_SCHEMA = "write";

    static WriteSchema v0_6(String name, String version, String ...attrs) {
        return new WriteSchemaImpl(name, version, attrs);
    }

    /**
     * Sends the write request message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    void write(Context context) throws IOException, VerityException;
    JSONObject writeMsg(Context context) throws UndefinedContextException;
    byte[] writeMsgPacked(Context context) throws VerityException;
}
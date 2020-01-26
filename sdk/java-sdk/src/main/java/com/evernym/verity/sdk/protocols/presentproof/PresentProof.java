package com.evernym.verity.sdk.protocols.presentproof;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Builds and sends a message to Verity asking it to send a Proof Request to a connection
 */
public interface PresentProof extends MessageFamily {
    default String qualifier() {return Util.EVERNYM_MSG_QUALIFIER;}
    default String family() {return "present-proof";}
    default String version() {return "0.6";}

    String PROOF_REQUEST = "request";
    String GET_STATUS = "get-status";

    static PresentProof v0_6(String forRelationship,
                             String name,
                             Attribute...attributes) {
        return new PresentProofImpl(forRelationship, name, attributes);
    }

    static PresentProof v0_6(String forRelationship,
                             String threadId) {
        return new PresentProofImpl(forRelationship, threadId);
    }

    static Attribute attribute(String name, Restriction...restrictions) {
        return new Attribute(name, restrictions);
    }

    static Predicate predicate(String name, int value, Restriction...restrictions) {
        return new Predicate(name, value, restrictions);
    }


    /**
     * Sends the proof request message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    void request(Context context) throws IOException, VerityException;

    JSONObject requestMsg(Context context) throws VerityException;

    byte[] requestMsgPacked(Context context) throws VerityException;


    void accept(Context context) throws IOException, VerityException;

    JSONObject acceptMsg(Context context) throws VerityException;

    byte[] acceptMsgPacked(Context context) throws VerityException;

    /**
     * Sends the status request message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    void status(Context context) throws IOException, VerityException;

    JSONObject statusMsg(Context context) throws VerityException;

    byte[] statusMsgPacked(Context context) throws VerityException;
}
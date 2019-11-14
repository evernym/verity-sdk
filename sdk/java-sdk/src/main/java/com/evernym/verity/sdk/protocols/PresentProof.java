package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Builds and sends a message to Verity asking it to send a Proof Request to a connection
 */
public class PresentProof extends Protocol {
    final private static String MSG_QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
    final private static String MSG_FAMILY = "present-proof";
    final private static String MSG_FAMILY_VERSION = "0.6";

    // Messages
    @SuppressWarnings("WeakerAccess")
    public static String PROOF_REQUEST = "request";
    public static String GET_STATUS = "get-status";

    // Status Definitions
    public static Integer PROOF_REQUEST_SENT_STATUS = 0;
    public static Integer PROOF_RECEIVED_STATUS = 1;

    String forRelationship;
    String name;
    JSONArray proofAttrs;
    JSONArray proofPredicates;
    JSONObject revocationInterval;

    /**
     * Initializes the proof request object
     * @param forRelationship DID of relationship where proof request will be sent to
     * @param name The name of the proof request
     * @param proofAttrs The requested attributes of the proof request
     */
    public PresentProof(String forRelationship, String name, JSONArray proofAttrs) {
        this(forRelationship, name, proofAttrs, null, null);
    }

    /**
     * Initializes the proof request object
     * @param forRelationship DID of relationship where proof request will be sent to
     * @param name The name of the proof request
     * @param proofAttrs The requested attributes of the proof request
     * @param proofPredicates The requested predicates of the proof request
     */
    public PresentProof(String forRelationship, String name, JSONArray proofAttrs, JSONArray proofPredicates) {
        this(forRelationship, name, proofAttrs, proofPredicates, null);
    }

    /**
     * Initializes the proof request object with a revocation interval
     * @param forRelationship DID of relationship where proof request will be sent to
     * @param name The name of the proof request
     * @param proofAttrs The requested attributes of the proof request
     * @param proofPredicates The requested predicates of the proof request
     * @param revocationInterval The proof request revocation interval
     */
    @SuppressWarnings("WeakerAccess")
    public PresentProof(String forRelationship, String name, JSONArray proofAttrs, JSONArray proofPredicates, JSONObject revocationInterval) {
        super();
        this.forRelationship = forRelationship;
        this.name = name;
        this.proofAttrs = proofAttrs;
        this.proofPredicates = proofPredicates;
        this.revocationInterval = revocationInterval;
        defineMessages();
    }

    public static String getMessageType(String msgName) {
        return Util.getMessageType(MSG_QUALIFIER, MSG_FAMILY, MSG_FAMILY_VERSION, msgName);
    }

    public static String getProblemReportMessageType() {
        return Util.getProblemReportMessageType(MSG_QUALIFIER, MSG_FAMILY, MSG_FAMILY_VERSION);
    }

    public static String getStatusMessageType() {
        return Util.getStatusMessageType(MSG_QUALIFIER, MSG_FAMILY, MSG_FAMILY_VERSION);
    }

    @Override
    protected void defineMessages() {
        JSONObject requestMsg = new JSONObject();
        requestMsg.put("@type", PresentProof.getMessageType(PROOF_REQUEST));
        requestMsg.put("@id", PresentProof.getNewId());
        addThread(requestMsg);
        requestMsg.put("~for_relationship", this.forRelationship);
        requestMsg.put("name", this.name);
        requestMsg.put("proofAttrs", proofAttrs);
        if (this.proofPredicates != null)
            requestMsg.put("proofPredicates", proofPredicates);
        if (this.revocationInterval != null)
            requestMsg.put("revocationInterval", this.revocationInterval);
        this.messages.put(PROOF_REQUEST, requestMsg);

        JSONObject statusMsg = new JSONObject();
        statusMsg.put("@type", PresentProof.getMessageType(GET_STATUS));
        statusMsg.put("@id", PresentProof.getNewId());
        addThread(statusMsg);
        statusMsg.put("~for_relationship", this.forRelationship);
        this.messages.put(GET_STATUS, statusMsg);
    }

    /**
     * Sends the proof request message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    public byte[] request(Context context) throws IOException, UndefinedContextException, WalletException {
        return this.send(context, this.messages.getJSONObject(PROOF_REQUEST));
    }

    /**
     * Sends the status request message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    public byte[] status(Context context) throws IOException, UndefinedContextException, WalletException {
        return this.send(context, this.messages.getJSONObject(GET_STATUS));
    }
}
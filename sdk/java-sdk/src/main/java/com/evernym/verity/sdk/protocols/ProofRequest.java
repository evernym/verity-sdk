package com.evernym.verity.sdk.protocols;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.utils.Context;

import org.hyperledger.indy.sdk.IndyException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Builds and sends a message to Verity asking it to send a Proof Request to a connection
 */
public class ProofRequest extends Protocol {

    // Message Type Definitions
    public static String PROOF_REQUEST_MESSAGE_TYPE = "vs.service/proof/0.1/request";
    public static String PROBLEM_REPORT_MESSAGE_TYPE = "vs.service/proof/0.1/problem-report";
    public static String STATUS_MESSAGE_TYPE = "vs.service/proof/0.1/status";

    // Status Definitions
    public static Integer PROOF_REQUEST_SENT_STATUS = 0;
    public static Integer PROOF_RECEIVED_STATUS = 1;

    private String connectionId;
    private String name;
    private JSONArray proofAttrs;
    private JSONObject revocationInterval;

    /**
     * Initializes the proof request object
     * @param connectionId The connectionId the proof request will be sent to
     * @param name The name of the proof request
     * @param proofAttrs The requested attributes of the proof request
     */
    public ProofRequest(String connectionId, String name, JSONArray proofAttrs) {
        this.connectionId = connectionId;
        this.name = name;
        this.proofAttrs = proofAttrs;
    }

    /**
     * Initializes the proof request object with a revocation interval
     * @param connectionId The connectionId the proof request will be sent to
     * @param name The name of the proof request
     * @param proofAttrs The requested attributes of the proof request
     * @param revocationInterval The proof request revocation interval
     */
    public ProofRequest(String connectionId, String name, JSONArray proofAttrs, JSONObject revocationInterval) {
        this.connectionId = connectionId;
        this.name = name;
        this.proofAttrs = proofAttrs;
        this.revocationInterval = revocationInterval;
    }

    @Override
    public String toString() {
        JSONObject message = new JSONObject();
        message.put("@type", ProofRequest.PROOF_REQUEST_MESSAGE_TYPE);
        message.put("@id", this.id);
        message.put("connectionId", this.connectionId);
        JSONObject proofRequest = new JSONObject();
        proofRequest.put("name", this.name);
        proofRequest.put("proofAttrs", proofAttrs);
        if (this.revocationInterval != null) {
            proofRequest.put("revocationInterval", this.revocationInterval);
        }
        message.put("proofRequest", proofRequest);
        return message.toString();
    }

    /**
     * Sends the proof request message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public void send(Context context) throws IOException, InterruptedException, ExecutionException, IndyException {
        this.sendMessage(context);
    }
}
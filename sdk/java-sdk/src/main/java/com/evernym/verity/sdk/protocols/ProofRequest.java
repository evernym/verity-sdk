package com.evernym.verity.sdk.protocols;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.utils.VerityConfig;

import org.hyperledger.indy.sdk.IndyException;
import org.json.JSONArray;
import org.json.JSONObject;

public class ProofRequest extends Protocol {

    // Message Type Definitions
    public static String PROOF_REQUEST_MESSAGE_TYPE = "vs.service/proof/0.1/request";
    public static String PROBLEM_REPORT_MESSAGE_TYPE = "vs.service/proof/0.1/problem-report";
    public static String STATUS_MESSAGE_TYPE = "vs.service/proof/0.1/status";

    // Status Definitions
    public static Integer PROOF_REQUEST_SENT_STATUS = 0;
    public static Integer PROOF_RECEIVED_STATUS = 1;

    private String name;
    private JSONArray proofAttrs;
    private String connectionId;

    public ProofRequest(String name, JSONArray proofAttrs, String connectionId) {
        this.name = name;
        this.proofAttrs = proofAttrs;
        this.connectionId = connectionId;
    }

    @Override
    public String toString() {
        JSONObject message = new JSONObject();
        message.put("@type", ProofRequest.PROOF_REQUEST_MESSAGE_TYPE);
        message.put("@id", this.id);
        message.put("connectionId", this.connectionId);
        JSONObject proof = new JSONObject();
        proof.put("name", this.name);
        proof.put("proofAttrs", proofAttrs);
        message.put("proof", proof);
        return message.toString();
    }

    /**
     * Sends the proof request message to Verity
     * @param verityConfig an instance of VerityConfig configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public void send(VerityConfig verityConfig) throws IOException, InterruptedException, ExecutionException, IndyException {
        this.sendMessage(verityConfig);
    }
}
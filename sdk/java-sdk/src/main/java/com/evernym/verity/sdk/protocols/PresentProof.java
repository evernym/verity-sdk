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
public class PresentProof extends Protocol {

    private static String MSG_FAMILY = "present-proof";
    private static String MSG_FAMILY_VERSION = "0.1";

    // Messages
    @SuppressWarnings("WeakerAccess")
    public static String PROOF_REQUEST = "request";

    // Status Definitions
    public static Integer PROOF_REQUEST_SENT_STATUS = 0;
    public static Integer PROOF_RECEIVED_STATUS = 1;

    String connectionId;
    String name;
    JSONArray proofAttrs;
    JSONObject revocationInterval;

    /**
     * Initializes the proof request object
     * @param connectionId The connectionId the proof request will be sent to
     * @param name The name of the proof request
     * @param proofAttrs The requested attributes of the proof request
     */
    public PresentProof(String connectionId, String name, JSONArray proofAttrs) {
        this(connectionId, name, proofAttrs, null);
    }

    /**
     * Initializes the proof request object with a revocation interval
     * @param connectionId The connectionId the proof request will be sent to
     * @param name The name of the proof request
     * @param proofAttrs The requested attributes of the proof request
     * @param revocationInterval The proof request revocation interval
     */
    @SuppressWarnings("WeakerAccess")
    public PresentProof(String connectionId, String name, JSONArray proofAttrs, JSONObject revocationInterval) {
        super(MSG_FAMILY, MSG_FAMILY_VERSION);
        this.connectionId = connectionId;
        this.name = name;
        this.proofAttrs = proofAttrs;
        this.revocationInterval = revocationInterval;
        defineMessages();
    }

    @Override
    protected void defineMessages() {
        JSONObject message = new JSONObject();
        message.put("@type", this.getMessageType(PresentProof.PROOF_REQUEST));
        message.put("@id", PresentProof.getNewId());
        message.put("connectionId", this.connectionId);
            JSONObject proofRequest = new JSONObject();
            proofRequest.put("name", this.name);
            proofRequest.put("proofAttrs", proofAttrs);
            if (this.revocationInterval != null) {
                proofRequest.put("revocationInterval", this.revocationInterval);
            }
            message.put("proofRequest", proofRequest);
        this.messages.put(PresentProof.PROOF_REQUEST, message);
    }

    /**
     * Sends the proof request message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public byte[] request(Context context) throws IOException, InterruptedException, ExecutionException, IndyException {
        return this.send(context, this.messages.getJSONObject(PresentProof.PROOF_REQUEST));
    }
}
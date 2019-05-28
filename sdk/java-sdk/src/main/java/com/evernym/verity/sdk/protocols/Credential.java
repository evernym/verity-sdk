package com.evernym.verity.sdk.protocols;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.utils.VerityConfig;

import org.hyperledger.indy.sdk.IndyException;
import org.json.JSONObject;

public class Credential extends Protocol {

    // Message type definitions
    public static String CREDENTIAL_OFFER_MESSAGE_TYPE = "vs.service/credential/0.1/offer";
    public static String CREDENTIAL_MESSAGE_TYPE = "vs.service/credential/0.1/credential";
    public static String PROBLEM_REPORT_MESSAGE_TYPE = "vs.service/credential/0.1/problem-report";
    public static String STATUS_MESSAGE_TYPE = "vs.service/credential/0.1/status";

    // Status definitions
    public static Integer OFFER_SENT_STATUS = 0;
    public static Integer OFFER_ACCEPTED_BY_USER_STATUS = 1;
    public static Integer CREDENTIAL_SENT_TO_USER_STATUS = 2;
    public static Integer CREDENTIAL_ACCEPTED_BY_USER_STATUS = 3;

    String offerMessageId;
    String credentialMessageId;
    String credentialDataId;
    String connectionId;
    String credDefId;
    JSONObject credentialValues;
    Integer price;

    /**
     * Creates a new credential
     * @param connectionId The pairwise DID of the connection you would like to send the Credential to.
     * @param credDefId The credDefId of the credential definition being used
     * @param credentialValues key-value pairs of credential attribute fields with the specified params defined in the credential definition
     * @param price The cost of the credential for the user.
     */
    public Credential(String connectionId, String credDefId, JSONObject credentialValues, Integer price) {
        this.offerMessageId = UUID.randomUUID().toString();
        this.credentialMessageId = UUID.randomUUID().toString();
        this.credentialDataId = UUID.randomUUID().toString();
        this.id = credentialMessageId;
        
        this.connectionId = connectionId;
        this.credDefId = credDefId;
        this.credentialValues = credentialValues;
        this.price = price;
    }

    /**
     * Builds the JSON structure of the offer message
     * @return the offer message string
     */
    public String offerMessageToString() {
        JSONObject message = new JSONObject();
        message.put("@type", Credential.CREDENTIAL_OFFER_MESSAGE_TYPE);
        message.put("@id", this.offerMessageId);
        message.put("connectionId", this.connectionId);
        message.put("credDefId", this.credDefId);
        return message.toString();
    }

    /**
     * Encrypts the offer message for Verity
     * @param verityConfig an instance of VerityConfig configured with the results of the provision_sdk.py script
     * @return The encrypted message, ready to send to Verity
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public byte[] getOfferMessage(VerityConfig verityConfig) throws InterruptedException, ExecutionException, IndyException {
        return this.getMessage(verityConfig, offerMessageToString());
    }

    /**
     * Sends the offer message to the connection
     * @param verityConfig an instance of VerityConfig configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public void sendOffer(VerityConfig verityConfig) throws IOException, InterruptedException, ExecutionException, IndyException {
        this.sendMessage(verityConfig, offerMessageToString());
    }

    /**
     * Builds the JSON structure of the credential message
     * @return the credential message string
     */
    public String credentialMessageToString() {
        JSONObject message = new JSONObject();
        message.put("@type", Credential.CREDENTIAL_MESSAGE_TYPE);
        message.put("@id", this.credentialMessageId);
        JSONObject thread = new JSONObject();
        thread.put("pthid", this.offerMessageId);
        message.put("~thread", thread);
        message.put("connectionId", this.connectionId);
        JSONObject credentialData = new JSONObject();
        credentialData.put("id", credentialDataId);
        credentialData.put("credDefId", this.credDefId);
        credentialData.put("credentialValues", this.credentialValues);
        credentialData.put("price", this.price);
        message.put("credentialData", credentialData);
        return message.toString();
    }

    /**
     * Encrypts the credential message for Verity
     * @param verityConfig an instance of VerityConfig configured with the results of the provision_sdk.py script
     * @return the encrypted message, ready to send to Verity
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public byte[] getCredentialMessage(VerityConfig verityConfig) throws InterruptedException, ExecutionException, IndyException {
        return this.getMessage(verityConfig, credentialMessageToString());
    }

    /**
     * Sends the credential message to the connection
     * @param verityConfig an instance of VerityConfig configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public void sendCredential(VerityConfig verityConfig) throws IOException, InterruptedException, ExecutionException, IndyException {
        this.sendMessage(verityConfig, credentialMessageToString());
    }

    @Override
    public String toString() {
        return credentialMessageToString();
    }

    @Override
    public byte[] getMessage(VerityConfig verityConfig) throws InterruptedException, ExecutionException, IndyException, MethodNotSupportedException {
        // Disable default getMessage function from Protocol class.
        throw new MethodNotSupportedException();
    }

    @Override
    public void sendMessage(VerityConfig verityConfig) throws IOException, InterruptedException, ExecutionException, IndyException, MethodNotSupportedException {
        // Disable default sendMessage function from Protocol class.
        throw new MethodNotSupportedException();
    }
}
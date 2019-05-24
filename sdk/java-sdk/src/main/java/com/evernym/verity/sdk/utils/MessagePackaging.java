package com.evernym.verity.sdk.utils;

import java.util.concurrent.ExecutionException;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.crypto.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class MessagePackaging {

    /**
     * Encrypts a message for the Evernym verity. This function should not be called directly because it is called by the individual protocol classes.
     * @param verityConfig an instance of VerityConfig configured with the results of the provision_sdk.py script
     * @param message the message being sent
     * @return Encrypted message ready to be sent to the verity
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public static byte[] packMessageForVerity(VerityConfig verityConfig, String message) throws InterruptedException, ExecutionException, IndyException {
        String pairwiseReceiver = new JSONArray(new String[]{verityConfig.getVerityPairwiseVerkey()}).toString();
        String verityReceiver = new JSONArray(new String[]{verityConfig.getVerityPublicVerkey()}).toString();
        byte[] agentMessage = Crypto.packMessage(verityConfig.getWalletHandle(), pairwiseReceiver, verityConfig.getSdkPairwiseVerkey(), message.getBytes()).get();
        byte[] verityMessage = Crypto.packMessage(verityConfig.getWalletHandle(), verityReceiver, null, agentMessage).get();
        return verityMessage;
    }

    /**
     * Unpacks a message received from the Evernym verity
     * @param verityConfig an instance of VerityConfig configured with the results of the provision_sdk.py script
     * @param message the message received from the Evernym verity
     * @return an unencrypted String message
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public static JSONObject unpackMessageFromVerity(VerityConfig verityConfig, byte[] message) throws InterruptedException, ExecutionException, IndyException {
        byte[] jwe = Crypto.unpackMessage(verityConfig.getWalletHandle(), message).get();
        return new JSONObject(new JSONObject(new String(jwe)).getString("message"));
    }
}

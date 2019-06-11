package com.evernym.verity.sdk.utils;

import java.util.concurrent.ExecutionException;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.crypto.Crypto;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Static helper functions used for packaging and unpackaging messages
 */
public class MessagePackaging {

    /**
     * Encrypts a message for the Evernym verity. This function should not be called directly because it is called by the individual protocol classes.
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @param message the message being sent
     * @return Encrypted message ready to be sent to the verity
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public static byte[] packMessageForVerity(Context context, String message) throws InterruptedException, ExecutionException, IndyException {
        String pairwiseReceiver = new JSONArray(new String[]{context.getVerityPairwiseVerkey()}).toString();
        String verityReceiver = new JSONArray(new String[]{context.getVerityPublicVerkey()}).toString();
        byte[] agentMessage = Crypto.packMessage(context.getWalletHandle(), pairwiseReceiver, context.getSdkPairwiseVerkey(), message.getBytes()).get();
        String innerFwd = prepareFwdMessage(context.getVerityPairwiseDID(),agentMessage);
        byte[] verityMessage = Crypto.packMessage(context.getWalletHandle(), verityReceiver, null, innerFwd.getBytes()).get();
        return verityMessage;
    }

    /**
     * Builds a forward message
     * @param DID the DID the message is being forwarded to
     * @param message the raw bytes of the message being forwarded
     */
    public static String prepareFwdMessage(String DID, byte[] message) {
        JSONObject fwdMessage = new JSONObject();
        fwdMessage.put("@type", "did:sov:123456789abcdefghi1234;spec/routing/0.6/FWD");
        fwdMessage.put("@fwd", DID);
        fwdMessage.put("@msg", new JSONObject(new String(message)));
        return fwdMessage.toString();
    }

    /**
     * Unpacks a message received from the Evernym verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @param message the message received from the Evernym verity
     * @return an unencrypted String message
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public static JSONObject unpackMessageFromVerity(Context context, byte[] message) throws InterruptedException, ExecutionException, IndyException {
        byte[] jwe = Crypto.unpackMessage(context.getWalletHandle(), message).get();
        return new JSONObject(new JSONObject(new String(jwe)).getString("message"));
    }

    /**
     * Helper function for converting a JSONArray of bytes to byte[]
     * @param array JSONArray of integers
     * @return Java byte array
     */
    public static byte[] objectToByteArray( JSONArray array ) {
        byte[] myArray = new byte[array.length()];
        for (int i = 0; i < array.length(); i++) {
            myArray[i] = (byte) array.getInt(i);
        }
        return myArray;
    }

    /**
     * Unpack message forwarded message
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @param message the message received from the Evernym verity
     * @return an unencrypted String message
     * @throws InterruptedException when there are issues with encryption and decryption
     * @throws ExecutionException when there are issues with encryption and decryption
     * @throws IndyException when there are issues with encryption and decryption
     */
    public static JSONObject unpackForwardMsg(Context context, JSONObject message) throws InterruptedException, ExecutionException, IndyException {
        byte[] jwe = Crypto.unpackMessage(context.getWalletHandle(), message.toString().getBytes()).get();
        return new JSONObject(new JSONObject(new String(jwe)).getString("message"));
    }
}

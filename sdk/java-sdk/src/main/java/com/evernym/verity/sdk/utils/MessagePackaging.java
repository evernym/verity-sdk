package com.evernym.verity.sdk.utils;

import java.util.concurrent.ExecutionException;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.crypto.Crypto;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Static helper functions used for packaging and unpackaging messages
 */
public class MessagePackaging {

    public static byte[] packMessageForVerity(Wallet walletHandle,
                                              String message,
                                              String pairwiseRemoteDID,
                                              String pairwiseRemoteVerkey,
                                              String pairwiseLocalVerkey,
                                              String publicVerkey
    ) throws InterruptedException, ExecutionException, IndyException {

        String pairwiseReceiver = new JSONArray(new String[]{pairwiseRemoteVerkey}).toString();
        String verityReceiver = new JSONArray(new String[]{publicVerkey}).toString();
        byte[] agentMessage = Crypto.packMessage(walletHandle, pairwiseReceiver, pairwiseLocalVerkey, message.getBytes()).get();
        String innerFwd = prepareFwdMessage(pairwiseRemoteDID, agentMessage);
        byte[] verityMessage = Crypto.packMessage(walletHandle, verityReceiver, null, innerFwd.getBytes()).get();
        return verityMessage;
    }

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
        return packMessageForVerity(
                context.walletHandle,
                message,
                context.getVerityPairwiseDID(),
                context.getVerityPairwiseVerkey(),
                context.getSdkPairwiseVerkey(),
                context.getVerityPublicVerkey()
        );
    }

    /**
     * Builds a forward message
     * @param DID the DID the message is being forwarded to
     * @param message the raw bytes of the message being forwarded
     */
    public static String prepareFwdMessage(String DID, byte[] message) {
        JSONObject fwdMessage = new JSONObject();
        fwdMessage.put("@type", "did:sov:123456789abcdefghi1234;spec/routing/1.0/FWD");
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

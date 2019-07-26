package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.crypto.Crypto;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

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
    ) throws WalletException {
        try {
            String pairwiseReceiver = new JSONArray(new String[]{pairwiseRemoteVerkey}).toString();
            String verityReceiver = new JSONArray(new String[]{publicVerkey}).toString();

            byte[] agentMessage = Crypto.packMessage(walletHandle, pairwiseReceiver, pairwiseLocalVerkey, message.getBytes()).get();

            String innerFwd = prepareFwdMessage(pairwiseRemoteDID, agentMessage);

            return Crypto.packMessage(walletHandle, verityReceiver, null, innerFwd.getBytes()).get();
        }
        catch (IndyException | InterruptedException | ExecutionException e) {
            throw new WalletException("Unable to pack messages", e);
        }
    }

    /**
     * Encrypts a message for the Evernym verity. This function should not be called directly because it is called by the individual protocol classes.
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @param message the message being sent
     * @return Encrypted message ready to be sent to the verity
     * @throws WalletException when there are issues with encryption and decryption
     * @throws UndefinedContextException when the context don't have enough information for this operation
     */
    public static byte[] packMessageForVerity(Context context, String message) throws UndefinedContextException, WalletException {
        Wallet handle = context.walletHandle();
        return packMessageForVerity(
                handle,
                message,
                context.verityPairwiseDID(),
                context.verityPairwiseVerkey(),
                context.sdkPairwiseVerkey(),
                context.verityPublicVerkey()
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
     * @throws WalletException when there are issues with encryption and decryption
     */
    public static JSONObject unpackMessageFromVerity(Context context, byte[] message) throws WalletException {
        try {
            byte[] jwe = Crypto.unpackMessage(context.walletHandle(), message).get();
            return new JSONObject(new JSONObject(new String(jwe)).getString("message"));
        }
        catch (IndyException | InterruptedException | ExecutionException e) {
            throw new WalletException("Unable to unpack message", e);
        }
    }

    /**
     * Unpack message forwarded message
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @param message the message received from the Evernym verity
     * @return an unencrypted String message
     * @throws WalletException when there are issues with encryption and decryption
     */
    public static JSONObject unpackForwardMsg(Context context, JSONObject message) throws WalletException {
        try {
            byte[] jwe = Crypto.unpackMessage(context.walletHandle(), message.toString().getBytes()).get();
            return new JSONObject(new JSONObject(new String(jwe)).getString("message"));
        }
        catch (IndyException | InterruptedException | ExecutionException e) {
            throw new WalletException("Unable to unpack message", e);
        }
    }
}

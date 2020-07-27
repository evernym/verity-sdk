package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.crypto.Crypto;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Static Utilities helper functions used for verity-sdk
 */
public class Util {
    /**
     * QUALIFIER for evernym specific protocols
     */
    public static final String EVERNYM_MSG_QUALIFIER = "did:sov:123456789abcdefghi1234";

    /**
     * QUALIFIER for community specified protocol
     */
    public static final String COMMUNITY_MSG_QUALIFIER = "did:sov:BzCbsNYhMrjHiqZDTUASHg";

    /**
     * Packages message (instructor and encryption) for the verity-application. Uses local private keys and remote
     * public keys for encryption. The encryption and instructor is defined by the Aries community.
     *
     * @param walletHandle the handle to a created and open Indy wallet
     * @param message the JSON message to be communicated to the verity-application
     * @param domainDID the domain DID of the intended recipient agent on the verity-application
     * @param remoteVerkey the verkey for the agent on the verity-application
     * @param localVerkey the authorized verkey in the local wallet for the verity-sdk application
     * @param publicVerkey the public verkey for the verity-application
     * @return the byte array of the packaged and encrypted message
     * @throws WalletException when wallet operations fails
     *
     * @see <a href="https://github.com/hyperledger/aries-rfcs/tree/9b0aaa39df7e8bd434126c4b33c097aae78d65bf/features/0019-encryption-envelope" target="_blank" rel="noopener noreferrer">Aries 0019: Encryption Envelope</a>
     */
    public static byte[] packMessageForVerity(Wallet walletHandle,
                                              JSONObject message,
                                              String domainDID,
                                              String remoteVerkey,
                                              String localVerkey,
                                              String publicVerkey
    ) throws WalletException {
        try {
            String pairwiseReceiver = new JSONArray(new String[]{remoteVerkey}).toString();
            String verityReceiver = new JSONArray(new String[]{publicVerkey}).toString();

            byte[] agentMessage = Crypto.packMessage(
                    walletHandle,
                    pairwiseReceiver,
                    localVerkey,
                    message.toString().getBytes()
            ).get();

            String innerFwd = prepareForwardMessage(
                    domainDID,
                    agentMessage
            );

            return Crypto.packMessage(
                    walletHandle,
                    verityReceiver,
                    null,
                    innerFwd.getBytes()
            ).get();
        } catch (IndyException | InterruptedException | ExecutionException e) {
            throw new WalletException("Unable to pack messages", e);
        }
    }

    /**
     * Packages message (instructor and encryption) for the verity-application. Uses local private keys and remote
     * public keys for encryption. The encryption and instructor is defined by the Aries community.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param message the JSON message to be communicated to the verity-application
     * @return the byte array of the packaged and encrypted message
     * @throws WalletException when wallet operations fails (including encryption)
     * @throws UndefinedContextException when the context is missing required fields (domainDID, verityAgentVerKey, sdkVerKey, verityPublicVerKey)
     *
     * @see <a href="https://github.com/hyperledger/aries-rfcs/tree/9b0aaa39df7e8bd434126c4b33c097aae78d65bf/features/0019-encryption-envelope" target="_blank" rel="noopener noreferrer">Aries 0019: Encryption Envelope</a>
     */
    public static byte[] packMessageForVerity(Context context, JSONObject message) throws UndefinedContextException, WalletException {
        if(context == null) {
            throw new UndefinedContextException("Context cannot be NULL");
        }

        Wallet handle = context.walletHandle();
        return packMessageForVerity(
                handle,
                message,
                context.domainDID(),
                context.verityAgentVerKey(),
                context.sdkVerKey(),
                context.verityPublicVerKey()
        );
    }

    /**
     * Prepares (pre-encryption) a forward message to a given DID that contains the given byte array message
     * @param DID the DID the forward message is intended for
     * @param message the packaged and encrypted message that is being forwarded
     * @return the prepared JSON forward structure
     */
    private static String prepareForwardMessage(String DID, byte[] message) {
        JSONObject fwdMessage = new JSONObject();
        fwdMessage.put("@type", "did:sov:123456789abcdefghi1234;spec/routing/1.0/FWD");
        fwdMessage.put("@fwd", DID);
        fwdMessage.put("@msg", new JSONObject(new String(message)));
        return fwdMessage.toString();
    }

    /**
     * Extracts the message in the byte array that has been packaged and encrypted for a key that is locally held.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param message the raw message received from the verity-application agent
     * @return an unencrypted messages as a JSON object
     * @throws WalletException when wallet operations fails (including decryption)
     */
    public static JSONObject unpackMessage(Context context, byte[] message) throws WalletException {
        try {
            byte[] jwe = Crypto.unpackMessage(context.walletHandle(), message).get();
            return new JSONObject(new JSONObject(new String(jwe)).getString("message"));
        }
        catch (IndyException | InterruptedException | ExecutionException e) {
            throw new WalletException("Unable to unpack message", e);
        }
    }


    /**
     * Combines elements the given of a message family static values with the given message name to produce a fully
     * qualified message type
     * @param f the given message family
     * @param msgName the given message name
     * @return a fully qualified message type
     */
    public static String getMessageType(MessageFamily f, String msgName) {
        return getMessageType(f.qualifier(), f.family(), f.version(), msgName);
    }

    /**
     * Combines the given elements to produce a fully qualified message type
     * @param msgQualifier the given qualifier
     * @param msgFamily the given family name
     * @param msgFamilyVersion the given version
     * @param msgName the given message name
     * @return a fully qualified message type
     */
    public static String getMessageType(String msgQualifier, String msgFamily, String msgFamilyVersion, String msgName) {
        return msgQualifier + ";spec/" + msgFamily + "/" + msgFamilyVersion + "/" + msgName;
    }

    @Deprecated
    public static String getProblemReportMessageType(String msgQualifier, String msgFamily, String msgFamilyVersion) {
        return Util.getMessageType(msgQualifier, msgFamily, msgFamilyVersion, "problem-report");
    }

    @Deprecated
    public static String getStatusMessageType(String msgQualifier, String msgFamily, String msgFamilyVersion) {
        return Util.getMessageType(msgQualifier, msgFamily, msgFamilyVersion, "status");
    }
}

package com.evernym.verity.sdk.utils;

import java.util.concurrent.ExecutionException;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.crypto.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class MessagePackaging {

    /**
     * Encrypts a message for the Evernym agency. This function should not be called directly because it is called by the individual protocol classes.
     * @param walletContents an instance of WalletContents that has been initialized with your wallet details
     * @param message the message being sent
     * @return Encrypted message ready to be sent to the agency
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IndyException
     */
    public static byte[] packMessageForAgency(VerityConfig verityConfig, String message) throws InterruptedException, ExecutionException, IndyException {
        String pairwiseReceiver = new JSONArray(new String[]{verityConfig.getAgencyPairwiseVerkey()}).toString();
        String agencyReceiver = new JSONArray(new String[]{verityConfig.getAgencyPublicVerkey()}).toString();
        byte[] agentMessage = Crypto.packMessage(verityConfig.getWalletHandle(), pairwiseReceiver, verityConfig.getSdkPairwiseVerkey(), message.getBytes()).get();
        byte[] agencyMessage = Crypto.packMessage(verityConfig.getWalletHandle(), agencyReceiver, null, agentMessage).get();
        return agencyMessage;
    }

    /**
     * Unpacks a message received from the Evernym agency
     * @param walletContents an instance of WalletContents that has been initialized with your wallet details
     * @param message the message received from the Evernym agency
     * @return an unencrypted String message
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IndyException
     */
    public static String unpackMessageFromAgency(VerityConfig verityConfig, byte[] message) throws InterruptedException, ExecutionException, IndyException {
        byte[] jwe = Crypto.unpackMessage(verityConfig.getWalletHandle(), message).get();
        return new JSONObject(new String(jwe)).getString("message");
    }
}

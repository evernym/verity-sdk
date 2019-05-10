package com.evernym.verity.sdk.utils;

import java.util.concurrent.ExecutionException;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.crypto.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class MessagePackaging {

    public static byte[] packMessageForAgency(WalletContents walletContents, String message) throws InterruptedException, ExecutionException, IndyException {
        String pairwiseReceiver = new JSONArray(new String[]{walletContents.getAgencyPairwiseVerkey()}).toString();
        String agencyReceiver = new JSONArray(new String[]{walletContents.getAgencyVerkey()}).toString();
        byte[] agentMessage = Crypto.packMessage(walletContents.getWalletHandle(), pairwiseReceiver, walletContents.getMyPairwiseVerkey(), message.getBytes()).get();
        byte[] agencyMessage = Crypto.packMessage(walletContents.getWalletHandle(), agencyReceiver, null, agentMessage).get();
        return agencyMessage;
    }

    public static String unpackMessageFromAgency(WalletContents walletContents, byte[] message) throws InterruptedException, ExecutionException, IndyException {
        byte[] jwe = Crypto.unpackMessage(walletContents.getWalletHandle(), message).get();
        return new JSONObject(new String(jwe)).toString();
    }
    
}

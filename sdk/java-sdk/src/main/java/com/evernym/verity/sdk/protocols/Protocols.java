package com.evernym.verity.sdk.protocols;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.utils.MessagePackaging;
import com.evernym.verity.sdk.utils.VerityConfig;

import org.hyperledger.indy.sdk.IndyException;
import org.json.JSONObject;

public class Protocols {
    static ArrayList<MessageHandler> messageHandlers = new ArrayList<MessageHandler>();

    public static void addHandler(String messageType, MessageHandler.Handler messageHandler) {
        messageHandlers.add(new MessageHandler(messageType, messageHandler));
    }

    public static void addHandler(String messageType, Integer messageStatus, MessageHandler.Handler messageHandler) {
        messageHandlers.add(new MessageHandler(messageType, messageStatus, messageHandler));
    }

    public static void handleMessage(VerityConfig verityConfig, byte[] rawMessage) throws InterruptedException, ExecutionException, IndyException {
        JSONObject message = MessagePackaging.unpackMessageFromVerity(verityConfig, rawMessage);
        System.out.println("New message from verity: " + message.toString());
        for(MessageHandler messageHandler: messageHandlers) {
            if(messageHandler.handles(message)) {
                messageHandler.handle(message);
            }
        }
    }
    
}
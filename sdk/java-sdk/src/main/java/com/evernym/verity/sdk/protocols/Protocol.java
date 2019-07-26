package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.transports.HTTPTransport;
import com.evernym.verity.sdk.transports.Transport;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.MessagePackaging;

import java.io.IOException;
import java.util.UUID;

/**
 * The base class for all protocols
 */
public abstract class Protocol {
    protected String id;

    public Protocol() {
        this.id = UUID.randomUUID().toString();
    }
    
    /**
     * Packs the connection message for the verity
     * @param context an instance of Context that has been initialized with your wallet and key details
     * @return Encrypted connection message ready to be sent to the verity
     * @throws WalletException when there are issues with encryption and decryption
     * @throws UndefinedContextException when the context don't have enough information for this operation
     */
    public byte[] getMessage(Context context) throws WalletException, UndefinedContextException {
        return MessagePackaging.packMessageForVerity(context, toString());
    }

    /**
     * Encrypts and sends the default message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws WalletException when there are issues with encryption and decryption
     * @throws UndefinedContextException when the context don't have enough information for this operation
     */
    public void sendMessage(Context context) throws IOException, UndefinedContextException, WalletException {
        // Later we can switch on context.getVerityProtocol
        Transport transport = new HTTPTransport();
        transport.sendMessage(context.verityUrl(), getMessage(context));
    }

    /**
     * Encrypts and sends a specified message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @param message the message to send to Verity
     * @throws IOException when the HTTP library fails to post to the agency endpoint
     * @throws WalletException when there are issues with encryption and decryption
     * @throws UndefinedContextException when the context don't have enough information for this operation
     */
    public void sendMessage(Context context, String message) throws IOException, UndefinedContextException, WalletException {
        // Later we can switch on context.getVerityProtocol
        Transport transport = new HTTPTransport();
        transport.sendMessage(context.verityUrl(), MessagePackaging.packMessageForVerity(context, message));
    }

    public abstract String toString();
}
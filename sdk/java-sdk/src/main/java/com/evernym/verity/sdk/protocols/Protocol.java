package com.evernym.verity.sdk.protocols;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.transports.HTTPTransport;
import com.evernym.verity.sdk.transports.Transport;
import com.evernym.verity.sdk.utils.*;

import org.hyperledger.indy.sdk.IndyException;

public abstract class Protocol {
    protected String id;

    public Protocol() {
        this.id = UUID.randomUUID().toString();
    }
    
    /**
     * Packs the connection message for the agency
     * @param verityConfig an instance of VerityConfig that has been initialized with your wallet and key details
     * @return Encrypted connection message ready to be sent to the agency
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IndyException
     */
    public byte[] getMessage(VerityConfig verityConfig) throws InterruptedException, ExecutionException, IndyException {
        return MessagePackaging.packMessageForAgency(verityConfig, toString());
    }

    public void sendMessage(VerityConfig verityConfig) throws IOException, InterruptedException, ExecutionException, IndyException {
        // TODO: Switch on verityConfig.getAgencyProtocol
        Transport transport = new HTTPTransport();
        transport.sendMessage(verityConfig.getAgencyUrl(), getMessage(verityConfig));
    }

    public abstract String toString();
}
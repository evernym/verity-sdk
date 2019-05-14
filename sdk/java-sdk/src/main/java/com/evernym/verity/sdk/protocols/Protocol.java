package com.evernym.verity.sdk.protocols;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.evernym.verity.sdk.utils.*;

import org.hyperledger.indy.sdk.IndyException;

public abstract class Protocol {
    protected String type;
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

    public abstract String toString();
}
package com.evernym.verity.sdk.protocols;

public interface MessageFamily {
    String qualifier();
    String family();
    String version();

    default String getMessageType(String msgName) {
        return qualifier() + ";spec/" + family() + "/" + version() + "/" + msgName;
    }
}

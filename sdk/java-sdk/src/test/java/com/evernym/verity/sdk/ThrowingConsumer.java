package com.evernym.verity.sdk;

import com.evernym.verity.sdk.exceptions.VerityException;

public interface ThrowingConsumer<T> {
    void accept(T t) throws RuntimeException, VerityException;
}

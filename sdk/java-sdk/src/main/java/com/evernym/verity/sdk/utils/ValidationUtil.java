package com.evernym.verity.sdk.utils;

import java.util.ArrayList;
import java.util.Objects;

public class ValidationUtil {

    private ValidationUtil() {}

    public static void checkAtLeastOneOptionalFieldExists(ArrayList allOptionalInputs) {
        boolean result = allOptionalInputs.stream().allMatch(Objects::isNull);
        if (result) {
            throw new IllegalArgumentException("at least one of the optional input parameters must be supplied");
        }
    }

    public static <E> void checkRequiredField(E field, String fieldName) {
        if (field == null) {
            throw new IllegalArgumentException(String.format("'%s' field is required", fieldName));
        }
    }
}

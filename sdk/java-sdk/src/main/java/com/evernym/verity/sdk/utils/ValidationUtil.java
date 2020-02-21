package com.evernym.verity.sdk.utils;

import java.util.List;
import java.util.Objects;

public class ValidationUtil {

    private ValidationUtil() {}

    public static <T> void checkAtLeastOneOptionalFieldExists(List<T> list) {
        boolean result = list.stream().allMatch(Objects::isNull);
        if (result) {
            throw new IllegalArgumentException("at least one of the optional input parameters must be supplied");
        }
    }

    public static <T> void checkOnlyOneOptionalFieldExists(List<T> list) {
        long result = list.stream().filter(Objects::nonNull).count();
        if (result != 1) {
            throw new IllegalArgumentException("only one of the optional input parameters must be supplied");
        }
    }


    public static <E> void checkRequiredField(E field, String fieldName) {
        if (field == null) {
            throw new IllegalArgumentException(String.format("'%s' field is required", fieldName));
        }
    }
}

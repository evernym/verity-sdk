package com.evernym.verity.sdk.utils;

public class DbcUtil {
    protected DbcUtil() {}

    public static void requireNotNull(Object arg) {
        requireNotNull(arg, "ARG");
    }

    public static void requireNotNull(Object arg, String argName) {
        require(arg != null, "required that "+ argName +" must NOT be null");
    }

    public static void require(Boolean requirement) {
        require(requirement, "");
    }

    public static void require(Boolean requirement, String msg) {
        if(!requirement) {
            throw new IllegalArgumentException("requirement failed: "+ msg);
        }
    }
}

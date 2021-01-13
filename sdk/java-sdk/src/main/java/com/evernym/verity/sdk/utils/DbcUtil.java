package com.evernym.verity.sdk.utils;

/**
 * Utilities for design by contract. Allows for common pre-condition and post-condition checking.
 */
public class DbcUtil {
    protected DbcUtil() {}

    /**
     * Checks and throws an IllegalArgumentException if the given object is null
     * @param arg any object that can be null
     */
    public static void requireNotNull(Object arg) {
        requireNotNull(arg, "ARG");
    }

    /**
     * Checks and throws an IllegalArgumentException if the given object is null
     * @param arg any object that can be null
     * @param argName name of the argument being checked, used in exception message if null
     */
    public static void requireNotNull(Object arg, String argName) {
        require(arg != null, "required that "+ argName +" must NOT be null");
    }

    /**
     * Checks that given boolean is true, throws an IllegalArgumentException if false
     * @param requirement testable requirement
     */
    public static void require(Boolean requirement) {
        require(requirement, "");
    }

    /**
     * Checks that given boolean is true, throws an IllegalArgumentException if false
     * @param requirement testable requirement
     * @param msg string that is used to build exception message
     */
    public static void require(Boolean requirement, String msg) {
        if(!requirement) {
            throw new IllegalArgumentException("requirement failed: "+ msg);
        }
    }

    /**
     * Checks and throws an IllegalArgumentException if the given string is null or empty
     * @param arg String argument which needs to be checked.
     * @param argName name of the argument being checked, used in exception message if null or empty
     */
    public static void requireStringNotNullOrEmpty(String arg, String argName) {
        require(arg != null && !arg.isEmpty(), "required that "+ argName +" must NOT be null or empty");
    }

    /**
     * Checks and throws an IllegalArgumentException if the given array is null or contains null element
     * @param array Array argument which needs to be checked.
     * @param argName name of the argument being checked, used in exception message
     */
    public static void requireArrayNotContainNull(Object[] array, String argName) {
        requireNotNull(array, argName);
        for (Object x: array)
            require(x != null, "required that elements of "+ argName +" must NOT be null");
    }
}

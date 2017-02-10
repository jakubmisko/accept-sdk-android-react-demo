package com.wirecard.accept.help;

/**
 * Helper class for avoiding of null checks
 */

public class Preconditions {
    public static void nullCheck(Object o, String msg) {
        if (o == null) {
            throw new NullPointerException(msg);
        }
    }
}

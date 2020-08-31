package com.gazman.quadratic_sieve.debug;

/**
 * A wrapper for a debug checks, so it can be easily removed with proguard
 */
public class AssertUtils {

    public static void assertTrue(String errorMessage, Tester tester) {
        if (!tester.test()) {
            throw new Error(errorMessage);
        }
    }

    public interface Tester {
        boolean test();
    }
}

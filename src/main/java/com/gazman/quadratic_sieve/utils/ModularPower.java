package com.gazman.quadratic_sieve.utils;

import java.math.BigInteger;

/**
 * Modular power.
 * @author Tilman Neumann
 */
public class ModularPower {
    /**
     * Computes a^b (mod c) for all-BigInteger arguments.</br></br>
     *
     * <em>BigIntegers implementation is much faster!</em>.
     *
     * @param a
     * @param b
     * @param c
     * @return a^b (mod c)
     */
    static BigInteger modPow(BigInteger a, BigInteger b, BigInteger c) {
        BigInteger modPow = BigInteger.ONE;
        while (true) {
            int i = b.compareTo(BigInteger.ZERO);
            if (!(i > 0)) break;
            if ((b.intValue() & 1) == 1) { // oddness test needs only the lowest bit
                modPow = modPow.multiply(a).mod(c);
            }
            a = a.multiply(a).mod(c);
            b = b.shiftRight(1);
        }
        return modPow;
    }

    /**
     * Computes a^b (mod c) for <code>a</code> BigInteger, <code>b, c</code> int. Very fast.
     * @return a^b (mod c)
     */
    public static int modPow(BigInteger a, int b, int c) {
        // products need long precision
        long modPow = 1;
        long aModC = a.mod(BigInteger.valueOf(c)).longValue();
        while (b > 0) {
            if ((b&1) == 1) modPow = (modPow * aModC) % c;
            aModC = (aModC * aModC) % c;
            b >>= 1;
        }
        return (int) modPow;
    }

    /**
     * Computes a^b (mod c) for all-int arguments. Very fast.
     * @return a^b (mod c)
     */
    public static int modPow(int a, int b, int c) {
        // products need long precision
        long modPow = 1;
        long aModC = a % c;
        while (b > 0) {
            if ((b&1) == 1) modPow = (modPow * aModC) % c;
            aModC = (aModC * aModC) % c;
            b >>= 1;
        }
        return (int) modPow;
    }
}

package com.gazman.quadratic_sieve.utils;

import java.math.BigInteger;

public class MathUtils {

    public static final BigInteger TOW = BigInteger.valueOf(2);

    /**
     * Based on Euler's criterion
     *
     * @param n the number to factor
     * @param p prime to test
     * @return true if p is a quadratic residue of x^2 % n
     */
    public static boolean isRootInQuadraticResidues(BigInteger n, BigInteger p) {
        BigInteger x = n.mod(p);
        if (p.equals(TOW)) {
            return x.mod(TOW).equals(BigInteger.ONE);
        }
        BigInteger exponent = p.subtract(BigInteger.ONE).divide(TOW);
        return x.modPow(exponent, p).equals(BigInteger.ONE);
    }


    /**
     * Took from Google Elliptic curve crypto
     *
     * @param x exponent
     * @param p prime
     * @return sqrt of x mod p
     * @see "https://www.programcreek.com/java-api-examples/?code=google%2Fwycheproof%2Fwycheproof-master%2Fjava%2Fcom%2Fgoogle%2Fsecurity%2Fwycheproof%2FEcUtil.java"
     */
    public static BigInteger modSqrt(BigInteger x, BigInteger p) {
//        if (p.signum() != 1) {
//            throw new GeneralSecurityException("p must be positive");
//        }
        x = x.mod(p);
        BigInteger squareRoot = null;
        // Special case for x == 0.
        // This check is necessary for Cipolla's algorithm.
        if (x.equals(BigInteger.ZERO)) {
            return x;
        }
        if (p.testBit(0) && p.testBit(1)) {
            // Case p % 4 == 3
            // q = (p + 1) / 4
            BigInteger q = p.add(BigInteger.ONE).shiftRight(2);
            squareRoot = x.modPow(q, p);
        } else if (p.testBit(0) && !p.testBit(1)) {
            // Case p % 4 == 1
            // For this case we use Cipolla's algorithm.
            // This algorithm is preferable to Tonelli-Shanks for primes p where p-1 is divisible by
            // a large power of 2, which is a frequent choice since it simplifies modular reduction.
            BigInteger a = BigInteger.ONE;
            BigInteger d;
            while (true) {
                d = a.multiply(a).subtract(x).mod(p);
                // Computes the Legendre symbol. Using the Jacobi symbol would be a faster. Using Legendre
                // has the advantage, that it detects a non prime p with high probability.
                // On the other hand if p = q^2 then the Jacobi (d/p)==1 for almost all d's and thus
                // using the Jacobi symbol here can result in an endless loop with invalid inputs.
                int t = legendre(d, p);
                if (t == -1) {
                    break;
                } else {
                    a = a.add(BigInteger.ONE);
                }
            }
            // Since d = a^2 - n is a non-residue modulo p, we have
            //   a - sqrt(d) == (a+sqrt(d))^p (mod p),
            // and hence
            //   n == (a + sqrt(d))(a - sqrt(d) == (a+sqrt(d))^(p+1) (mod p).
            // Thus if n is square then (a+sqrt(d))^((p+1)/2) (mod p) is a square root of n.
            BigInteger q = p.add(BigInteger.ONE).shiftRight(1);
            BigInteger u = a;
            BigInteger v = BigInteger.ONE;
            for (int bit = q.bitLength() - 2; bit >= 0; bit--) {
                // Compute (u + v sqrt(d))^2
                BigInteger tmp = u.multiply(v);
                u = u.multiply(u).add(v.multiply(v).mod(p).multiply(d)).mod(p);
                v = tmp.add(tmp).mod(p);
                if (q.testBit(bit)) {
                    tmp = u.multiply(a).add(v.multiply(d)).mod(p);
                    v = a.multiply(v).add(u).mod(p);
                    u = tmp;
                }
            }
            squareRoot = u;
        }
//        // The methods used to compute the square root only guarantee a correct result if the
//        // preconditions (i.e. p prime and x is a square) are satisfied. Otherwise the value is
//        // undefined. Hence, it is important to verify that squareRoot is indeed a square root.
//        if (squareRoot != null && squareRoot.multiply(squareRoot).mod(p).compareTo(x) != 0) {
//            throw new GeneralSecurityException("Could not find square root");
//        }
        return squareRoot;
    }

    /**
     * Compute the Legendre symbol of x mod p. This implementation is slow. Faster would be the
     * computation for the Jacobi symbol.
     *
     * @param n an integer
     * @param p a prime modulus
     * @return 1 if x is a quadratic residue, -1 if x is a non-quadratic residue and 0 if x and p are
     * not coprime.
     */
    public static int legendre(BigInteger n, BigInteger p) {
        BigInteger q = p.subtract(BigInteger.ONE).shiftRight(1);
        BigInteger t = n.modPow(q, p);
        if (t.equals(BigInteger.ONE)) {
            return 1;
        } else if (t.equals(BigInteger.ZERO)) {
            return 0;
        } else if (t.add(BigInteger.ONE).equals(p)) {
            return -1;
        }
//        else {
//            throw new GeneralSecurityException("p is not prime");
//        }
        throw new Error("p is not a prime");
    }
}

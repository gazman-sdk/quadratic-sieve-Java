package com.gazman.quadratic_sieve.core.poly;

import com.gazman.quadratic_sieve.data.DataQueue;
import com.gazman.quadratic_sieve.data.MagicNumbers;
import com.gazman.quadratic_sieve.data.PolynomialData;
import com.gazman.quadratic_sieve.data.PrimeBase;
import com.gazman.quadratic_sieve.debug.Analytics;
import com.gazman.quadratic_sieve.utils.MathUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for generating multiple polynomial of the form (ax+b)^2-N
 */
public class PolyMiner implements Runnable {
    private BigInteger N;
    private final List<BigInteger> primeModSquares = new ArrayList<>();

    public static final PolyMiner instance = new PolyMiner();
    private int delta = Integer.MIN_VALUE;

    public void start(BigInteger N) {
        this.N = N;
        new Thread(this, "PolyMiner").start();
    }

    @Override
    public void run() {
        BigInteger loopsCount = BigInteger.valueOf(MagicNumbers.instance.loopsCount);
        BigInteger m = BigInteger.valueOf(MagicNumbers.instance.loopsSize).multiply(loopsCount);
        BigInteger Q = N.multiply(BigInteger.TWO).sqrt().divide(m).sqrt();
        BigInteger v = BigInteger.ZERO;
        initModSquaresCache();
        while (true) {
            Analytics.POLY_MINER_TOTAL.start();
            BigInteger q;
            while (true) {
                v = v.add(BigInteger.ONE);
                q = Q.add(v);
                if (q.isProbablePrime(20) && MathUtils.isRootInQuadraticResidues(N, q)) {
                    break;
                }
                q = Q.subtract(v);
                if (q.isProbablePrime(20) && MathUtils.isRootInQuadraticResidues(N, q)) {
                    break;
                }
            }

            BigInteger aRoot;
            try {
                aRoot = MathUtils.modSqrt(N, q);
            } catch (Exception e) {
                // q wasn't a prime
                continue;
            }
            BigInteger k = N.subtract(aRoot.pow(2)).divide(q).multiply(BigInteger.TWO.multiply(aRoot).modInverse(q)).mod(q);
            BigInteger a = q.pow(2);
            BigInteger b = k.multiply(q).add(aRoot);
            BigInteger c = b.pow(2).subtract(N).divide(a);

            if (delta == Integer.MIN_VALUE) {
                delta = calculateDelta(a, b, c);
            }

            PolynomialData polynomialData = new PolynomialData(a, b, c, delta, N, primeModSquares);

            double baseLog = Math.log(polynomialData.getSievingValue(delta).abs().doubleValue());
            polynomialData.scale = 256 / baseLog / 4;

            Analytics.POLY_MINER_TOTAL.end();
            try {
                DataQueue.polynomialData.put(polynomialData);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void initModSquaresCache() {
        for (int p : PrimeBase.instance.primeBase) {
            if (p < MagicNumbers.instance.minPrimeSize || p == 2) {
                continue;
            }
            primeModSquares.add(MathUtils.modSqrt(N, BigInteger.valueOf(p)));
        }
    }

    private int calculateDelta(BigInteger a, BigInteger b, BigInteger c) {
        int loopSize = b.pow(2).subtract(a.multiply(c)).sqrt().subtract(b).divide(a).intValue();
        return loopSize - MagicNumbers.instance.loopsSize * MagicNumbers.instance.loopsCount / 2;
    }
}

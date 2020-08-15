package com.gazman.quadratic_sieve.core.poly;

import com.gazman.quadratic_sieve.data.DataQueue;
import com.gazman.quadratic_sieve.data.MagicNumbers;
import com.gazman.quadratic_sieve.data.PolynomialData;
import com.gazman.quadratic_sieve.data.PrimeBase;
import com.gazman.quadratic_sieve.logger.Logger;
import com.gazman.quadratic_sieve.utils.MathUtils;
import com.gazman.quadratic_sieve.wheel.Wheel;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for generating multiple polynomial of the form (ax+b)^2-N
 */
public class PolyMiner implements Runnable {
    private BigInteger N;

    public static final PolyMiner instance = new PolyMiner();

    public void start(BigInteger N) {
        this.N = N;
        new Thread(this, "PolyMiner").start();
    }

    @Override
    public void run() {
        BigInteger loopsCount = BigInteger.valueOf(MagicNumbers.instance.loopsCount);
        BigInteger m = MagicNumbers.instance.loopsSize.multiply(loopsCount);
        BigInteger q = N.multiply(BigInteger.TWO).sqrt().divide(m).sqrt();
        //noinspection InfiniteLoopStatement
        while (true) {
            Logger.POLY_MINER.start();
            do {
                q = q.nextProbablePrime();
            } while (!MathUtils.isRootInQuadraticResidues(N, q));

            BigInteger aRoot = MathUtils.modSqrt(N, q);
            BigInteger a = q.pow(2);
            BigInteger k = N.subtract(aRoot.pow(2)).divide(q).multiply(BigInteger.TWO.multiply(aRoot).modInverse(q)).mod(q);
            BigInteger b = k.multiply(q).add(aRoot);

            List<Wheel> wheels = buildWheels(a, b);

            Logger.POLY_MINER.end();
            try {
                DataQueue.polynomialData.put(new PolynomialData(a, b, N, wheels));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Wheel> buildWheels(BigInteger a, BigInteger b) {
        List<Wheel> wheels = new ArrayList<>();
        for (int p : PrimeBase.instance.primeBase) {
            if (p < MagicNumbers.instance.minPrimeSize) {
                continue;
            }
            BigInteger prime = BigInteger.valueOf(p);
            BigInteger root = MathUtils.modSqrt(N, prime);
            BigInteger p1 = root.subtract(b).multiply(a.modInverse(prime)).mod(prime);
            BigInteger p2 = prime.subtract(root).subtract(b).multiply(a.modInverse(prime)).mod(prime);
            wheels.add(WheelPool.instance.get(p, p1.intValue()));
            wheels.add(WheelPool.instance.get(p, p2.intValue()));
        }

        return wheels;
    }
}

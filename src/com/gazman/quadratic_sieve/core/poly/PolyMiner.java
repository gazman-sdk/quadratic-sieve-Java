package com.gazman.quadratic_sieve.core.poly;

import com.gazman.quadratic_sieve.data.DataQueue;
import com.gazman.quadratic_sieve.data.MagicNumbers;
import com.gazman.quadratic_sieve.data.PolynomialData;
import com.gazman.quadratic_sieve.data.PrimeBase;
import com.gazman.quadratic_sieve.logger.Analytics;
import com.gazman.quadratic_sieve.utils.MathUtils;
import com.gazman.quadratic_sieve.wheel.Wheel;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Responsible for generating multiple polynomial of the form (ax+b)^2-N
 */
public class PolyMiner implements Runnable {
    private BigInteger N;
    private final HashMap<BigInteger, BigInteger> map = new HashMap<>();

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
        while (true) {
            Analytics.POLY_MINER.start();
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
                return;
            }
            BigInteger k = N.subtract(aRoot.pow(2)).divide(q).multiply(BigInteger.TWO.multiply(aRoot).modInverse(q)).mod(q);
            BigInteger a = q.pow(2);
            BigInteger b = k.multiply(q).add(aRoot);
            BigInteger c = b.pow(2).subtract(N).divide(a);

            if (delta == Integer.MIN_VALUE) {
                delta = calculateDelta(a, b, c);
            }

            PolynomialData polynomialData = new PolynomialData(a, b, c, delta, N);

            double baseLog = Math.log(polynomialData.getSievingValue(delta).abs().doubleValue());
            polynomialData.scale = 256 / baseLog / 4;

            polynomialData.wheels = buildWheels(a, b, delta, polynomialData.scale);


            Analytics.POLY_MINER.end();
            try {
                DataQueue.polynomialData.put(polynomialData);
            } catch (InterruptedException e) {

                return;
            }
        }
    }

    private int calculateDelta(BigInteger a, BigInteger b, BigInteger c) {
        int loopSize = b.pow(2).subtract(a.multiply(c)).sqrt().subtract(b).divide(a).intValue();
        return loopSize - MagicNumbers.instance.loopsSize * MagicNumbers.instance.loopsCount / 2;
    }

    private List<Wheel> buildWheels(BigInteger a, BigInteger b, int delta, double scale) {
        boolean create = false;
        List<Wheel> wheels = WheelPool.instance.get();
        if (wheels == null) {
            create = true;
            wheels = new ArrayList<>();
        }
        List<Integer> primeBase = PrimeBase.instance.primeBase;
        int wheelIndex = 0;
        for (int i = 0, primeBaseSize = primeBase.size(); i < primeBaseSize; i++) {
            int p = primeBase.get(i);
            if (p < MagicNumbers.instance.minPrimeSize) {
                continue;
            }
            if (p == 2) {
                int startingPosition = b.mod(BigInteger.TWO).intValue() == 0 ? 1 : 0;
                if (create) {
                    wheels.add(new Wheel(p,i, startingPosition, delta, scale));
                } else {
                    wheels.get(wheelIndex).reset(startingPosition, delta);
                    wheelIndex++;
                }
                continue;
            }
            BigInteger prime = BigInteger.valueOf(p);
            BigInteger root = map.computeIfAbsent(prime, x -> MathUtils.modSqrt(N, x));
            BigInteger aModInversePrime = a.modInverse(prime);

            int p1 = root.subtract(b).multiply(aModInversePrime).mod(prime).intValue();
            int p2 = prime.subtract(root).subtract(b).multiply(aModInversePrime).mod(prime).intValue();
            if (create) {
                wheels.add(new Wheel(p, i, p1, delta, scale));
                wheels.add(new Wheel(p, i, p2, delta, scale));
            } else {
                wheels.get(wheelIndex).reset(p1, delta);
                wheels.get(wheelIndex + 1).reset(p2, delta);
                wheelIndex += 2;
            }
        }
        return wheels;
    }
}

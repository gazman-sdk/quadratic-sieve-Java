package com.gazman.quadratic_sieve.core.poly;

import com.gazman.quadratic_sieve.data.DataQueue;
import com.gazman.quadratic_sieve.data.MagicNumbers;
import com.gazman.quadratic_sieve.data.PolynomialData;
import com.gazman.quadratic_sieve.data.PrimeBase;
import com.gazman.quadratic_sieve.debug.Analytics;
import com.gazman.quadratic_sieve.utils.MathUtils;

import java.math.BigInteger;
import java.util.*;

/**
 * Responsible for generating multiple polynomial of the form (ax+b)^2-N
 */
public class PolyMiner implements Runnable {
    public static final PolyMiner instance = new PolyMiner();
    private List<BigInteger> primeModSquares;
    private BigInteger N;
    private int delta = Integer.MIN_VALUE;

    public void start(BigInteger N) {
        this.N = N;
        new Thread(this, "PolyMiner").start();
    }

    @Override
    public void run() {
        initModSquaresCache();
        List<Integer> primeBase = PrimeBase.instance.primeBase;
        BigInteger m = BigInteger.valueOf(MagicNumbers.instance.loopsSize * MagicNumbers.instance.loopsCount);
        BigInteger targetA = N.multiply(BigInteger.TWO).sqrt().divide(m);
        int minPrimeIndex = Collections.binarySearch(primeBase, 2000) * -1 + 1;

        Random random = new Random();
        //noinspection InfiniteLoopStatement
        while (true) {
            List<Integer> primeBaseIndexes = generateA(primeBase, targetA, minPrimeIndex, random);
            buildBlock(new AData(primeBaseIndexes));
        }
    }

    private static List<Integer> generateA(List<Integer> primeBase, BigInteger targetA, int minPrimeIndex, Random random) {
        BigInteger a = BigInteger.ONE;
        ArrayList<Integer> list = new ArrayList<>(6);
        HashSet<Integer> indexes = new HashSet<>(128);
        while (true) {
            int compare = a.compareTo(targetA);
            if (compare < 0) {
                int i = random.nextInt(Math.min(primeBase.size() - minPrimeIndex, 2000)) + minPrimeIndex;
                if (!indexes.add(i)) {
                    continue;
                }
                a = a.multiply(BigInteger.valueOf(primeBase.get(i)));
                list.add(i);
            } else {
                break;
            }
        }

        for (int i = 0; i < 10; i++) {
            for (int k = list.size() - 1; k >= 0; k--) {
                Integer prime = primeBase.get(list.get(k));
                a = a.divide(BigInteger.valueOf(prime));
                BigInteger reminder = targetA.divide(a);
                int j;
                if (reminder.bitLength() > 30) {
                    j = primeBase.size() - 1;
                } else {
                    j = Collections.binarySearch(primeBase, reminder.intValue());
                    if (j < 0) {
                        j = -j - 1;
                    }
                    if (j < minPrimeIndex) {
                        j = minPrimeIndex;
                    }
                }
                if (j == primeBase.size()) {
                    j--;
                }
                while (true) {
                    boolean contains = indexes.contains(j);
                    if (!contains) break;
                    j--;
                }
                a = a.multiply(BigInteger.valueOf(primeBase.get(j)));
                list.set(k, j);
                indexes.remove(list.get(k));
                indexes.add(j);
            }
        }
        Collections.sort(list);
        return list;
    }

    private void initModSquaresCache() {
        primeModSquares = new ArrayList<>(PrimeBase.instance.primeBase.size());
        List<Integer> primeBase = PrimeBase.instance.primeBase;
        for (int i = 0, primeBaseSize = primeBase.size(); i < primeBaseSize; i++) {
            int p = primeBase.get(i);
            if (p < MagicNumbers.instance.minPrimeSize || p == 2) {
                continue;
            }
            primeModSquares.add(MathUtils.modSqrt(N, BigInteger.valueOf(p)));
        }
    }

    private void buildBlock(AData aData) {
        BigInteger a = extractA(aData);
        List<BigInteger> bParts = extractBParts(aData, a);
        int limit = (int) (Math.pow(2, bParts.size()));

        buildPolynomials(aData, a, bParts, limit);
    }

    public static List<BigInteger> buildAModInverseList(AData aData, BigInteger a) {
        List<Integer> primeBase = PrimeBase.instance.primeBase;
        List<BigInteger> aModInverseList = new ArrayList<>(primeBase.size());
        for (int i = 0, filteredPrimes = 0, primeBaseSize = primeBase.size(); i < primeBaseSize; i++) {
            int p = primeBase.get(i);
            if (p < MagicNumbers.instance.minPrimeSize || p == 2) {
                continue;
            }
            if (filteredPrimes != -1 && i == aData.primesIndexes.get(filteredPrimes)) {
                if (filteredPrimes < aData.primesIndexes.size() - 1) {
                    filteredPrimes++;
                } else {
                    filteredPrimes = -1;
                }
                continue;
            }
            aModInverseList.add(a.modInverse(BigInteger.valueOf(p)));
        }

        return aModInverseList;
    }

    private void buildPolynomials(AData aData, BigInteger a, List<BigInteger> bParts, int limit) {
        List<BigInteger> aModInverseList = buildAModInverseList(aData, a);


        for (int i = 0; i < limit; i++) {
            Analytics.start();
            BigInteger b = BigInteger.ZERO;
            for (int j = 0, size = bParts.size(); j < size; j++) {
                if ((i & (1 << (j))) == 0) {
                    b = b.add(bParts.get(j));
                } else {
                    b = b.add(bParts.get(j).negate());
                }
            }

            if (b.compareTo(BigInteger.ZERO) < 0) {
                continue;
            }

            BigInteger c = b.pow(2).subtract(N).divide(a);

            if (delta == Integer.MIN_VALUE) {
                delta = calculateDelta(a, b, c);
            }

            PolynomialData polynomialData = new PolynomialData(a, b, c, delta, N, primeModSquares, aData, aModInverseList);
            double baseLog = Math.log(polynomialData.getSievingValue(delta).abs().doubleValue());
            polynomialData.scale = 256 / baseLog / 4;
            Analytics.POLY_MINER_TOTAL.end();
            try {
                DataQueue.polynomialData.put(polynomialData);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private List<BigInteger> extractBParts(AData aData, BigInteger a) {
        List<BigInteger> list = new ArrayList<>(aData.primesIndexes.size());
        for (int i : aData.primesIndexes) {
            BigInteger b = BigInteger.valueOf(PrimeBase.instance.primeBase.get(i));
            BigInteger mod = MathUtils.modSqrt(N, b);

            BigInteger localP = a.divide(b);

            BigInteger result = mod.multiply(localP.modInverse(b)).multiply(localP);

            BigInteger x = result.mod(a);

            list.add(x);
        }
        return list;
    }

    private static BigInteger extractA(AData aData) {
        BigInteger a = BigInteger.ONE;
        for (int i : aData.primesIndexes) {
            a = BigInteger.valueOf(PrimeBase.instance.primeBase.get(i)).multiply(a);
        }
        return a;
    }

    private static int calculateDelta(BigInteger a, BigInteger b, BigInteger c) {
        int loopSize = b.pow(2).subtract(a.multiply(c)).sqrt().subtract(b).divide(a).intValue();
        return loopSize - MagicNumbers.instance.loopsSize * MagicNumbers.instance.loopsCount / 2;
    }
}


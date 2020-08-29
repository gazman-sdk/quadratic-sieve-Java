package com.gazman.quadratic_sieve.data;

import com.gazman.quadratic_sieve.core.poly.AData;
import com.gazman.quadratic_sieve.core.poly.WheelPool;
import com.gazman.quadratic_sieve.wheel.Wheel;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class PolynomialData {
    public final BigInteger a;
    public final BigInteger b;
    public final BigInteger c;
    public final BigInteger N;
    public final int delta;
    public final AData aData;
    private final List<BigInteger> aModInverseList;
    private final List<BigInteger> primeModSquares;

    public double scale;


    public PolynomialData(BigInteger a, BigInteger b, BigInteger c,
                          int delta, BigInteger N, List<BigInteger> primeModSquares,
                          AData aData, List<BigInteger> aModInverseList) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.N = N;
        this.delta = delta;
        this.primeModSquares = primeModSquares;
        this.aData = aData;
        this.aModInverseList = aModInverseList;
    }


    public BigInteger getSievingValue(long localX) {
        BigInteger x = BigInteger.valueOf(localX);
        return a.multiply(x.pow(2)).add(b.multiply(x).multiply(BigInteger.TWO).add(c));
    }

    public BigInteger getA(long localX) {
        return a.multiply(BigInteger.valueOf(localX)).add(b);
    }

    public BigInteger getB(long localX) {
        return getA(localX).pow(2).subtract(N);
    }

    public List<Wheel> buildWheels() {
        boolean create = false;
        List<Wheel> wheels = WheelPool.instance.get(this);
        if (wheels == null) {
            create = true;
            wheels = new ArrayList<>();
        }
        List<Integer> primeBase = PrimeBase.instance.primeBase;
        int wheelIndex = 0;
        int aModInverseIndex = 0;
        for (int i = 0, primeBaseSize = primeBase.size(), modSquareIndex = 0, filteredPrimesIndex = 0; i < primeBaseSize; i++) {
            int p = primeBase.get(i);
            if (p < MagicNumbers.instance.minPrimeSize) {
                continue;
            }
            if (p == 2) {
                int startingPosition = b.mod(BigInteger.TWO).intValue() == 0 ? 1 : 0;
                if (create) {
                    wheels.add(new Wheel(p, i, startingPosition, delta, scale));
                } else {
                    wheels.get(wheelIndex).reset(startingPosition, delta);
                    wheelIndex++;
                }
                continue;
            }
            if (filteredPrimesIndex != -1 && i == aData.primesIndexes.get(filteredPrimesIndex)) {
                modSquareIndex++;
                if (filteredPrimesIndex < aData.primesIndexes.size() - 1) {
                    filteredPrimesIndex++;
                } else {
                    filteredPrimesIndex = -1;
                }
                continue;
            }
            BigInteger prime = BigInteger.valueOf(p);
            BigInteger root = primeModSquares.get(modSquareIndex);
            BigInteger aModInversePrime = aModInverseList.get(aModInverseIndex);
            modSquareIndex++;
            aModInverseIndex++;

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

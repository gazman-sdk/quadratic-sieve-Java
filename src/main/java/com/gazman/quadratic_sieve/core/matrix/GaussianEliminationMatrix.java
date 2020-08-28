package com.gazman.quadratic_sieve.core.matrix;

import com.gazman.quadratic_sieve.QuadraticSieve;
import com.gazman.quadratic_sieve.data.BSmooth;
import com.gazman.quadratic_sieve.data.PrimeBase;
import com.gazman.quadratic_sieve.debug.Logger;

import java.math.BigInteger;
import java.util.*;

public class GaussianEliminationMatrix {
    private final List<BitSet> solutionMatrix = new ArrayList<>();
    private final Map<Integer, Integer> eliminatorsMap = new HashMap<>();
    private final List<BSmooth> bSmoothList = new ArrayList<>();
    private BigInteger N;

    public void add(BSmooth bSmooth) {
        bSmoothList.add(bSmooth);
        addToSolutionMatrix();
        int bSmoothIndex = bSmoothList.size() - 1;

        for (int i = bSmooth.vector.previousSetBit(bSmooth.vector.size()); i >= 0; i = bSmooth.vector.previousSetBit(i - 1)) {
            Integer eliminatorIndex = eliminatorsMap.get(i);
            if (eliminatorIndex != null) {
                xor(eliminatorIndex, bSmoothIndex);
            } else {
                eliminatorsMap.put(i, bSmoothIndex);
                return;
            }
        }

        extractSolution(solutionMatrix.size() - 1, this.N);
    }

    public int getSize() {
        return bSmoothList.size();
    }

    private void addToSolutionMatrix() {
        BitSet row = new BitSet(bSmoothList.size());
        row.set(bSmoothList.size() - 1);
        solutionMatrix.add(row);
    }

    private void xor(int eliminatorIndex, Integer bSmoothIndex) {
        BSmooth bSmooth = bSmoothList.get(bSmoothIndex);
        BSmooth eliminator = bSmoothList.get(eliminatorIndex);
        bSmooth.vector.xor(eliminator.vector);
        solutionMatrix.get(bSmoothIndex).xor(solutionMatrix.get(eliminatorIndex));
    }

    private void extractSolution(int index, BigInteger N) {
        List<BSmooth> solution = extractSolution(solutionMatrix, bSmoothList, index);
        BigInteger a = BigInteger.ONE;
        BigInteger b = BigInteger.ONE;


        int[] vectorParts = new int[PrimeBase.instance.primeBase.size()];

        for (BSmooth bSmooth : solution) {
            a = bSmooth.getA().multiply(a).mod(N);
            BigInteger nextB = bSmooth.getB();
            BitSet vector = bSmooth.originalVector;
            for (int i = vector.previousSetBit(vector.size()); i >= 0; i = vector.previousSetBit(i - 1)) {
                nextB = nextB.divide(BigInteger.valueOf(PrimeBase.instance.primeBase.get(i)));
                vectorParts[i]++;
            }

            if (nextB.compareTo(BigInteger.ZERO) < 0) {
                Logger.log("Error extracting solution");
                return;
            }
            b = nextB.sqrt().multiply(b).mod(N);
        }

        for (int i = 0; i < vectorParts.length; i++) {
            if (vectorParts[i] == 0) {
                continue;
            }
            int prime = PrimeBase.instance.primeBase.get(i);
            BigInteger p = BigInteger.valueOf(prime).modPow(BigInteger.valueOf(vectorParts[i] / 2), N);
            b = b.multiply(p).mod(N);
        }

        BigInteger maybeSolution = a.add(b).gcd(N);
        if (!maybeSolution.equals(N) && !maybeSolution.equals(BigInteger.ONE)) {
            Logger.logProgress(true);
            QuadraticSieve.shutDown(maybeSolution);
        }  //            Logger.log(index + " bad luck " + maybeSolution);

    }


    private List<BSmooth> extractSolution(List<BitSet> solutionMatrix, List<BSmooth> bSmoothList,
                                          int bSmoothIndex) {
        List<BSmooth> solution = new ArrayList<>();
        BitSet bitSet = solutionMatrix.get(bSmoothIndex);
        for (int i = 0; i < bSmoothList.size(); i++) {
            if (bitSet.get(i)) {
                solution.add(bSmoothList.get(i));
            }
        }

        return solution;
    }

    public void setN(BigInteger N) {
        this.N = N;
    }
}

package com.gazman.quadratic_sieve.matrix;

import com.gazman.quadratic_sieve.data.BSmooth;
import com.gazman.quadratic_sieve.logger.Logger;

import java.math.BigInteger;
import java.util.*;

public class GaussianEliminationMatrix4 {
    private final List<BitSet> solutionMatrix = new ArrayList<>();
    private final Map<Integer, Integer> eliminatorsMap = new HashMap<>();
    private final List<BSmooth> bSmoothList = new ArrayList<>();
    private BigInteger N;

    public void add(BSmooth bSmooth) {
        Logger.MATRIX.start();
        bSmoothList.add(bSmooth);
        addToSolutionMatrix();


        for (int i = bSmooth.vector.nextSetBit(0); i >= 0; i = bSmooth.vector.nextSetBit(i + 1)) {
            Integer eliminatorIndex = eliminatorsMap.get(i);
            if (eliminatorIndex != null) {
                xor(eliminatorIndex, bSmoothList.size() - 1);
            }
        }

        addEliminator(bSmoothList.size() - 1);
        Logger.MATRIX.end();
    }

    public int getSize() {
        return bSmoothList.size();
    }

    private void addToSolutionMatrix() {
        BitSet row = new BitSet(bSmoothList.size());
        row.set(bSmoothList.size() - 1);
        solutionMatrix.add(row);
    }

    private void addEliminator(int eliminatorIndex) {
        BSmooth eliminator = bSmoothList.get(eliminatorIndex);
        if (eliminator.vector.isEmpty()) {
            extractSolution(solutionMatrix.size() - 1, this.N);
        } else {
            List<Integer> list = new ArrayList<>();

            int eliminatingBitIndex = eliminator.vector.previousSetBit(eliminator.vector.size());
            eliminatorsMap.put(eliminatingBitIndex, bSmoothList.indexOf(eliminator));
            for (int bSmoothIndex = 0; bSmoothIndex < bSmoothList.size(); bSmoothIndex++) {
                BSmooth bSmooth = bSmoothList.get(bSmoothIndex);
                if (bSmooth != eliminator && bSmooth.vector.get(eliminatingBitIndex)) {
                    boolean turningIntoEliminator = true;
                    for (int i = 0; i < eliminatingBitIndex; i++) {
                        if (bSmooth.vector.get(i)) {
                            turningIntoEliminator = false;
                            break;
                        }
                    }
                    xor(eliminatorIndex, bSmoothIndex);
                    if (turningIntoEliminator) {
                        list.add(bSmoothIndex);
                    }
                }
            }

            for (int i : list) {
                addEliminator(i);
            }
        }
    }

    private int extractEliminatorBitIndex(BSmooth eliminator) {
        int eliminatingBitIndex = 0;
        for (int i = 0; i < eliminator.vector.size(); i++) {
            if (eliminator.vector.get(i)) {
                eliminatingBitIndex = i;
                break;
            }
        }
        return eliminatingBitIndex;
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

        for (BSmooth smooth : solution) {
            a = smooth.getA().multiply(a);
            b = smooth.getB().multiply(b);
        }


        BigInteger maybeSolution = a.add(b.sqrt()).gcd(N);
        if (!maybeSolution.equals(N) && !maybeSolution.equals(BigInteger.ONE)) {
            Logger.log("Found " + bSmoothList.size() + " bSmooth values");
            Logger.log("Oh yeah " + maybeSolution);
            System.exit(0);
        } else {
            Logger.log(index + " bad luck " + maybeSolution);
        }
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

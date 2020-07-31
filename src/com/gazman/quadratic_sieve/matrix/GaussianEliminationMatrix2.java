package com.gazman.quadratic_sieve.matrix;

import com.gazman.quadratic_sieve.data.BSmooth;

import java.util.*;

public class GaussianEliminationMatrix2 {

    public List<List<BSmooth>> solve(List<BSmooth> bSmoothList, List<Integer> primeBase) {
        List<List<BSmooth>> solutions = new ArrayList<>();

        bSmoothList = filerSinglePrimes(bSmoothList, primeBase.size());
        List<BSmooth> originalList = deepCopy(bSmoothList);

        List<BitSet> solutionMatrix = buildSolutionMatrix(bSmoothList.size());
        Set<BSmooth> eliminators = new HashSet<>();

        for (int i = 0; i < primeBase.size(); i++) {
            int eliminatorIndex = findEliminator(bSmoothList, eliminators, i);
            if (eliminatorIndex == -1) {
                continue;
            }
            eliminate(eliminatorIndex, bSmoothList, solutionMatrix, i);
        }

        for (int i = 0; i < bSmoothList.size(); i++) {
            BSmooth bSmooth = bSmoothList.get(i);
            if (bSmooth.vector.isEmpty()) {
                solutions.add(extractSolution(solutionMatrix, originalList, i));
            }
        }

        return solutions;
    }

    private List<BSmooth> deepCopy(List<BSmooth> bSmoothList) {
        ArrayList<BSmooth> copy = new ArrayList<>();
        for (BSmooth bSmooth : bSmoothList) {
            copy.add(bSmooth.copy());
        }
        return copy;
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

    private void eliminate(int eliminatorIndex, List<BSmooth> bSmoothList, List<BitSet> solutionMatrix, int primeIndex) {
        BitSet eliminator = bSmoothList.get(eliminatorIndex).vector;
        for (int smoothIndex = 0; smoothIndex < bSmoothList.size(); smoothIndex++) {
            BSmooth bSmooth = bSmoothList.get(smoothIndex);
            if (bSmooth.vector != eliminator && bSmooth.vector.get(primeIndex)) {
                bSmooth.vector.xor(eliminator);
                solutionMatrix.get(smoothIndex).xor(solutionMatrix.get(eliminatorIndex));
            }
        }
    }

    private int findEliminator(List<BSmooth> bSmoothList, Set<BSmooth> eliminators, int i) {
        for (int smoothIndex = 0; smoothIndex < bSmoothList.size(); smoothIndex++) {
            BSmooth bSmooth = bSmoothList.get(smoothIndex);
            if (bSmooth.vector.get(i) && eliminators.add(bSmooth)) {
                return smoothIndex;
            }
        }
        return -1;
    }

    private List<BitSet> buildSolutionMatrix(int size) {
        List<BitSet> solutionMatrix = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            solutionMatrix.add(new BitSet());
            solutionMatrix.get(row).set(row, true);
        }
        return solutionMatrix;
    }

    private List<BSmooth> filerSinglePrimes(List<BSmooth> bSmoothList, int primeBaseSize) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int bSmoothIndex = 0; bSmoothIndex < bSmoothList.size(); bSmoothIndex++) {
            BSmooth bSmooth = bSmoothList.get(bSmoothIndex);
            for (int i = 0; i < primeBaseSize; i++) {
                if (bSmooth.vector.get(i)) {
                    Integer integer = map.get(i);
                    if (integer == null) {
                        map.put(i, -bSmoothIndex);
                        break;
                    } else if (integer < 0) {
                        map.put(i, bSmoothIndex);
                    }
                }
            }
        }

        ArrayList<BSmooth> bSmooths = new ArrayList<>(bSmoothList);

        ArrayList<Integer> indexesToRemove = new ArrayList<>();

        for (Integer value : map.values()) {
            if (value < 0) {
                indexesToRemove.add(value * -1);
            }
        }

        Collections.sort(indexesToRemove);
        for (int i = indexesToRemove.size() - 1; i >= 0; i--) {
            bSmooths.remove((int) indexesToRemove.get(i));
        }

        return bSmooths;
    }
}

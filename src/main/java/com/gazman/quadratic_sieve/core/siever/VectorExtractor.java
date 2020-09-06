package com.gazman.quadratic_sieve.core.siever;

import com.gazman.quadratic_sieve.data.*;
import com.gazman.quadratic_sieve.debug.Analytics;
import com.gazman.quadratic_sieve.primes.BigPrimes;

import java.util.BitSet;
import java.util.List;

/**
 * Responsible for sieving
 */
public class VectorExtractor {

    public static void extract(PolynomialData polynomialData, List<BSmoothData> bSmoothList) {
        for (int j = 0, bSmoothListSize = bSmoothList.size(); j < bSmoothListSize; j++) {
            BSmoothData bSmoothData = bSmoothList.get(j);
            if (bSmoothData.ignoreA || bSmoothData.ignoreB) {
                continue;
            }
            List<Integer> primeBase = PrimeBase.instance.primeBase;
            long reminderA = bSmoothData.reminderA;

            BitSet vector = bSmoothData.vector;
            for (int i = vector.previousSetBit(vector.size()); i >= 0; i = vector.previousSetBit(i - 1)) {
                reminderA = checkPrime(vector, i, primeBase.get(i), reminderA);
            }



//            if (bSmoothData.reminder < 0) {
//                vector.set(0);
//                bSmoothData.reminder *= -1;
//            }
            int powerTwo = Long.numberOfTrailingZeros(reminderA);
            if ((powerTwo & 0x1) == 1) {
                vector.set(1);
            }
            reminderA >>= powerTwo;

            int primeBaseSize = primeBase.size();
            for (int i = 2; i < primeBaseSize; i++) {
                int prime = primeBase.get(i);
                if (prime >= MagicNumbers.instance.minPrimeSize) {
                    break;
                }
                reminderA = checkPrime(vector, i, prime, reminderA);
            }

            BSmooth bSmooth = new BSmooth(polynomialData, bSmoothData.localX, vector);
            if (reminderA == 1) {
                try {
                    Analytics.start();
                    DataQueue.bSmooths.put(bSmooth);
                    Analytics.SIEVE_QUEUE_B_SMOOTH.end();
                } catch (InterruptedException e) {
                    return;
                }
            } else {
                if (reminderA < MagicNumbers.instance.maxBigPrimeSize) {
                    BigPrimes.instance.addBigPrime(bSmooth, reminderA);
                }
            }
        }

        for (int i = 0, bSmoothListSize = bSmoothList.size(); i < bSmoothListSize; i++) {
            BSmoothData bSmoothData = bSmoothList.get(i);
            BSmoothDataPool.put(bSmoothData);
        }
    }

    private static long checkPrime(BitSet vector, int primeIndex, int prime, long reminder) {
        if (reminder % prime == 0) {
            int count = 0;
            do {
                reminder /= prime;
                count++;
            } while (reminder % prime == 0);
            if ((count & 0x1) == 1) {
                vector.flip(primeIndex);
            }
        }
        return reminder;
    }


}

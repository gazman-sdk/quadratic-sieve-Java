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

    public void extract(PolynomialData polynomialData, List<BSmoothData> bSmoothList) {
        for (BSmoothData bSmoothData : bSmoothList) {
            List<Integer> primeBase = PrimeBase.instance.primeBase;

            BitSet vector = bSmoothData.vector;
            for (int i = vector.previousSetBit(vector.size()); i >= 0; i = vector.previousSetBit(i - 1)) {
                checkPrime(bSmoothData, vector, i, primeBase.get(i));
            }

            if (bSmoothData.reminder < 0) {
                vector.set(0);
                bSmoothData.reminder *= -1;
            }
            int powerTwo = Long.numberOfTrailingZeros(bSmoothData.reminder);
            if ((powerTwo & 0x1) == 1) {
                vector.set(1);
            }
            bSmoothData.reminder >>= powerTwo;

            for (int i = 2; i < primeBase.size(); i++) {
                int prime = primeBase.get(i);
                if(prime > MagicNumbers.instance.minPrimeSize){
                    break;
                }
                checkPrime(bSmoothData, vector, i, prime);
            }

            BSmooth bSmooth = new BSmooth(polynomialData, bSmoothData.localX, vector);
            if (bSmoothData.reminder == 1) {
                try {
                    Analytics.SIEVE_QUEUE_B_SMOOTH.start();
                    DataQueue.bSmooths.put(bSmooth);
                    Analytics.SIEVE_QUEUE_B_SMOOTH.end();
                } catch (InterruptedException e) {
                    return;
                }
            } else {
                BigPrimes.instance.addBigPrime(bSmooth, bSmoothData.reminder);
            }
        }
    }

    private void checkPrime(BSmoothData bSmoothData, BitSet vector, int primeIndex, int prime) {
        if (bSmoothData.reminder % prime == 0) {
            int count = 0;
            do {
                bSmoothData.reminder /= prime;
                count++;
            } while (bSmoothData.reminder % prime == 0);
            if ((count & 0x1) == 1) {
                vector.flip(primeIndex);
            }
        }
    }


}

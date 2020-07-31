package com.gazman.quadratic_sieve.wheel;

import java.math.BigInteger;

public class Wheel2 {
    private long startingPosition;
    private final long prime;
    private final double log;

    public Wheel2(long prime, long startingPosition) {
        this.startingPosition = startingPosition;
        this.prime = prime;
        log = Math.log(this.prime);
    }

    public void update(double[] logs) {
        for (; startingPosition < logs.length; startingPosition += prime) {
            logs[(int) startingPosition] += log;
        }

        this.startingPosition -= logs.length;


    }
}

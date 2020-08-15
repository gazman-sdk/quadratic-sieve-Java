package com.gazman.quadratic_sieve.wheel;

public class Wheel {
    private long startingPosition;
    private long prime;
    private double log;

    public void init(long prime, long startingPosition) {
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

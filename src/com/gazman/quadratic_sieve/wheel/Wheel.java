package com.gazman.quadratic_sieve.wheel;

public class Wheel {
    private long currentPosition;
    private long prime;
    public byte log;

    public void init(long prime, long startingPosition, int delta, double scale) {
        startingPosition = startingPosition + (prime - delta % prime);

        this.currentPosition = startingPosition;
        this.prime = prime;
        log = (byte) Math.round(Math.log(prime) * scale);
    }

    public void update(byte[] logs) {
        for (; currentPosition < logs.length; currentPosition += prime) {
            logs[(int) currentPosition] += log;
        }

        this.currentPosition -= logs.length;
    }
}

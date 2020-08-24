package com.gazman.quadratic_sieve.wheel;

public class Wheel {
    private long currentPosition;
    private final long prime;
    private final byte log;

    public Wheel(long prime, long startingPosition, int delta, double scale) {
        this.prime = prime;
        reset(startingPosition, delta);
        log = (byte) Math.round(Math.log(prime) * scale);
    }

    public void reset(long startingPosition, int delta) {
        currentPosition = (startingPosition + prime - delta % prime) % prime;
    }

    public void update(byte[] logs) {
        for (; currentPosition < logs.length; currentPosition += prime) {
            logs[(int) currentPosition] += log;
        }

        this.currentPosition -= logs.length;
    }
}

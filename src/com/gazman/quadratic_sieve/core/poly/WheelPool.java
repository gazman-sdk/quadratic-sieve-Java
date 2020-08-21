package com.gazman.quadratic_sieve.core.poly;

import com.gazman.quadratic_sieve.wheel.Wheel;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class WheelPool {
    private final Queue<Wheel> wheels = new ConcurrentLinkedDeque<>();

    public static final WheelPool instance = new WheelPool();

    public Wheel get(long prime, long startingPosition, int delta, double scale) {
        Wheel wheel = wheels.poll();
        if (wheel == null) {
            wheel = new Wheel();
        }
        wheel.init(prime, startingPosition, delta, scale);
        return wheel;
    }

    public void put(Wheel wheel) {
        wheels.add(wheel);
    }

}

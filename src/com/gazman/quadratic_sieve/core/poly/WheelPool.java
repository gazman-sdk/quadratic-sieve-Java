package com.gazman.quadratic_sieve.core.poly;

import com.gazman.quadratic_sieve.wheel.Wheel;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class WheelPool {
    private final Queue<List<Wheel>> wheelLists = new ConcurrentLinkedDeque<>();

    public static final WheelPool instance = new WheelPool();

    public List<Wheel> get() {
        return wheelLists.poll();
    }

    public void put(List<Wheel> wheels) {
        wheelLists.add(wheels);
    }

}

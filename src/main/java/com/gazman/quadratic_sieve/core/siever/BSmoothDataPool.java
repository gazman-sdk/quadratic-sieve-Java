package com.gazman.quadratic_sieve.core.siever;

import java.util.BitSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class BSmoothDataPool {
    private static final Queue<BSmoothData> dataQueue = new ConcurrentLinkedDeque<>();

    public static BSmoothData get(int localX, BitSet baseVector){
        BSmoothData data = dataQueue.poll();
        if(data == null) {
            data = new BSmoothData(localX, baseVector);
        }
        else{
            data.init(localX, baseVector);
        }
        return data;
    }

    public static void put(BSmoothData data){
        dataQueue.add(data);
    }
}

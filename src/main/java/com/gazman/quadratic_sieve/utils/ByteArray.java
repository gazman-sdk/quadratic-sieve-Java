package com.gazman.quadratic_sieve.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class ByteArray {

    private static final Unsafe UNSAFE;
    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static final byte ZERO_BYTE = (byte) 0;
    private final long address;
    public final int capacity;

    public ByteArray(int capacity) {
        this.capacity = capacity;
        address = UNSAFE.allocateMemory(capacity);
        clear();
    }

    public void add(int index, byte sum) {
        long address = this.address + index;
        byte aByte = UNSAFE.getByte(address);
        UNSAFE.putByte(address, (byte) (aByte + sum));
    }

    public long getLong(int index) {
        return UNSAFE.getLong(address + index);
    }

    public byte getByte(int index) {
        return UNSAFE.getByte(address + index);
    }

    /**
     * Set all bytes to ZERO
     */
    public void clear() {
        UNSAFE.setMemory(address, capacity, ZERO_BYTE);
    }

    /**
     * Set all bytes to `clear`
     *
     * @param clear a `clear` byte
     */
    public void clear(byte clear) {
        UNSAFE.setMemory(address, capacity, clear);
    }

    /**
     * Copy byteArray to this array using the byteArray capacity.
     */
    public void clear(ByteArray byteArray) {
        UNSAFE.copyMemory(byteArray.address, address, byteArray.capacity);
    }

    public void free() {
        UNSAFE.freeMemory(address);
    }
}

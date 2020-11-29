package com.daniel_araujo.byteringbuffer;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * These tests allow us to see good and bad usage patterns.
 */
public class ByteRingBufferUsageTest {
    @Test
    public final void addByteVariableToBuffer() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        byte var = 2;

        buffer.add(var);
    }

    @Test
    public final void addMultipleByteVariables() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        byte var1 = 2;
        byte var2 = 4;
        byte var3 = 6;

        buffer.add(var1, var2, var3);
    }

    @Test
    public final void addLiteralByte() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        // Without the cast the compiler will complain that it "Cannot resolve method 'add(int, byte)'"
        buffer.add((byte) 1);
    }

    @Test
    public final void addMultipleBytesInArray() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.add(new byte[] { 1, 2, 3 });

        byte[] result = new byte[3];
        buffer.peek(result);
        assertArrayEquals(new byte[] { 1, 2, 3 }, result);
    }

    @Test
    public final void getSingleByte() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.add((byte) 1);

        byte[] tmp = new byte[1];
        buffer.pop(tmp);
        byte var = tmp[0];

        assertEquals(1, var);
    }

    @Test
    public final void getMultipleBytesInSingleArray() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.add(new byte[] { 1, 2, 3 });

        byte[] result = new byte[3];
        buffer.peek(result);
        assertArrayEquals(new byte[] { 1, 2, 3 }, result);
    }
}

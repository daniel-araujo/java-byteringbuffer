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

        buffer.push(var);
    }

    @Test
    public final void addMultipleByteVariables() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        byte var1 = 2;
        byte var2 = 4;
        byte var3 = 6;

        buffer.push(var1, var2, var3);
    }

    @Test
    public final void addLiteralByte() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        // Without the cast the compiler will complain that it "Cannot resolve method 'add(int, byte)'"
        buffer.push((byte) 1);
    }

    @Test
    public final void addMultipleBytesInArray() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.push(new byte[] { 1, 2, 3 });

        assertArrayEquals(new byte[] { 1, 2, 3 }, buffer.peek(3));
    }

    @Test
    public final void getSingleByte() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.push((byte) 1);

        byte[] tmp = buffer.pop(1);
        byte var = tmp[0];

        assertEquals(1, var);
    }

    @Test
    public final void getMultipleBytesInSingleArray() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.push(new byte[] { 1, 2, 3 });

        assertArrayEquals(new byte[] { 1, 2, 3 }, buffer.pop(3));
    }

    @Test
    public final void removeMultipleBytes() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.push(new byte[] { 1, 2, 3 });

        buffer.drop(2);
    }

    @Test
    public final void removeAndRetrieveMultipleBytes() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.push(new byte[] { 1, 2, 3 });

        assertArrayEquals(new byte[] { 1, 2 }, buffer.pop(2));
    }
}

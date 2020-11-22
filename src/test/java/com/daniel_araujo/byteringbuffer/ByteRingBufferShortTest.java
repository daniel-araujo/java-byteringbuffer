package com.daniel_araujo.byteringbuffer;

import static org.junit.Assert.*;
import org.junit.Test;

public class ByteRingBufferShortTest {
    @Test
    public final void add_addOneShortAsTwoBytes() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new short[] { 1 });

        assertEquals(2, buffer.sizeUsed());
    }

    @Test
    public final void add_startsAddingFromIndexUpToEndOfArray() {
        ByteRingBuffer buffer = new ByteRingBuffer(6);

        assertEquals(2, buffer.add(new short[] { 1, 2, 3 }, 1));

        assertEquals(4, buffer.sizeUsed());
    }

    @Test
    public final void add_startsAddingFromIndexUpToGivenLength() {
        ByteRingBuffer buffer = new ByteRingBuffer(6);

        assertEquals(2, buffer.add(new short[] { 1, 2, 3, 4 }, 1, 2));

        assertEquals(4, buffer.sizeUsed());
    }

    @Test
    public final void peek_retrievesTwoBytesAsShort() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new short[] { 1 });

        short[] result = new short[1];
        assertEquals(1, buffer.peek(result));
        assertArrayEquals(new short[] { 1 }, result);
    }
}

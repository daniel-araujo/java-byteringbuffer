package com.daniel_araujo.byteringbuffer;

import static org.junit.Assert.*;
import org.junit.Test;

public final class ByteRingBufferDropTest {
    @Test
    public final void continuousBuffer_firstElement() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1, 2, 3, 4 });
        buffer.drop(1);
        assertEquals(3, buffer.sizeUsed());

        byte[] result = new byte[3];
        assertEquals(3, buffer.peek(result));
        assertArrayEquals(new byte[] { 2, 3, 4 }, result);
    }

    @Test
    public final void continuousBuffer_bufferSize() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1, 2, 3, 4 });
        buffer.drop(4);
        assertEquals(0, buffer.sizeUsed());

        byte[] result = new byte[4];
        assertEquals(0, buffer.peek(result));
    }

    @Test
    public final void partitionedBuffer_allElementsFromFirstPartition() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.overrunAdd(new byte[] { 1, 2, 3, 4, 5, 6 });
        buffer.drop(2);
        assertEquals(2, buffer.sizeUsed());

        byte[] result = new byte[2];
        assertEquals(2, buffer.peek(result));
        assertArrayEquals(new byte[] { 5, 6 }, result);
    }

    @Test
    public final void droppingMoreElementsThanItHasDoesNotCrashOrBreakInternalState() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1, 2 });
        buffer.drop(3);
        assertEquals(0, buffer.sizeUsed());
    }
}

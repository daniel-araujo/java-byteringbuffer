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

        assertArrayEquals(new byte[] { 2, 3, 4 }, buffer.peek(3));
    }

    @Test
    public final void continuousBuffer_bufferSize() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1, 2, 3, 4 });
        buffer.drop(4);
        assertEquals(0, buffer.sizeUsed());

        assertArrayEquals(new byte[] {}, buffer.peek(4));
    }

    @Test
    public final void partitionedBuffer_allElementsFromFirstPartition() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.overrunAdd(new byte[] { 1, 2, 3, 4, 5, 6 });
        buffer.drop(2);
        assertEquals(2, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 5, 6 }, buffer.peek(2));
    }

    @Test
    public final void droppingMoreElementsThanItHasDoesNotCrashOrBreakInternalState() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1, 2 });
        buffer.drop(3);
        assertEquals(0, buffer.sizeUsed());
    }
}

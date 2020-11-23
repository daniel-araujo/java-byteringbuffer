package com.daniel_araujo.byteringbuffer;

import static org.junit.Assert.*;
import org.junit.Test;

public final class ByteRingBufferPopTest {
    @Test
    public final void givenArrayDefinesHowManyElementsAreRemoved() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.overrunAdd(new byte[] { 1, 2, 3, 4 });
        assertEquals(4, buffer.sizeUsed());

        byte[] result = new byte[2];
        assertEquals(2, buffer.pop(result));
        assertArrayEquals(new byte[] { 1, 2 }, result);

        assertEquals(2, buffer.sizeUsed());

        assertEquals(2, buffer.peek(result));
        assertArrayEquals(new byte[] { 3, 4 }, result);
    }

    @Test
    public final void continuousBuffer_writesRemovedElementsToGivenArray() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.overrunAdd(new byte[] { 1, 2, 3, 4 });
        assertEquals(4, buffer.sizeUsed());

        byte[] result = new byte[4];
        assertEquals(4, buffer.pop(result));
        assertArrayEquals(new byte[] { 1, 2, 3, 4 }, result);

        assertEquals(0, buffer.sizeUsed());
    }

    @Test
    public final void partitionedBuffer_writesRemovedElementsToGivenArray() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.overrunAdd(new byte[] { 1, 2, 3, 4, 5 });
        assertEquals(4, buffer.sizeUsed());

        byte[] result = new byte[4];
        assertEquals(4, buffer.pop(result));
        assertArrayEquals(new byte[] { 2, 3, 4, 5 }, result);

        assertEquals(0, buffer.sizeUsed());
    }

    @Test
    public final void partitionedBuffer_readHalfOfContentsInBuffer() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.overrunAdd(new byte[] { 1, 2, 3, 4, 5 });
        assertEquals(4, buffer.sizeUsed());

        byte[] result = new byte[2];
        assertEquals(2, buffer.pop(result));
        assertArrayEquals(new byte[] { 2, 3 }, result);
        assertEquals(2, buffer.sizeUsed());

        assertEquals(2, buffer.peek(result));
        assertArrayEquals(new byte[] { 4, 5 }, result);
    }

    @Test
    public final void index_placesRemovedElementsAfterIndex() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1, 2, 3 });

        byte[] result = new byte[3];
        assertEquals(2, buffer.pop(result, 1));
        assertArrayEquals(new byte[] { 0, 1, 2 }, result);
        assertEquals(1, buffer.sizeUsed());

        byte[] result2 = new byte[1];
        assertEquals(1, buffer.peek(result2));
        assertArrayEquals(new byte[] { 3 }, result2);
    }
}

package com.daniel_araujo.byteringbuffer;

import static org.junit.Assert.*;
import org.junit.Test;

public class ByteRingBufferSizeTotalTest {
    @Test
    public final void reportsValueOfCapacityAfterCreatingObject() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        assertEquals(3, buffer.sizeTotal());
    }

    @Test
    public final void doesNotChangeAfterAddingElements() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.push(new byte[] { 1, 2, 3 });

        assertEquals(3, buffer.sizeTotal());
    }

    @Test
    public final void doesNotChangeWhenBufferBecomesPartitioned() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.overrunPush(new byte[] { 1, 2, 3, 4 });

        assertEquals(3, buffer.sizeTotal());
    }

    @Test
    public final void doesNotChangeWhenDroppingElements() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.push(new byte[] { 1, 2, 3 });

        buffer.drop(1);

        assertEquals(3, buffer.sizeTotal());
    }
}

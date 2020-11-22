package com.daniel_araujo.byteringbuffer;

import static org.junit.Assert.*;
import org.junit.Test;

public class ByteRingBufferSizeFreeTest {
    @Test
    public final void reportsValueOfCapacityAfterCreatingObject() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        assertEquals(3, buffer.sizeFree());
    }

    @Test
    public final void isDecreasedByEachElementAdded() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.add(new byte[] { 1, 2 });

        assertEquals(1, buffer.sizeFree());

        buffer.add(new byte[] { 1 });

        assertEquals(0, buffer.sizeFree());
    }

    @Test
    public final void reportsCorrectNumberOfFreeElementsWhenBufferIsPartitioned() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.overrunAdd(new byte[] { 1, 2, 3, 4 });

        assertEquals(0, buffer.sizeFree());

        buffer.drop(1);

        assertEquals(1, buffer.sizeFree());

        buffer.drop(1);

        assertEquals(2, buffer.sizeFree());

        buffer.add(new byte[] { 1 });

        assertEquals(1, buffer.sizeFree());
    }

    @Test
    public final void increasesWhenDroppingElements() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.add(new byte[] { 1, 2, 3 });

        assertEquals(0, buffer.sizeFree());

        buffer.drop(1);

        assertEquals(1, buffer.sizeFree());
    }
}

package com.daniel_araujo.byteringbuffer;

import static org.junit.Assert.*;
import org.junit.Test;

public class ByteRingBufferOverrunPushTest {
    @Test
    public final void fillBufferInOneCall() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.overrunPush(new byte[] { 1, 2, 3 });
        assertEquals(3, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 1, 2, 3 }, buffer.peek(3));
    }

    @Test
    public final void subsequentCallsPlacesElementsNextToPrevious() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.overrunPush(new byte[] { 1, 2 });
        buffer.overrunPush(new byte[] { 3 });
        assertEquals(3, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 1, 2, 3 }, buffer.peek(3));
    }

    @Test
    public final void fillHalf() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.overrunPush(new byte[] { 1, 2 });
        assertEquals(2, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 1, 2 }, buffer.peek(2));
    }

    @Test
    public final void overrunningReplacesOldValuesFirst() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.overrunPush(new byte[] { 1, 2, 3, 4 });
        assertEquals(3, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 2, 3, 4 }, buffer.peek(3));
    }

    @Test
    public final void overrunning() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.overrunPush(new byte[] { 1, 2 });
        buffer.overrunPush(new byte[] { 3, 4 });
        assertEquals(3, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 2, 3, 4 }, buffer.peek(3));
    }

    @Test
    public final void overfilling3TimesTooMuchStoresLastValues() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.overrunPush(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        assertEquals(3, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 7, 8, 9 }, buffer.peek(3));
    }

    @Test
    public final void indexlength_addsExactSize() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.overrunPush(new byte[] { 1, 2, 3, 4 }, 1, 3);
        assertEquals(3, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 2, 3, 4 }, buffer.peek(3));
    }

    @Test
    public final void indexlength_addsHalf() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.overrunPush(new byte[] { 1, 2, 3, 4 }, 1, 2);
        assertEquals(2, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 2, 3 }, buffer.peek(2));
    }

    @Test
    public final void indexlength_overfills() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.overrunPush(new byte[] { 1, 2, 3, 4, 5, 6, 7 }, 1, 6);
        assertEquals(3, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 5, 6, 7 }, buffer.peek(3));
    }

    @Test
    public final void varargsSupport() {
        ByteRingBuffer buffer = new ByteRingBuffer(2);

        buffer.overrunPush((byte) 1);
        buffer.overrunPush((byte) 2, (byte) 3);

        assertEquals(2, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 2, 3 }, buffer.peek(2));
    }
}

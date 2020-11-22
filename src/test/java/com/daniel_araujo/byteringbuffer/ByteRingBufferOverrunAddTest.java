package com.daniel_araujo.byteringbuffer;

import static org.junit.Assert.*;
import org.junit.Test;

public class ByteRingBufferOverrunAddTest {
    @Test
    public final void fillBufferInOneCall() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.overrunAdd(new byte[] { 1, 2, 3 });
        assertEquals(3, buffer.sizeUsed());

        byte[] result = new byte[3];
        buffer.peek(result);
        assertArrayEquals(new byte[] { 1, 2, 3 }, result);
    }

    @Test
    public final void subsequentCallsPlacesElementsNextToPrevious() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.overrunAdd(new byte[] { 1, 2 });
        buffer.overrunAdd(new byte[] { 3 });
        assertEquals(3, buffer.sizeUsed());

        byte[] result = new byte[3];
        buffer.peek(result);
        assertArrayEquals(new byte[] { 1, 2, 3 }, result);
    }

    @Test
    public final void fillHalf() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.overrunAdd(new byte[] { 1, 2 });
        assertEquals(2, buffer.sizeUsed());

        byte[] result = new byte[2];
        buffer.peek(result);
        assertArrayEquals(new byte[] { 1, 2 }, result);
    }

    @Test
    public final void overrunningReplacesOldValuesFirst() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.overrunAdd(new byte[] { 1, 2, 3, 4 });
        assertEquals(3, buffer.sizeUsed());

        byte[] result = new byte[3];
        buffer.peek(result);
        assertArrayEquals(new byte[] { 2, 3, 4 }, result);
    }

    @Test
    public final void overrunning() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.overrunAdd(new byte[] { 1, 2 });
        buffer.overrunAdd(new byte[] { 3, 4 });
        assertEquals(3, buffer.sizeUsed());

        byte[] result = new byte[3];
        assertEquals(3, buffer.peek(result));
        assertArrayEquals(new byte[] { 2, 3, 4 }, result);
    }

    @Test
    public final void overfilling3TimesTooMuchStoresLastValues() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.overrunAdd(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        assertEquals(3, buffer.sizeUsed());

        byte[] result = new byte[3];
        buffer.peek(result);
        assertArrayEquals(new byte[] { 7, 8, 9 }, result);
    }

    @Test
    public final void indexlength_addsExactSize() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.overrunAdd(new byte[] { 1, 2, 3, 4 }, 1, 3);
        assertEquals(3, buffer.sizeUsed());

        byte[] result = new byte[3];
        buffer.peek(result);
        assertArrayEquals(new byte[] { 2, 3, 4 }, result);
    }

    @Test
    public final void indexlength_addsHalf() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.overrunAdd(new byte[] { 1, 2, 3, 4 }, 1, 2);
        assertEquals(2, buffer.sizeUsed());

        byte[] result = new byte[2];
        buffer.peek(result);
        assertArrayEquals(new byte[] { 2, 3 }, result);
    }

    @Test
    public final void indexlength_overfills() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.overrunAdd(new byte[] { 1, 2, 3, 4, 5, 6, 7 }, 1, 6);
        assertEquals(3, buffer.sizeUsed());

        byte[] result = new byte[3];
        buffer.peek(result);
        assertArrayEquals(new byte[] { 5, 6, 7 }, result);
    }
}

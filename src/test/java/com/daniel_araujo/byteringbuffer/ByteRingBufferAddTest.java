package com.daniel_araujo.byteringbuffer;

import static org.junit.Assert.*;
import org.junit.Test;

public class ByteRingBufferAddTest {
    @Test
    public final void doesNothingIfArrayIsEmpty() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        assertEquals(0, buffer.add(new byte[] {}));
        assertEquals(0, buffer.sizeUsed());
    }

    @Test
    public final void onlyReturnsNumberOfElementsInserted() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        assertEquals(2, buffer.add(new byte[] { 1, 2 }));
    }

    @Test
    public final void doesNotReturnNumberOfElementsThatWereNotInserted() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        assertEquals(3, buffer.add(new byte[] { 1, 2, 3, 4 }));
    }

    @Test
    public final void returnsZeroWhenBufferIsFull() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.add(new byte[] { 1, 2, 3, 4 });

        assertEquals(0, buffer.add(new byte[] { 1 }));
    }

    @Test
    public final void discardsLastElementsWhenBufferCannotHoldAll() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.add(new byte[] { 1, 2, 3, 4 });

        assertEquals(3, buffer.sizeUsed());

        byte[] result = new byte[3];
        buffer.peek(result);
        assertArrayEquals(new byte[] { 1, 2, 3 }, result);
    }

    @Test
    public final void fillBufferInOneCall() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.add(new byte[] { 1, 2, 3 });
        assertEquals(3, buffer.sizeUsed());

        byte[] result = new byte[3];
        buffer.peek(result);
        assertArrayEquals(new byte[] { 1, 2, 3 }, result);
    }

    @Test
    public final void subsequentCallsPlacesElementsNextToPrevious() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.add(new byte[] { 1, 2 });
        buffer.add(new byte[] { 3 });
        assertEquals(3, buffer.sizeUsed());

        byte[] result = new byte[3];
        buffer.peek(result);
        assertArrayEquals(new byte[] { 1, 2, 3 }, result);
    }

    @Test
    public final void fillHalf() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1, 2 });
        assertEquals(2, buffer.sizeUsed());

        byte[] result = new byte[2];
        buffer.peek(result);
        assertArrayEquals(new byte[] { 1, 2 }, result);
    }

    @Test
    public final void overfillingInFirstCallDiscardsLastValues() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.add(new byte[] { 1, 2, 3, 4 });
        assertEquals(3, buffer.sizeUsed());

        byte[] result = new byte[3];
        buffer.peek(result);
        assertArrayEquals(new byte[] { 1, 2, 3 }, result);
    }

    @Test
    public final void overfillingInSecondCallWillNotOverwriteExistingValues() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.add(new byte[] { 1, 2 });

        assertEquals(1, buffer.add(new byte[] { 3, 4 }));

        assertEquals(3, buffer.sizeUsed());

        byte[] result = new byte[3];
        assertEquals(3, buffer.peek(result));
        assertArrayEquals(new byte[] { 1, 2, 3 }, result);
    }

    @Test
    public final void overfilling3TimesDoesNotOverwriteFirstValues() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.add(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });

        assertEquals(3, buffer.sizeUsed());

        byte[] result = new byte[3];

        buffer.peek(result);
        assertArrayEquals(new byte[] { 1, 2, 3 }, result);
    }

    @Test
    public final void extractsElementsStartingFromIndex() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        assertEquals(2, buffer.add(new byte[] { 1, 2, 3, 4 }, 2));
        assertEquals(2, buffer.sizeUsed());

        byte[] result = new byte[2];
        buffer.peek(result);
        assertArrayEquals(new byte[] { 3, 4 }, result);
    }

    @Test
    public final void extractsElementsStartingFromIndexAndOnlyUpToLength() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1, 2, 3, 4 }, 1, 2);
        assertEquals(2, buffer.sizeUsed());

        byte[] result = new byte[2];
        buffer.peek(result);
        assertArrayEquals(new byte[] { 2, 3 }, result);
    }

    @Test
    public final void varargsSupport() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        assertEquals(1, buffer.add((byte) 1));
        assertEquals(2, buffer.add((byte) 2, (byte) 3));

        assertEquals(3, buffer.sizeUsed());

        byte[] result = new byte[3];
        buffer.peek(result);
        assertArrayEquals(new byte[] { 1, 2, 3 }, result);
    }
}

package com.daniel_araujo.byteringbuffer;

import static org.junit.Assert.*;
import org.junit.Test;

public class ByteRingBufferPushTest {
    @Test
    public final void doesNothingIfArrayIsEmpty() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        assertEquals(0, buffer.push(new byte[] {}));
        assertEquals(0, buffer.sizeUsed());
    }

    @Test
    public final void onlyReturnsNumberOfElementsInserted() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        assertEquals(2, buffer.push(new byte[] { 1, 2 }));
    }

    @Test
    public final void doesNotReturnNumberOfElementsThatWereNotInserted() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        assertEquals(3, buffer.push(new byte[] { 1, 2, 3, 4 }));
    }

    @Test
    public final void returnsZeroWhenBufferIsFull() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.push(new byte[] { 1, 2, 3, 4 });

        assertEquals(0, buffer.push(new byte[] { 1 }));
    }

    @Test
    public final void discardsLastElementsWhenBufferCannotHoldAll() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.push(new byte[] { 1, 2, 3, 4 });

        assertEquals(3, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 1, 2, 3 }, buffer.peek(3));
    }

    @Test
    public final void fillBufferInOneCall() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.push(new byte[] { 1, 2, 3 });
        assertEquals(3, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 1, 2, 3 }, buffer.peek(3));
    }

    @Test
    public final void subsequentCallsPlacesElementsNextToPrevious() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.push(new byte[] { 1, 2 });
        buffer.push(new byte[] { 3 });
        assertEquals(3, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 1, 2, 3 }, buffer.peek(3));
    }

    @Test
    public final void fillHalf() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.push(new byte[] { 1, 2 });
        assertEquals(2, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 1, 2 }, buffer.peek(2));
    }

    @Test
    public final void overfillingInFirstCallDiscardsLastValues() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.push(new byte[] { 1, 2, 3, 4 });
        assertEquals(3, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 1, 2, 3 }, buffer.peek(3));
    }

    @Test
    public final void overfillingInSecondCallWillNotOverwriteExistingValues() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.push(new byte[] { 1, 2 });

        assertEquals(1, buffer.push(new byte[] { 3, 4 }));

        assertEquals(3, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 1, 2, 3 }, buffer.peek(3));
    }

    @Test
    public final void overfilling3TimesDoesNotOverwriteFirstValues() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.push(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });

        assertEquals(3, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 1, 2, 3 }, buffer.peek(3));
    }

    @Test
    public final void extractsElementsStartingFromIndex() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        assertEquals(2, buffer.push(new byte[] { 1, 2, 3, 4 }, 2));
        assertEquals(2, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 3, 4 }, buffer.peek(2));
    }

    @Test
    public final void extractsElementsStartingFromIndexAndOnlyUpToLength() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.push(new byte[] { 1, 2, 3, 4 }, 1, 2);
        assertEquals(2, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 2, 3 }, buffer.peek(2));
    }

    @Test
    public final void varargsSupport() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        assertEquals(1, buffer.push((byte) 1));
        assertEquals(2, buffer.push((byte) 2, (byte) 3));

        assertEquals(3, buffer.sizeUsed());

        assertArrayEquals(new byte[] { 1, 2, 3 }, buffer.peek(3));
    }
}

package com.daniel_araujo.byteringbuffer;

import static org.junit.Assert.*;
import org.junit.Test;

public class ByteRingBufferShortViewTest {
    @Test
    public final void add_addOneShortAsTwoBytes() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.shortView().add(new short[] { 1 });

        assertEquals(2, buffer.sizeUsed());
    }

    @Test
    public final void add_startsAddingFromIndexUpToEndOfArray() {
        ByteRingBuffer buffer = new ByteRingBuffer(6);

        assertEquals(2, buffer.shortView().add(new short[] { 1, 2, 3 }, 1));

        assertEquals(4, buffer.sizeUsed());
    }

    @Test
    public final void add_startsAddingFromIndexUpToGivenLength() {
        ByteRingBuffer buffer = new ByteRingBuffer(6);

        assertEquals(2, buffer.shortView().add(new short[] { 1, 2, 3, 4 }, 1, 2));

        assertEquals(4, buffer.sizeUsed());
    }

    @Test
    public final void add_cannotAddIfThereIsOnlyOneByteLeft() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1, 2, 3 });

        buffer.shortView().add(new short[] { 1 });

        assertEquals(3, buffer.sizeUsed());
    }

    @Test
    public final void overrunAdd_wontAddAnythingIfBufferHasNotEnoughCapacity() {
        ByteRingBuffer buffer = new ByteRingBuffer(1);

        buffer.shortView().overrunAdd(new short[] { 1 });

        assertEquals(0, buffer.sizeUsed());
    }

    @Test
    public final void overrunAdd_overwritesExistingElements() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.shortView().add(new short[] { 1, 2 });
        buffer.shortView().overrunAdd(new short[] { 3 });

        assertEquals(4, buffer.sizeUsed());

        short[] result = new short[2];
        assertEquals(2, buffer.shortView().peek(result));
        assertArrayEquals(new short[] { 2, 3 }, result);
    }

    @Test
    public final void overrunAdd_overwritesBytesWithShorts() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.add(new byte[] { -128 });
        buffer.shortView().overrunAdd(new short[] { 1, 2 });

        assertEquals(3, buffer.sizeUsed());

        byte[] result = new byte[3];
        assertEquals(3, buffer.peek(result));
        assertArrayEquals(new byte[] { 1, 0, 2 }, result);
    }

    @Test
    public final void peek_retrievesTwoBytesAsShort() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.shortView().add(new short[] { 1 });

        short[] result = new short[1];
        assertEquals(1, buffer.shortView().peek(result));
        assertArrayEquals(new short[] { 1 }, result);
    }

    @Test
    public final void peek_retrievesNothingIfThereIsOneByteLeft() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1 });

        short[] result = new short[1];
        assertEquals(0, buffer.shortView().peek(result));

        assertEquals("Should not write to array", 0, result[0]);

        assertEquals(1, buffer.sizeUsed());
    }

    @Test
    public final void pop_removesTwoBytesAsShort() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.shortView().add(new short[] { 1 });

        short[] result = new short[1];
        assertEquals(1, buffer.shortView().pop(result));
        assertArrayEquals(new short[] { 1 }, result);

        assertEquals(0, buffer.sizeUsed());
    }

    @Test
    public final void pop_wontRemoveOneByte() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1 });

        short[] result = new short[1];
        assertEquals(0, buffer.shortView().pop(result));

        assertEquals("Should not write to array", 0, result[0]);

        assertEquals(1, buffer.sizeUsed());
    }

    @Test
    public final void pop_onlyRemovesUpToGivenLength() {
        ByteRingBuffer buffer = new ByteRingBuffer(8);

        buffer.shortView().add(new short[] { 1, 2, 3, 4 });

        short[] result = new short[2];
        assertEquals(2, buffer.shortView().pop(result, 0, 2));

        assertArrayEquals(new short[] { 1, 2 }, result);

        assertEquals(4, buffer.sizeUsed());
    }

    @Test
    public final void pop_placesElementsAfterGivenIndex() {
        ByteRingBuffer buffer = new ByteRingBuffer(8);

        buffer.shortView().add(new short[] { 1, 2, 3, 4 });

        short[] result = new short[3];
        assertEquals(2, buffer.shortView().pop(result, 1, 2));

        assertArrayEquals(new short[] { 0, 1, 2 }, result);

        assertEquals(4, buffer.sizeUsed());
    }

    @Test
    public final void drop_doesNotRemoveOneByte() {
        ByteRingBuffer buffer = new ByteRingBuffer(6);

        buffer.add(new byte[] { 1 });

        buffer.shortView().drop(1);

        assertEquals(1, buffer.sizeUsed());
    }

    @Test
    public final void drop_removesTwoBytes() {
        ByteRingBuffer buffer = new ByteRingBuffer(6);

        buffer.add(new byte[] { 1, 2 });

        buffer.shortView().drop(1);

        assertEquals(0, buffer.sizeUsed());
    }

    @Test
    public final void drop_removesOnlyEntireShorts() {
        ByteRingBuffer buffer = new ByteRingBuffer(6);

        buffer.add(new byte[] { 1, 2, 3 });

        buffer.shortView().drop(1);

        assertEquals(1, buffer.sizeUsed());

        byte[] result = new byte[1];
        assertEquals(1, buffer.peek(result));
        assertArrayEquals(new byte[] { 3 }, result);
    }

    @Test
    public final void sizeTotal_returnsHowManyShortsCanBeAddedInTotal() {
        ByteRingBuffer buffer = new ByteRingBuffer(6);

        assertEquals(3, buffer.shortView().sizeTotal());
    }

    @Test
    public final void sizeTotal_onlyCountsCompleteShorts() {
        ByteRingBuffer buffer = new ByteRingBuffer(5);

        assertEquals(2, buffer.shortView().sizeTotal());
    }

    @Test
    public final void sizeFree_returnsHowManyShortsCanBeAdded() {
        ByteRingBuffer buffer = new ByteRingBuffer(6);

        assertEquals(3, buffer.shortView().sizeFree());

        buffer.shortView().add(new short[] { 1 });

        assertEquals(2, buffer.shortView().sizeFree());
    }

    @Test
    public final void sizeFree_onlyCountsCompleteShorts() {
        ByteRingBuffer buffer = new ByteRingBuffer(6);

        buffer.add(new byte[] { 1 });

        assertEquals(2, buffer.shortView().sizeFree());

        buffer.shortView().add(new short[] { 1 });

        assertEquals(1, buffer.shortView().sizeFree());
    }

    @Test
    public final void sizeUsed_onlyCountsCompleteShorts() {
        ByteRingBuffer buffer = new ByteRingBuffer(6);

        buffer.add(new byte[] { 1 });

        assertEquals(0, buffer.shortView().sizeUsed());

        buffer.add(new byte[] { 2 });

        assertEquals(1, buffer.shortView().sizeUsed());
    }
}

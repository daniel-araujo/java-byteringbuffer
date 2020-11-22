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
    public final void peek_retrievesTwoBytesAsShort() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.shortView().add(new short[] { 1 });

        short[] result = new short[1];
        assertEquals(1, buffer.shortView().peek(result));
        assertArrayEquals(new short[] { 1 }, result);
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

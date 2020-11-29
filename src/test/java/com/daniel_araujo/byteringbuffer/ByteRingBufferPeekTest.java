package com.daniel_araujo.byteringbuffer;

import static org.junit.Assert.*;
import org.junit.Test;
import java.nio.ByteBuffer;

public final class ByteRingBufferPeekTest {
    @Test
    public final void bytebuffer_writesStartingFromPositionAndUpToItsCapacity() {
        ByteRingBuffer buffer = new ByteRingBuffer(5);

        buffer.add(new byte[] { 1, 2, 3 });
        assertEquals(3, buffer.sizeUsed());
        ByteBuffer result = ByteBuffer.allocate(3);
        result.position(1);
        assertEquals(2, buffer.peek(result));
        assertEquals(1, result.position());
        assertArrayEquals(new byte[] { 0, 1, 2 }, result.array());
    }

    @Test
    public final void bytebuffer_length_writesStartingFromPositionAndUpToLength() {
        ByteRingBuffer buffer = new ByteRingBuffer(5);

        buffer.add(new byte[] { 1, 2, 3 });
        assertEquals(3, buffer.sizeUsed());
        ByteBuffer result = ByteBuffer.allocate(3);
        result.position(1);
        assertEquals(1, buffer.peek(result, 1));
        assertEquals(1, result.position());
        assertArrayEquals(new byte[] { 0, 1, 0 }, result.array());
    }

    @Test
    public final void noargs_withoutPartition_readEntireBuffer() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.add(new byte[] { 1, 2, 3 });
        assertEquals(3, buffer.sizeUsed());
        byte[] result = new byte[3];
        assertEquals(3, buffer.peek(result));
        assertArrayEquals(new byte[] { 1, 2, 3 }, result);
    }

    @Test
    public final void noargs_withoutPartition_readHalf() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1, 2, 3, 4 });
        assertEquals(4, buffer.sizeUsed());
        byte[] result = new byte[2];
        assertEquals(2, buffer.peek(result));
        assertArrayEquals(new byte[] { 1, 2 }, result);
    }

    @Test
    public final void noargs_withoutPartition_readUpToSizeOfBuffer() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1, 2 });
        assertEquals(2, buffer.sizeUsed());
        byte[] result = new byte[4];
        assertEquals(2, buffer.peek(result));
        assertArrayEquals(new byte[] { 1, 2, 0, 0 }, result);
    }

    @Test
    public final void noargs_withPartition_readEntireBuffer() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.overrunAdd(new byte[] { 1, 2, 3, 4, 5 });
        assertEquals(4, buffer.sizeUsed());
        byte[] result = new byte[4];
        assertEquals(4, buffer.peek(result));
        assertArrayEquals(new byte[] { 2, 3, 4, 5 }, result);
    }

    @Test
    public final void noargs_withPartition_readUpToArraySize() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.overrunAdd(new byte[] { 1, 2, 3, 4, 5 });
        assertEquals(4, buffer.sizeUsed());
        byte[] result = new byte[2];
        assertEquals(2, buffer.peek(result));
        assertArrayEquals(new byte[] { 2, 3 }, result);
    }

    @Test
    public final void cb_callsFunctionIfBufferIsEmpty() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        PeekCallbackTracker peekCallback = new PeekCallbackTracker();
        buffer.peek(peekCallback);

        assertEquals(1, peekCallback.calls.size());
        assertEquals(0, peekCallback.calls.get(0).length);
    }

    @Test
    public final void cb_callsFunctionOnceWhenBufferIsContinuous() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1, 2, 3 });

        PeekCallbackTracker peekCallback = new PeekCallbackTracker();
        buffer.peek(peekCallback);

        assertEquals(1, peekCallback.calls.size());
        assertArrayEquals(new byte[] { 1, 2, 3 }, peekCallback.calls.get(0));
    }

    @Test
    public final void cb_callsFunctionOnceWhenBufferIsContinuousButDoesNotStartAtIndex0() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1, 2, 3 });
        buffer.drop(1);

        PeekCallbackTracker peekCallback = new PeekCallbackTracker();
        buffer.peek(peekCallback);

        assertEquals(1, peekCallback.calls.size());
        assertArrayEquals(new byte[] { 2, 3 }, peekCallback.calls.get(0));
    }

    @Test
    public final void cb_callsFunctionTwiceWhenBufferIsPartitioned() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.overrunAdd(new byte[] { 1, 2, 3, 4, 5 });

        PeekCallbackTracker peekCallback = new PeekCallbackTracker();
        buffer.peek(peekCallback);

        assertEquals(2, peekCallback.calls.size());
        assertArrayEquals(new byte[] { 2, 3, 4 }, peekCallback.calls.get(0));
        assertArrayEquals(new byte[] { 5 }, peekCallback.calls.get(1));
    }

    @Test
    public final void noargs_withPartition_firstPartitionIsNotFilled() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1, 2, 3 });
        buffer.drop(2);
        assertEquals(1, buffer.sizeUsed());
        byte[] result = new byte[4];
        assertEquals(1, buffer.peek(result));
        assertArrayEquals(new byte[] { 3, 0, 0, 0 }, result);
    }

    @Test
    public final void bugfix_peekCrashesAfterWrappingAroundTwiceAndALittleBitMore() {
        int rounds = 2;
        int payloadSize = 2;
        int total = rounds * payloadSize;

        ByteRingBuffer buffer = new ByteRingBuffer(total);

        for (int var6 = payloadSize * rounds + 1, var5 = 0; var5 < var6; ++var5) {
            buffer.add(new byte[payloadSize]);
        }

        byte[] result = new byte[total];
        assertEquals(total, buffer.peek(result));
    }

    @Test
    public final void length_returnsArrayOfElements() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1, 2, 3 });

        assertArrayEquals(new byte[] { 1, 2, 3 }, buffer.peek(3));
    }

    @Test
    public final void length_returnsEmptyArrayWhenBufferIsEmpty() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        assertArrayEquals(new byte[] {}, buffer.peek(3));
    }

    @Test
    public final void length_returnsEmptyArrayWhenLengthIs0() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1, 2, 3 });

        assertArrayEquals(new byte[] {}, buffer.peek(0));
    }

    @Test
    public final void length_retrievesOnlyLengthElements() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1, 2, 3 });

        assertArrayEquals(new byte[] { 1, 2 }, buffer.peek(2));
        assertArrayEquals(new byte[] { 1 }, buffer.peek(1));
    }

    @Test
    public final void length_returnsLessThanRequestedIfBufferDoesNotHaveEnoughElements() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1, 2, 3 });

        assertArrayEquals(new byte[] { 1, 2, 3 }, buffer.peek(4));
    }

    @Test
    public final void length_doesNotRemoveElements() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.add(new byte[] { 1, 2, 3 });

        assertArrayEquals(new byte[] { 1, 2 }, buffer.peek(2));
        assertArrayEquals(new byte[] { 1 }, buffer.peek(1));
    }
}

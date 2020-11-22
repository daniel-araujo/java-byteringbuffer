package com.daniel_araujo.byteringbuffer;

import static org.junit.Assert.*;
import org.junit.Test;

public final class ByteRingBufferClearTest {
    @Test
    public final void putsBufferInInitialState() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);

        buffer.overrunAdd(new byte[] { 1, 2, 3, 4, 5, 6 });
        buffer.clear();
        assertEquals(0, buffer.sizeUsed());
        buffer.overrunAdd(new byte[] { 1, 2, 3, 4 });

        PeekCallbackTracker peekCallback = new PeekCallbackTracker();
        buffer.peek(peekCallback);

        assertEquals(1, peekCallback.calls.size());
        assertArrayEquals(new byte[] { 1, 2, 3, 4 }, peekCallback.calls.get(0));
    }
}

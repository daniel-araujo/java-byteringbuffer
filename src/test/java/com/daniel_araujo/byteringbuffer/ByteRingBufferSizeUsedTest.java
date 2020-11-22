package com.daniel_araujo.byteringbuffer;

import static org.junit.Assert.*;
import org.junit.Test;

public class ByteRingBufferSizeUsedTest {
    @Test
    public final void initialSizeIs0() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        assertEquals(0, buffer.sizeUsed());
    }

    @Test
    public final void fullBufferReportsCapacity() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.add(new byte[] { 1, 2, 3 });
        assertEquals(3, buffer.sizeUsed());
    }

    @Test
    public final void notFillingBufferWillNotReportTotalSize() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.add(new byte[] { 1 });
        assertEquals(1, buffer.sizeUsed());

        buffer.add(new byte[] { 2 });
        assertEquals(2, buffer.sizeUsed());
    }

    @Test
    public final void overfillingStillReprtsTotalSize() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);

        buffer.add(new byte[] { 1, 2, 3 });
        buffer.add(new byte[] { 2 });
        assertEquals(3, buffer.sizeUsed());
    }
}

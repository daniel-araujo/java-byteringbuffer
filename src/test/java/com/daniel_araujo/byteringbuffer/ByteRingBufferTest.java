package com.daniel_araujo.byteringbuffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;

public final class ByteRingBufferTest {
    @Test
    public final void add_exactSizeToFillBuffer() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);
        buffer.add(new byte[] { 1, 2, 3 });
        Assert.assertEquals(3, buffer.getSize());
        byte[] result = new byte[3];
        buffer.peek(result);
        Assert.assertArrayEquals(new byte[] { 1, 2, 3 }, result);
    }

    @Test
    public final void add_callTwiceWithoutFilling() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);
        buffer.add(new byte[] { 1, 2 });
        buffer.add(new byte[] { 3 });
        Assert.assertEquals(3, buffer.getSize());
        byte[] result = new byte[3];
        buffer.peek(result);
        Assert.assertArrayEquals(new byte[] { 1, 2, 3 }, result);
    }

    @Test
    public final void add_fillHalf() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);
        buffer.add(new byte[] { 1, 2 });
        Assert.assertEquals(2, buffer.getSize());
        byte[] result = new byte[2];
        buffer.peek(result);
        Assert.assertArrayEquals(new byte[] { 1, 2 }, result);
    }

    @Test
    public final void add_overfillingInOneCallDiscardsOldValues() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);
        buffer.add(new byte[] { 1, 2, 3, 4 });
        Assert.assertEquals(3, buffer.getSize());
        byte[] result = new byte[3];
        buffer.peek(result);
        Assert.assertArrayEquals(new byte[] { 2, 3, 4 }, result);
    }

    @Test
    public final void add_overfillingInSecondCallDiscardsOldValues() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);
        buffer.add(new byte[] { 1, 2 });
        buffer.add(new byte[] { 3, 4 });
        Assert.assertEquals(3, buffer.getSize());
        byte[] result = new byte[3];
        Assert.assertEquals(3, buffer.peek(result));
        Assert.assertArrayEquals(new byte[] { 2, 3, 4 }, result);
    }

    @Test
    public final void add_overfilling3TimesTooMuchStoresLastValues() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);
        buffer.add(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        Assert.assertEquals(3, buffer.getSize());
        byte[] result = new byte[3];
        buffer.peek(result);
        Assert.assertArrayEquals(new byte[] { 7, 8, 9 }, result);
    }

    @Test
    public final void add_indexlength_addsExactSize() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);
        buffer.add(new byte[] { 1, 2, 3, 4 }, 1, 3);
        Assert.assertEquals(3, buffer.getSize());
        byte[] result = new byte[3];
        buffer.peek(result);
        Assert.assertArrayEquals(new byte[] { 2, 3, 4 }, result);
    }

    @Test
    public final void add_indexlength_addsHalf() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);
        buffer.add(new byte[] { 1, 2, 3, 4 }, 1, 2);
        Assert.assertEquals(2, buffer.getSize());
        byte[] result = new byte[2];
        buffer.peek(result);
        Assert.assertArrayEquals(new byte[] { 2, 3 }, result);
    }

    @Test
    public final void add_indexlength_overfills() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);
        buffer.add(new byte[] { 1, 2, 3, 4, 5, 6, 7 }, 1, 6);
        Assert.assertEquals(3, buffer.getSize());
        byte[] result = new byte[3];
        buffer.peek(result);
        Assert.assertArrayEquals(new byte[] { 5, 6, 7 }, result);
    }

    @Test
    public final void peek_bytebuffer_noargs_writesStartingFromPositionAndUpToItsCapacity() {
        ByteRingBuffer buffer = new ByteRingBuffer(5);
        buffer.add(new byte[] { 1, 2, 3 });
        Assert.assertEquals(3, buffer.getSize());
        ByteBuffer result = ByteBuffer.allocate(3);
        result.position(1);
        Assert.assertEquals(2, buffer.peek(result));
        Assert.assertEquals(1, result.position());
        Assert.assertArrayEquals(new byte[] { 0, 1, 2 }, result.array());
    }

    @Test
    public final void peek_bytebuffer_length_writesStartingFromPositionAndUpToLength() {
        ByteRingBuffer buffer = new ByteRingBuffer(5);
        buffer.add(new byte[] { 1, 2, 3 });
        Assert.assertEquals(3, buffer.getSize());
        ByteBuffer result = ByteBuffer.allocate(3);
        result.position(1);
        Assert.assertEquals(1, buffer.peek(result, 1));
        Assert.assertEquals(1, result.position());
        Assert.assertArrayEquals(new byte[] { 0, 1, 0 }, result.array());
    }

    @Test
    public final void peek_noargs_withoutPartition_readEntireBuffer() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);
        buffer.add(new byte[] { 1, 2, 3 });
        Assert.assertEquals(3, buffer.getSize());
        byte[] result = new byte[3];
        Assert.assertEquals(3, buffer.peek(result));
        Assert.assertArrayEquals(new byte[] { 1, 2, 3 }, result);
    }

    @Test
    public final void peek_noargs_withoutPartition_readHalf() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);
        buffer.add(new byte[] { 1, 2, 3, 4 });
        Assert.assertEquals(4, buffer.getSize());
        byte[] result = new byte[2];
        Assert.assertEquals(2, buffer.peek(result));
        Assert.assertArrayEquals(new byte[] { 1, 2 }, result);
    }

    @Test
    public final void peek_noargs_withoutPartition_readUpToSizeOfBuffer() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);
        buffer.add(new byte[] { 1, 2 });
        Assert.assertEquals(2, buffer.getSize());
        byte[] result = new byte[4];
        Assert.assertEquals(2, buffer.peek(result));
        Assert.assertArrayEquals(new byte[] { 1, 2, 0, 0 }, result);
    }

    @Test
    public final void peek_noargs_withPartition_readEntireBuffer() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);
        buffer.add(new byte[] { 1, 2, 3, 4, 5 });
        Assert.assertEquals(4, buffer.getSize());
        byte[] result = new byte[4];
        Assert.assertEquals(4, buffer.peek(result));
        Assert.assertArrayEquals(new byte[] { 2, 3, 4, 5 }, result);
    }

    @Test
    public final void peek_noargs_withPartition_readUpToArraySize() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);
        buffer.add(new byte[] { 1, 2, 3, 4, 5 });
        Assert.assertEquals(4, buffer.getSize());
        byte[] result = new byte[2];
        Assert.assertEquals(2, buffer.peek(result));
        Assert.assertArrayEquals(new byte[] { 2, 3 }, result);
    }

    @Test
    public final void peek_cb_callsFunctionIfBufferIsEmpty() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);
        final ArrayList calls = new ArrayList();
        buffer.peek(new ByteRingBuffer.PeekCallback() {
            @Override
            public void borrow(ByteBuffer chunk) {
                calls.add(chunk.remaining());
            }
        });
        Assert.assertEquals(1, calls.size());
        Assert.assertEquals(0, calls.get(0));
    }

    @Test
    public final void peek_cb_callsFunctionOnceWhenBufferIsContinuous() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);
        buffer.add(new byte[] { 1, 2, 3 });
        final ArrayList calls = new ArrayList();
        buffer.peek(new ByteRingBuffer.PeekCallback() {
            @Override
            public void borrow(ByteBuffer chunk) {
                byte[] arr = new byte[chunk.remaining()];
                chunk.get(arr);
                calls.add(arr);
            }
        });
        Assert.assertEquals(1, calls.size());
        Assert.assertArrayEquals(new byte[] { 1, 2, 3 }, (byte[]) calls.get(0));
    }

    @Test
    public final void peek_cb_callsFunctionOnceWhenBufferIsContinuousButDoesNotStartAtIndex0() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);
        buffer.add(new byte[] { 1, 2, 3 });
        buffer.drop(1);
        final ArrayList calls = new ArrayList();
        buffer.peek(new ByteRingBuffer.PeekCallback() {
            @Override
            public void borrow(ByteBuffer chunk) {
                byte[] arr = new byte[chunk.remaining()];
                chunk.get(arr);
                calls.add(arr);
            }
        });
        Assert.assertEquals(1, calls.size());
        Assert.assertArrayEquals(new byte[] { 2, 3 }, (byte[]) calls.get(0));
    }

    @Test
    public final void peek_cb_callsFunctionTwiceWhenBufferIsPartitioned() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);
        buffer.add(new byte[] { 1, 2, 3, 4, 5 });
        final ArrayList calls = new ArrayList();
        buffer.peek(new ByteRingBuffer.PeekCallback() {
            @Override
            public void borrow(ByteBuffer chunk) {
                byte[] arr = new byte[chunk.remaining()];
                chunk.get(arr);
                calls.add(arr);
            }
        });
        Assert.assertEquals(2, calls.size());
        Assert.assertArrayEquals(new byte[] { 2, 3, 4 }, (byte[]) calls.get(0));
        Assert.assertArrayEquals(new byte[] { 5 }, (byte[]) calls.get(1));
    }

    @Test
    public final void peek_noargs_withPartition_firstPartitionIsNotFilled() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);
        buffer.add(new byte[] { 1, 2, 3 });
        buffer.drop(2);
        Assert.assertEquals(1, buffer.getSize());
        byte[] result = new byte[4];
        Assert.assertEquals(1, buffer.peek(result));
        Assert.assertArrayEquals(new byte[] { 3, 0, 0, 0 }, result);
    }

    @Test
    public final void bugfix_peekCrashesAfterWrappingAroundTwiceAndALittleBitMore() {
        int rounds = 2;
        int payloadSize = 2;
        int total = rounds * payloadSize;
        ByteRingBuffer buffer = new ByteRingBuffer(total);
        int var5 = 0;

        for (int var6 = payloadSize * rounds + 1; var5 < var6; ++var5) {
            buffer.add(new byte[payloadSize]);
        }

        byte[] result = new byte[total];
        Assert.assertEquals(total, buffer.peek(result));
    }

    @Test
    public final void pop_noargs_withPartition_readEverything() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);
        buffer.add(new byte[] { 1, 2, 3, 4, 5 });
        Assert.assertEquals(4, buffer.getSize());
        byte[] result = new byte[4];
        Assert.assertEquals(4, buffer.pop(result));
        Assert.assertArrayEquals(new byte[] { 2, 3, 4, 5 }, result);
        Assert.assertEquals(0, buffer.getSize());
    }

    @Test
    public final void pop_noargs_withPartition_readHalf() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);
        buffer.add(new byte[] { 1, 2, 3, 4, 5 });
        Assert.assertEquals(4, buffer.getSize());
        byte[] result = new byte[2];
        Assert.assertEquals(2, buffer.pop(result));
        Assert.assertArrayEquals(new byte[] { 2, 3 }, result);
        Assert.assertEquals(2, buffer.getSize());
        Assert.assertEquals(2, buffer.peek(result));
        Assert.assertArrayEquals(new byte[] { 4, 5 }, result);
    }

    @Test
    public final void drop_continuousBuffer_firstElement() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);
        buffer.add(new byte[] { 1, 2, 3, 4 });
        buffer.drop(1);
        Assert.assertEquals(3, buffer.getSize());
        byte[] result = new byte[3];
        Assert.assertEquals(3, buffer.peek(result));
        Assert.assertArrayEquals(new byte[] { 2, 3, 4 }, result);
    }

    @Test
    public final void drop_continuousBuffer_bufferSize() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);
        buffer.add(new byte[] { 1, 2, 3, 4 });
        buffer.drop(4);
        Assert.assertEquals(0, buffer.getSize());
        byte[] result = new byte[4];
        Assert.assertEquals(0, buffer.peek(result));
    }

    @Test
    public final void drop_partitionedBuffer_allElementsFromFirstPartition() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);
        buffer.add(new byte[] { 1, 2, 3, 4, 5, 6 });
        buffer.drop(2);
        Assert.assertEquals(2, buffer.getSize());
        byte[] result = new byte[2];
        Assert.assertEquals(2, buffer.peek(result));
        Assert.assertArrayEquals(new byte[] { 5, 6 }, result);
    }

    @Test
    public final void drop_droppingMoreElementsThanItHasDoesNotCrashOrBreakInternalState() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);
        buffer.add(new byte[] { 1, 2 });
        buffer.drop(3);
        Assert.assertEquals(0, buffer.getSize());
    }

    @Test
    public final void clear_putsBufferInInitialState() {
        ByteRingBuffer buffer = new ByteRingBuffer(4);
        buffer.add(new byte[] { 1, 2, 3, 4, 5, 6 });
        buffer.clear();
        Assert.assertEquals(0, buffer.getSize());
        buffer.add(new byte[] { 1, 2, 3, 4 });
        final ArrayList calls = new ArrayList();
        buffer.peek(new ByteRingBuffer.PeekCallback() {
            @Override
            public void borrow(ByteBuffer chunk) {
                byte[] arr = new byte[chunk.remaining()];
                chunk.get(arr);
                calls.add(arr);
            }
        });
        Assert.assertEquals(1, calls.size());
        Assert.assertArrayEquals(new byte[] { 1, 2, 3, 4 }, (byte[]) calls.get(0));
    }

    @Test
    public final void size_initialSizeIs0() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);
        Assert.assertEquals(0, buffer.getSize());
    }

    @Test
    public final void size_fillingBufferWillReportTotalSize() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);
        buffer.add(new byte[] { 1, 2, 3 });
        Assert.assertEquals(3, buffer.getSize());
    }

    @Test
    public final void size_notFillingBufferWillNotReportTotalSize() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);
        buffer.add(new byte[] { 1 });
        Assert.assertEquals(1, buffer.getSize());
        buffer.add(new byte[] { 2 });
        Assert.assertEquals(2, buffer.getSize());
    }

    @Test
    public final void size_overfillingStillReprtsTotalSize() {
        ByteRingBuffer buffer = new ByteRingBuffer(3);
        buffer.add(new byte[] { 1, 2, 3 });
        buffer.add(new byte[] { 2 });
        Assert.assertEquals(3, buffer.getSize());
    }
}

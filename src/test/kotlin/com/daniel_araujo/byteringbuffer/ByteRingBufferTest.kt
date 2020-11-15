package com.daniel_araujo.byteringbuffer

import org.junit.Assert.*
import org.junit.Test
import java.nio.ByteBuffer

class ByteRingBufferTest {
    @Test
    fun add_exactSizeToFillBuffer() {
        val buffer = ByteRingBuffer(3)

        buffer.add(byteArrayOf(1, 2, 3))

        assertEquals(3, buffer.size)

        val result = ByteArray(3)
        buffer.peek(result)
        assertArrayEquals(byteArrayOf(1, 2, 3), result)
    }

    @Test
    fun add_callTwiceWithoutFilling() {
        val buffer = ByteRingBuffer(3)

        buffer.add(byteArrayOf(1, 2))
        buffer.add(byteArrayOf(3))

        assertEquals(3, buffer.size)

        val result = ByteArray(3)
        buffer.peek(result)
        assertArrayEquals(byteArrayOf(1, 2, 3), result)
    }

    @Test
    fun add_fillHalf() {
        val buffer = ByteRingBuffer(4)

        buffer.add(byteArrayOf(1, 2))

        assertEquals(2, buffer.size)

        val result = ByteArray(2)
        buffer.peek(result)
        assertArrayEquals(byteArrayOf(1, 2), result)
    }

    @Test
    fun add_overfillingInOneCallDiscardsOldValues() {
        val buffer = ByteRingBuffer(3)

        buffer.add(byteArrayOf(1, 2, 3, 4))

        assertEquals(3, buffer.size)

        val result = ByteArray(3)
        buffer.peek(result)
        assertArrayEquals(byteArrayOf(2, 3, 4), result)
    }

    @Test
    fun add_overfillingInSecondCallDiscardsOldValues() {
        val buffer = ByteRingBuffer(3)

        buffer.add(byteArrayOf(1, 2))
        buffer.add(byteArrayOf(3, 4))

        assertEquals(3, buffer.size)

        val result = ByteArray(3)
        assertEquals(3, buffer.peek(result))
        assertArrayEquals(byteArrayOf(2, 3, 4), result)
    }

    @Test
    fun add_overfilling3TimesTooMuchStoresLastValues() {
        val buffer = ByteRingBuffer(3)

        buffer.add(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9))

        assertEquals(3, buffer.size)

        val result = ByteArray(3)
        buffer.peek(result)
        assertArrayEquals(byteArrayOf(7, 8, 9), result)
    }

    @Test
    fun add_indexlength_addsExactSize() {
        val buffer = ByteRingBuffer(3)

        buffer.add(byteArrayOf(1, 2, 3, 4), 1, 3)

        assertEquals(3, buffer.size)

        val result = ByteArray(3)
        buffer.peek(result)
        assertArrayEquals(byteArrayOf(2, 3, 4), result)
    }

    @Test
    fun add_indexlength_addsHalf() {
        val buffer = ByteRingBuffer(4)

        buffer.add(byteArrayOf(1, 2, 3, 4), 1, 2)

        assertEquals(2, buffer.size)

        val result = ByteArray(2)
        buffer.peek(result)
        assertArrayEquals(byteArrayOf(2, 3), result)
    }

    @Test
    fun add_indexlength_overfills() {
        val buffer = ByteRingBuffer(3)

        buffer.add(byteArrayOf(1, 2, 3, 4, 5, 6, 7), 1, 6)

        assertEquals(3, buffer.size)

        val result = ByteArray(3)
        buffer.peek(result)
        assertArrayEquals(byteArrayOf(5, 6, 7), result)
    }

    @Test
    fun peek_bytebuffer_noargs_writesStartingFromPositionAndUpToItsCapacity() {
        val buffer = ByteRingBuffer(5)

        buffer.add(byteArrayOf(1, 2, 3))

        assertEquals(3, buffer.size)

        val result = ByteBuffer.allocate(3)
        result.position(1)
        assertEquals(2, buffer.peek(result))
        assertEquals(1, result.position())
        assertArrayEquals(byteArrayOf(0, 1, 2), result.array())
    }

    @Test
    fun peek_bytebuffer_length_writesStartingFromPositionAndUpToLength() {
        val buffer = ByteRingBuffer(5)

        buffer.add(byteArrayOf(1, 2, 3))

        assertEquals(3, buffer.size)

        val result = ByteBuffer.allocate(3)
        result.position(1)
        assertEquals(1, buffer.peek(result, 1))
        assertEquals(1, result.position())
        assertArrayEquals(byteArrayOf(0, 1, 0), result.array())
    }

    @Test
    fun peek_noargs_withoutPartition_readEntireBuffer() {
        val buffer = ByteRingBuffer(3)

        buffer.add(byteArrayOf(1, 2, 3))

        assertEquals(3, buffer.size)

        val result = ByteArray(3)
        assertEquals(3, buffer.peek(result))
        assertArrayEquals(byteArrayOf(1, 2, 3), result)
    }

    @Test
    fun peek_noargs_withoutPartition_readHalf() {
        val buffer = ByteRingBuffer(4)

        buffer.add(byteArrayOf(1, 2, 3, 4))

        assertEquals(4, buffer.size)

        val result = ByteArray(2)
        assertEquals(2, buffer.peek(result))
        assertArrayEquals(byteArrayOf(1, 2), result)
    }

    @Test
    fun peek_noargs_withoutPartition_readUpToSizeOfBuffer() {
        val buffer = ByteRingBuffer(4)

        buffer.add(byteArrayOf(1, 2))

        assertEquals(2, buffer.size)

        val result = ByteArray(4)
        assertEquals(2, buffer.peek(result))
        assertArrayEquals(byteArrayOf(1, 2, 0, 0), result)
    }

    @Test
    fun peek_noargs_withPartition_readEntireBuffer() {
        val buffer = ByteRingBuffer(4)

        buffer.add(byteArrayOf(1, 2, 3, 4, 5))

        assertEquals(4, buffer.size)

        val result = ByteArray(4)
        assertEquals(4, buffer.peek(result))
        assertArrayEquals(byteArrayOf(2, 3, 4, 5), result)
    }

    @Test
    fun peek_noargs_withPartition_readUpToArraySize() {
        val buffer = ByteRingBuffer(4)

        buffer.add(byteArrayOf(1, 2, 3, 4, 5))

        assertEquals(4, buffer.size)

        val result = ByteArray(2)
        assertEquals(2, buffer.peek(result))
        assertArrayEquals(byteArrayOf(2, 3), result)
    }

    @Test
    fun peek_cb_callsFunctionIfBufferIsEmpty() {
        val buffer = ByteRingBuffer(4)

        val calls: ArrayList<Int> = ArrayList()

        buffer.peek {
            calls.add(it.remaining())
        }

        assertEquals(1, calls.size)
        assertEquals(0, calls[0])
    }

    @Test
    fun peek_cb_callsFunctionOnceWhenBufferIsContinuous() {
        val buffer = ByteRingBuffer(4)

        buffer.add(byteArrayOf(1, 2, 3))

        val calls: ArrayList<ByteArray> = ArrayList()

        buffer.peek {
            val arr = ByteArray(it.remaining())
            it.get(arr)
            calls.add(arr)
        }

        assertEquals(1, calls.size)
        assertArrayEquals(byteArrayOf(1, 2, 3), calls[0])
    }

    @Test
    fun peek_cb_callsFunctionOnceWhenBufferIsContinuousButDoesNotStartAtIndex0() {
        val buffer = ByteRingBuffer(4)

        buffer.add(byteArrayOf(1, 2, 3))
        buffer.drop(1)

        val calls: ArrayList<ByteArray> = ArrayList()

        buffer.peek {
            val arr = ByteArray(it.remaining())
            it.get(arr)
            calls.add(arr)
        }

        assertEquals(1, calls.size)
        assertArrayEquals(byteArrayOf(2, 3), calls[0])
    }

    @Test
    fun peek_cb_callsFunctionTwiceWhenBufferIsPartitioned() {
        val buffer = ByteRingBuffer(4)

        buffer.add(byteArrayOf(1, 2, 3, 4, 5))

        val calls: ArrayList<ByteArray> = ArrayList()

        buffer.peek {
            val arr = ByteArray(it.remaining())
            it.get(arr)
            calls.add(arr)
        }

        assertEquals(2, calls.size)
        assertArrayEquals(byteArrayOf(2, 3, 4), calls[0])
        assertArrayEquals(byteArrayOf(5), calls[1])
    }

    @Test
    fun peek_noargs_withPartition_firstPartitionIsNotFilled() {
        // Some math bugs could show up.
        val buffer = ByteRingBuffer(4)

        buffer.add(byteArrayOf(1, 2, 3))
        buffer.drop(2);

        assertEquals(1, buffer.size)

        val result = ByteArray(4)
        assertEquals(1, buffer.peek(result))
        assertArrayEquals(byteArrayOf(3, 0, 0, 0), result)
    }

    @Test
    fun bugfix_peekCrashesAfterWrappingAroundTwiceAndALittleBitMore() {
        // The start position would be set incorrectly after wrapping around circular buffer twice.
        val rounds = 2
        val payloadSize = 2
        val total = rounds * payloadSize

        val buffer = ByteRingBuffer(total)

        for (x in 0 until payloadSize * rounds + 1) {
            buffer.add(ByteArray(payloadSize))
        }

        val result = ByteArray(total)
        assertEquals(total, buffer.peek(result))
    }

    @Test
    fun pop_noargs_withPartition_readEverything() {
        val buffer = ByteRingBuffer(4)

        buffer.add(byteArrayOf(1, 2, 3, 4, 5))

        assertEquals(4, buffer.size)

        val result = ByteArray(4)
        assertEquals(4, buffer.pop(result))
        assertArrayEquals(byteArrayOf(2, 3, 4, 5), result)
        // Checking what's left in the buffer.
        assertEquals(0, buffer.size)
    }

    @Test
    fun pop_noargs_withPartition_readHalf() {
        val buffer = ByteRingBuffer(4)

        buffer.add(byteArrayOf(1, 2, 3, 4, 5))

        assertEquals(4, buffer.size)

        val result = ByteArray(2)
        assertEquals(2, buffer.pop(result))
        assertArrayEquals(byteArrayOf(2, 3), result)
        // Checking what's left in the buffer.
        assertEquals(2, buffer.size)
        assertEquals(2, buffer.peek(result))
        assertArrayEquals(byteArrayOf(4, 5), result)
    }

    @Test
    fun drop_continuousBuffer_firstElement() {
        // Some math bugs could show up.
        val buffer = ByteRingBuffer(4)

        buffer.add(byteArrayOf(1, 2, 3, 4))
        buffer.drop(1);

        assertEquals(3, buffer.size)

        val result = ByteArray(3)
        assertEquals(3, buffer.peek(result))
        assertArrayEquals(byteArrayOf(2, 3, 4), result)
    }

    @Test
    fun drop_continuousBuffer_bufferSize() {
        // Some math bugs could show up.
        val buffer = ByteRingBuffer(4)

        buffer.add(byteArrayOf(1, 2, 3, 4))
        buffer.drop(4);

        assertEquals(0, buffer.size)

        val result = ByteArray(4)
        assertEquals(0, buffer.peek(result))
    }

    @Test
    fun drop_partitionedBuffer_allElementsFromFirstPartition() {
        // Some math bugs could show up.
        val buffer = ByteRingBuffer(4)

        buffer.add(byteArrayOf(1, 2, 3, 4, 5, 6))
        buffer.drop(2);

        assertEquals(2, buffer.size)

        val result = ByteArray(2)
        assertEquals(2, buffer.peek(result))
        assertArrayEquals(byteArrayOf(5, 6), result)
    }

    @Test
    fun drop_droppingMoreElementsThanItHasDoesNotCrashOrBreakInternalState() {
        val buffer = ByteRingBuffer(4)

        buffer.add(byteArrayOf(1, 2))
        buffer.drop(3);

        // There was a bug that set size to -1.
        assertEquals(0, buffer.size)
    }

    @Test
    fun clear_putsBufferInInitialState() {
        val buffer = ByteRingBuffer(4)

        // Now it becomes partitioned.
        buffer.add(byteArrayOf(1, 2, 3, 4, 5, 6))

        buffer.clear()

        assertEquals(0, buffer.size)

        // Now we check whether the start position went back to 0.
        buffer.add(byteArrayOf(1, 2, 3, 4))

        val calls: ArrayList<ByteArray> = ArrayList()

        buffer.peek {
            val arr = ByteArray(it.remaining())
            it.get(arr)
            calls.add(arr)
        }

        assertEquals(1, calls.size)
        assertArrayEquals(byteArrayOf(1, 2, 3, 4), calls[0])
    }

    @Test
    fun size_initialSizeIs0() {
        val buffer = ByteRingBuffer(3)

        assertEquals(0, buffer.size)
    }

    @Test
    fun size_fillingBufferWillReportTotalSize() {
        val buffer = ByteRingBuffer(3)

        buffer.add(byteArrayOf(1, 2, 3))

        assertEquals(3, buffer.size)
    }

    @Test
    fun size_notFillingBufferWillNotReportTotalSize() {
        val buffer = ByteRingBuffer(3)

        buffer.add(byteArrayOf(1))

        assertEquals(1, buffer.size)

        buffer.add(byteArrayOf(2))

        assertEquals(2, buffer.size)
    }

    @Test
    fun size_overfillingStillReprtsTotalSize() {
        val buffer = ByteRingBuffer(3)

        buffer.add(byteArrayOf(1, 2, 3))

        buffer.add(byteArrayOf(2))

        assertEquals(3, buffer.size)
    }
}
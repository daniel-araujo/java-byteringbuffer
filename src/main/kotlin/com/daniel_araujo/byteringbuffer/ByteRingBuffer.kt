package com.daniel_araujo.byteringbuffer

import java.nio.ByteBuffer

/**
 * My own implementation of a ring buffer backed by a byte array.
 */
class ByteRingBuffer {
    /**
     * Byte array used as storage.
     */
    private val buffer: ByteArray

    /**
     * The start position in the buffer.
     */
    private var start = 0

    /**
     * How much data has been used.
     */
    var size = 0
        private set

    constructor(size: Int) {
        buffer = ByteArray(size)
    }

    /**
     * Adds bytes to buffer.
     */
    fun add(bytes: ByteArray) {
        add(bytes, 0, bytes.size)
    }

    /**
     * Adds bytes to buffer.
     */
    fun add(bytes: ByteArray, index: Int, length: Int) {
        // Slow route.
        var bytesRemaining = length
        var bytesOffset = index;

        if (!overflows(bytesRemaining)) {
            // Fast route.
            System.arraycopy(bytes, bytesOffset, buffer, offset(), bytesRemaining)
            advance(bytesRemaining)
        } else {
            do {
                // How much space is available starting from offset.
                val offset = offset()
                var available = buffer.size - offset
                // How many will be copied now.
                var copying = Math.min(bytesRemaining, available)

                System.arraycopy(bytes, bytesOffset, buffer, offset, copying)

                advance(copying)
                bytesRemaining -= copying
                bytesOffset += copying
            } while (bytesRemaining > 0)
        }
    }

    fun peek(bytes: ByteArray): Int {
        return peek(bytes, 0, bytes.size)
    }

    /**
     * Reads data from the buffer. Does not remove data from buffer.
     */
    fun peek(bytes: ByteArray, index: Int, length: Int): Int {
        if (size == 0) {
            // Can't do anything.
            return 0
        }

        // How much data to read from the buffer.
        var toIndex = index
        var toRead = Math.min(length, size)

        // Split buffer in two halves.
        val offset = offset();
        var firstHalfStart = start
        var firstHalfSize = buffer.size - start

        if (firstHalfSize > size) {
            // Can't read what's not there. The first partition is not entirely filled up.
            firstHalfSize = size;
        }

        if (firstHalfSize >= toRead) {
            // The function received an array that is less than the size of the first half of the
            // circular buffer. We will only read what we can from the first half. This also means
            // that we won't have to read the second half later on.
            firstHalfSize = toRead
        }

        // First half.
        System.arraycopy(buffer, firstHalfStart, bytes, toIndex, firstHalfSize)

        // Advance.
        toIndex += firstHalfSize;
        toRead -= firstHalfSize;

        if (toRead == 0) {
            // Done.
            return firstHalfSize;
        }

        var secondHalfStart = if (offset <= start) 0 else size
        var secondHalfSize = buffer.size - firstHalfSize;

        if (secondHalfSize >= toRead) {
            // Looks like we won't fill the array up.
            secondHalfSize = toRead
        }

        // Second half.
        System.arraycopy(buffer, secondHalfStart, bytes, index + firstHalfSize, secondHalfSize)

        toIndex += secondHalfSize;
        toRead -= secondHalfSize;

        return length - toRead;
    }

    /**
     * Uses a ByteBuffer that must be backed by an array that is directly accessible.
     */
    fun peek(byteBuffer: ByteBuffer): Int {
        val length = byteBuffer.limit() - byteBuffer.position()

        return peek(byteBuffer, length)
    }

    /**
     * Uses a ByteBuffer that must be backed by an array that is directly accessible.
     */
    fun peek(byteBuffer: ByteBuffer, length: Int): Int {
        val array = byteBuffer.array()

        val index = byteBuffer.arrayOffset() + byteBuffer.position()

        return peek(array, index, length)
    }

    /**
     * The most space efficient method for peeking into the contents of the buffer. The callback
     * function will be called once if the buffer is continuous or twice if the buffer is
     * partitioned. The ByteBuffer is valid for as long as the callback runs.
     */
    fun peek(cb: (ByteBuffer) -> Unit) {
        var firstHalfStart = start
        var firstHalfSize = buffer.size - start

        if (firstHalfSize > size) {
            // Can't read what's not there.
            firstHalfSize = size;
        }

        val first = ByteBuffer.wrap(buffer, firstHalfStart, firstHalfSize)
        cb(first)

        if (firstHalfSize == size) {
            // Done.
            return
        }

        var secondHalfStart = 0
        var secondHalfSize = size - firstHalfSize;

        val second = ByteBuffer.wrap(buffer, secondHalfStart, secondHalfSize)
        cb(second)
    }

    /**
     * Reads data from the buffer and removes it.
     */
    fun pop(bytes: ByteArray): Int {
        return pop(bytes, 0, bytes.size)
    }

    /**
     * Reads data from the buffer and removes it.
     */
    fun pop(bytes: ByteArray, index: Int, length: Int): Int {
        val read = peek(bytes, index, length)

        drop(read)

        return read
    }

    /**
     * Drop elements from the start.
     */
    fun drop(elements: Int) {
        var toDrop = elements

        if (toDrop > size) {
            toDrop = size
        }

        val newSize = size - toDrop
        val diff = size - newSize

        start = (start + diff) % buffer.size
        size = newSize
    }

    /**
     * Clears contents in buffer.
     */
    fun clear() {
        start = 0
        size = 0
    }

    /**
     * Verifies if the buffer has enough space to append given number of bytes to it with
     * System.arraycopy.
     */
    private fun overflows(size: Int): Boolean {
        return (offset() + size) > buffer.size
    }

    /**
     * Advances current position based off number of elements that were supposedly inserted into
     * the buffer. Wraps around automatically.
     */
    private fun advance(elements: Int) {
        val sumSize = size + elements

        var overflow = 0

        if (sumSize > buffer.size) {
            size = buffer.size
            overflow = sumSize - buffer.size;
        } else {
            size = sumSize;
        }

        // Quite important to not use the += operator when doing a Modulo operation like this.
        start = (start + overflow) % buffer.size;
    }

    /**
     * Returns index where next elements should be placed at.
     */
    private fun offset(): Int {
        return (start + size) % buffer.size;
    }
}
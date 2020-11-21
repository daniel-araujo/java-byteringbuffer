package com.daniel_araujo.byteringbuffer;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * A ring buffer backed by a byte array. Very memory efficient.
 */
public final class ByteRingBuffer {
    /**
     * Elements are stored here as bytes.
     */
    private final byte[] buffer;

    /**
     * The index of the first element in buffer.
     */
    private int start;

    /**
     * Number of used bytes in buffer,
     */
    private int size;

    /**
     * Creates a ring buffer that can store up to the given number of bytes.
     *
     * @param capacity How many bytes can be stored in the buffer.
     */
    public ByteRingBuffer(int capacity) {
        buffer = new byte[capacity];
    }

    public final int getSize() {
        return size;
    }

    public final void add(byte[] bytes) {
        add(bytes, 0, bytes.length);
    }

    public final void add(byte[] bytes, int index, int length) {
        Objects.requireNonNull(bytes);

        int bytesRemaining = length;
        int bytesOffset = index;
        if (!overflows(length)) {
            System.arraycopy(bytes, index, buffer, offset(), length);
            advance(length);
        } else {
            do {
                int offset = offset();
                int available = buffer.length - offset;
                int copying = Math.min(bytesRemaining, available);
                System.arraycopy(bytes, bytesOffset, buffer, offset, copying);
                advance(copying);
                bytesRemaining -= copying;
                bytesOffset += copying;
            } while (bytesRemaining > 0);
        }
    }

    public final int peek(byte[] bytes) {
        return peek(bytes, 0, bytes.length);
    }

    public final int peek(byte[] bytes, int index, int length) {
        if (size == 0) {
            return 0;
        } else {
            int toRead = Math.min(length, size);
            int offset = offset();
            int firstHalfStart = start;
            int firstHalfSize = buffer.length - start;
            if (firstHalfSize > size) {
                firstHalfSize = size;
            }

            if (firstHalfSize >= toRead) {
                firstHalfSize = toRead;
            }

            System.arraycopy(buffer, firstHalfStart, bytes, index, firstHalfSize);
            int toIndex = index + firstHalfSize;
            toRead -= firstHalfSize;
            if (toRead == 0) {
                return firstHalfSize;
            } else {
                int secondHalfStart = offset <= start ? 0 : size;
                int secondHalfSize = buffer.length - firstHalfSize;
                if (secondHalfSize >= toRead) {
                    secondHalfSize = toRead;
                }

                System.arraycopy(buffer, secondHalfStart, bytes, index + firstHalfSize, secondHalfSize);
                int var10000 = toIndex + secondHalfSize;
                toRead -= secondHalfSize;
                return length - toRead;
            }
        }
    }

    public final int peek(ByteBuffer byteBuffer) {
        int length = byteBuffer.limit() - byteBuffer.position();
        return peek(byteBuffer, length);
    }

    public final int peek(ByteBuffer byteBuffer, int length) {
        byte[] array = byteBuffer.array();
        int index = byteBuffer.arrayOffset() + byteBuffer.position();
        return peek(array, index, length);
    }

    public final void peek(PeekCallback cb) {
        int firstHalfStart = start;
        int firstHalfSize = buffer.length - start;
        if (firstHalfSize > size) {
            firstHalfSize = size;
        }

        ByteBuffer first = ByteBuffer.wrap(buffer, firstHalfStart, firstHalfSize);
        cb.borrow(first);
        if (firstHalfSize != size) {
            int secondHalfStart = 0;
            int secondHalfSize = size - firstHalfSize;
            ByteBuffer second = ByteBuffer.wrap(buffer, secondHalfStart, secondHalfSize);
            cb.borrow(second);
        }
    }

    public final int pop(byte[] bytes) {
        return pop(bytes, 0, bytes.length);
    }

    public final int pop(byte[] bytes, int index, int length) {
        int read = peek(bytes, index, length);
        drop(read);
        return read;
    }

    public final void drop(int elements) {
        int toDrop = elements;
        if (elements > size) {
            toDrop = size;
        }

        int newSize = size - toDrop;
        int diff = size - newSize;
        start = (start + diff) % buffer.length;
        size = newSize;
    }

    public final void clear() {
        start = 0;
        size = 0;
    }

    private final boolean overflows(int size) {
        return offset() + size > buffer.length;
    }

    private final void advance(int elements) {
        int sumSize = size + elements;
        int overflow = 0;
        if (sumSize > buffer.length) {
            size = buffer.length;
            overflow = sumSize - buffer.length;
        } else {
            size = sumSize;
        }

        start = (start + overflow) % buffer.length;
    }

    private final int offset() {
        return (start + size) % buffer.length;
    }

    public interface PeekCallback {
        void borrow(ByteBuffer chunk);
    }
}

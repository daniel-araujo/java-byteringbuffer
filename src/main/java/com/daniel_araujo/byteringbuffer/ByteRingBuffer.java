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
     * @param capacity
     *            How many bytes can be stored in the buffer.
     */
    public ByteRingBuffer(int capacity) {
        buffer = new byte[capacity];
    }

    /**
     * @return How many bytes are stored in the buffer.
     */
    public final int sizeUsed() {
        return size;
    }

    /**
     * @return How many bytes can be stored in the buffer in total.
     */
    public final int sizeTotal() {
        return buffer.length;
    }

    /**
     * @return How many bytes are free in the buffer.
     */
    public final int sizeFree() {
        return sizeTotal() - sizeUsed();
    }

    /**
     * Adds elements to the end of the buffer.
     *
     * @param bytes
     *            Adds entire array to buffer.
     */
    public final int add(byte[] bytes) {
        Objects.requireNonNull(bytes);

        return add(bytes, 0, bytes.length);
    }

    /**
     * Adds elements to the end of the buffer.
     *
     * @param bytes
     *            Array that contains elements to be added.
     * @param index
     *            Where to begin extracting elements.
     */
    public final int add(byte[] bytes, int index) {
        Objects.requireNonNull(bytes);

        return add(bytes, index, bytes.length - index);
    }

    /**
     * Adds elements to the end of the buffer.
     *
     * @param bytes
     *            Array that contains elements to be added.
     * @param index
     *            Where to begin extracting elements.
     * @param length
     *            How many elements to extract.
     */
    public final int add(byte[] bytes, int index, int length) {
        Objects.requireNonNull(bytes);

        int bytesRemaining = length;
        int bytesOffset = index;

        do {
            int offset = nextOffset();
            int available = availableAfter(offset);

            if (available == 0) {
                // Buffer is full.
                break;
            }

            int copying = Math.min(bytesRemaining, available);

            System.arraycopy(bytes, bytesOffset, buffer, offset, copying);

            advance(copying);
            bytesRemaining -= copying;
            bytesOffset += copying;
        } while (bytesRemaining > 0);

        return length - bytesRemaining;
    }

    /**
     * This version of the add method will overrun. This means that if the buffer is full then the oldest elements will
     * be overwritten by the newest ones.
     *
     * @param bytes
     *            Adds entire array to buffer.
     */
    public final void overrunAdd(byte[] bytes) {
        Objects.requireNonNull(bytes);

        overrunAdd(bytes, 0, bytes.length);
    }

    /**
     * This version of the add method will overrun. This means that if the buffer is full then the oldest elements will
     * be overwritten by the newest ones.
     *
     * @param bytes
     *            Array that contains elements to be added.
     * @param index
     *            Where to begin extracting elements.
     */
    public final void overrunAdd(byte[] bytes, int index) {
        Objects.requireNonNull(bytes);

        overrunAdd(bytes, index, bytes.length - index);
    }

    /**
     * This version of the add method will overrun. This means that if the buffer is full then the oldest elements will
     * be overwritten by the newest ones.
     *
     * @param bytes
     *            Array that contains elements to be added.
     * @param index
     *            Where to begin extracting elements.
     * @param length
     *            How many elements to extract.
     */
    public final void overrunAdd(byte[] bytes, int index, int length) {
        Objects.requireNonNull(bytes);

        int bytesRemaining = length;
        int bytesOffset = index;
        if (!overflows(length)) {
            System.arraycopy(bytes, index, buffer, nextOffset(), length);
            advance(length);
        } else {
            do {
                int offset = nextOffset();
                int available = buffer.length - offset;
                int copying = Math.min(bytesRemaining, available);
                System.arraycopy(bytes, bytesOffset, buffer, offset, copying);
                advance(copying);
                bytesRemaining -= copying;
                bytesOffset += copying;
            } while (bytesRemaining > 0);
        }
    }

    /**
     * Retrieves elements from the buffer and stores them in another array.
     *
     * @param bytes
     *            Where elements will be stored. The size of the array indicates how many elements will be retrieved.
     * 
     * @return Number of elements that were copied.
     */
    public final int peek(byte[] bytes) {
        Objects.requireNonNull(bytes);

        return peek(bytes, 0, bytes.length);
    }

    /**
     * Retrieves elements from the buffer and stores them in another array.
     *
     * @param bytes
     *            Where elements will be stored.
     * @param index
     *            Where to start placing elements in the given array.
     * @param length
     *            How many elements to copy.
     * 
     * @return Number of elements that were copied.
     */
    public final int peek(byte[] bytes, int index, int length) {
        Objects.requireNonNull(bytes);

        if (size == 0) {
            return 0;
        } else {
            int toRead = Math.min(length, size);
            int offset = nextOffset();
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

    /**
     * Retrieves elements from the buffer and places them in a ByteBuffer.
     *
     * @param byteBuffer
     *            Destination. The remaining size of the buffer indicates how many elements to retrieved.
     *
     * @return Number of elements placed into the given ByteBuffer object.
     */
    public final int peek(ByteBuffer byteBuffer) {
        int length = byteBuffer.limit() - byteBuffer.position();
        return peek(byteBuffer, length);
    }

    /**
     * Retrieves elements from the buffer and places them in a ByteBuffer.
     *
     * @param byteBuffer
     *            Destination.
     * @param length
     *            How many elements to retrieve.
     * 
     * @return Number of elements placed into the given ByteBuffer object.
     */
    public final int peek(ByteBuffer byteBuffer, int length) {
        byte[] array = byteBuffer.array();
        int index = byteBuffer.arrayOffset() + byteBuffer.position();
        return peek(array, index, length);
    }

    /**
     * Retrieves elements from the buffer with indirect access.
     *
     * @param cb
     *            The borrow method will be called at least once. It will be called if the buffer is empty.
     */
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

    /**
     * Moves elements from the buffer to the given array.
     * 
     * @param bytes
     *            The destination array. Its size determines how many elements to remove.
     * 
     * @return Number of elements removed. It may be less than the size of the given array if the buffer does not have
     *         enough elements to fill the array.
     */
    public final int pop(byte[] bytes) {
        return pop(bytes, 0, bytes.length);
    }

    /**
     * Moves elements from the buffer to the given array.
     *
     * @param bytes
     *            The destination array. Its size determines how many elements to remove.
     * @param index
     *            Where to begin placing elements in the array.
     *
     * @return Number of elements removed.
     */
    public final int pop(byte[] bytes, int index) {
        return pop(bytes, index, bytes.length - index);
    }

    /**
     * Moves elements from the buffer to the given array.
     *
     * @param bytes
     *            The destination array.
     * @param index
     *            Where to begin placing elements in the array.
     * @param length
     *            How many elements to remove.
     * 
     * @return Number of elements removed. It may be less than the size of the provided length if the buffer does not
     *         have enough elements to fill the array.
     */
    public final int pop(byte[] bytes, int index, int length) {
        int read = peek(bytes, index, length);
        drop(read);
        return read;
    }

    /**
     * Removes elements from the buffer.
     *
     * @param elements
     *            Number of elements to remove.
     */
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

    /**
     * Removes every element from the buffer. The buffer will go back to its initial state.
     */
    public final void clear() {
        start = 0;
        size = 0;
    }

    /**
     * Creates a view of this byte buffer that allows you to access its elements as shorts.
     */
    public final ShortView shortView() {
        return new ShortView();
    }

    /**
     * Checks if its possible to access the given number of bytes without passing the end of the buffer.
     * 
     * @param size
     *            Number of elements.
     * 
     * @return
     */
    private final boolean overflows(int size) {
        return nextOffset() + size > buffer.length;
    }

    /**
     * Moves end position and updates size and start position. Wraps around when neeeded.
     *
     * @param elements
     *            How many elements to move.
     */
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

    /**
     * @return The position from where to start placing new elements into the buffer.
     */
    private final int nextOffset() {
        return (start + size) % buffer.length;
    }

    /**
     * @param offset
     *            Position in the buffer.
     * 
     * @return How many bytes are free after the given position up to end the of the array or the start position,
     *         whichever comes first.
     */
    private final int availableAfter(int offset) {
        if (start >= offset) {
            if (size > 0) {
                // The beginning of the ring buffer is in front of us.
                return offset - start;
            } else {
                return buffer.length - offset;
            }
        } else {
            // The beginning of the ring buffer is behind us. We have everything in front of us
            // available.
            return buffer.length - offset;
        }
    }

    public class ShortView {
        /**
         * Adds elements to the end of the buffer.
         *
         * @param shorts
         *            Array that contains elements to be added. Will attempt to add them all.
         * 
         * @return How many elements were added.
         */
        public final int add(short[] shorts) {
            Objects.requireNonNull(shorts);

            return add(shorts, 0, shorts.length);
        }

        /**
         * Adds elements to the end of the buffer.
         *
         * @param shorts
         *            Array that contains elements to be added.
         * @param index
         *            Where to start copying elements from the array.
         * 
         * @return How many elements were added.
         */
        public final int add(short[] shorts, int index) {
            Objects.requireNonNull(shorts);

            return add(shorts, index, shorts.length - index);
        }

        /**
         * Adds elements to the end of the buffer.
         *
         * @param shorts
         *            Array that contains elements to be added.
         * @param index
         *            Where to start copying elements from the array.
         * @param length
         *            How many elements to copy.
         * 
         * @return How many elements were added.
         */
        public final int add(short[] shorts, int index, int length) {
            // TODO: This is experimental. Memory usage needs to be improved later.

            Objects.requireNonNull(shorts);

            int available = sizeFree();

            if (length > available) {
                length = available;
            }

            java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(length * 2);
            bb.asShortBuffer().put(shorts, index, length);

            return ByteRingBuffer.this.add(bb.array(), bb.arrayOffset(), bb.limit()) / 2;
        }

        /**
         * Retrieves elements from the buffer and places them in a short array.
         *
         * @param shorts
         *            Array of shorts. Will try to fill array.
         * 
         * @return How many elements were retrieved.
         */
        public final int peek(short[] shorts) {
            Objects.requireNonNull(shorts);

            return peek(shorts, 0, shorts.length);
        }

        /**
         * Retrieves elements from the buffer and places them in a short array.
         *
         * @param shorts
         *            Array of shorts. Will try to fill array.
         * @param index
         *            Index where elements will be placed.
         * 
         * @return How many elements were retrieved.
         */
        public final int peek(short[] shorts, int index) {
            Objects.requireNonNull(shorts);

            return peek(shorts, index, shorts.length - index);
        }

        /**
         * Retrieves elements from the buffer and places them in a short array.
         *
         * @param shorts
         *            Array of shorts.
         * @param index
         *            Index where elements will be placed.
         * @param length
         *            How many elements to retrieve.
         * 
         * @return How many elements were retrieved.
         */
        public final int peek(short[] shorts, int index, int length) {
            // TODO: This is experimental. Memory usage needs to be improved later.

            Objects.requireNonNull(shorts);

            int available = sizeUsed();

            if (length > available) {
                length = available;
            }

            java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(length * 2);

            int read = ByteRingBuffer.this.peek(bb.array(), bb.arrayOffset() + bb.position(), bb.limit());

            bb.asShortBuffer().get(shorts, index, length);

            return read / 2;
        }


        /**
         * Removes elements from the buffer.
         *
         * @param shorts Array that will contain removed elements. Its length determines how many elements will be removed.
         * @return Number of removed elements.
         */
        public final int pop(short[] shorts) {
            return pop(shorts, 0, shorts.length);
        }

        /**
         * Removes elements from the buffer.
         *
         * @param shorts Array that will contain removed elements. Its length determines how many elements will be removed.
         * @param index Index where elements will be placed.
         * @return Number of removed elements.
         */
        public final int pop(short[] shorts, int index) {
            return pop(shorts, index, shorts.length - index);
        }

        /**
         * Removes elements from the buffer.
         *
         * @param shorts
         *            Array that will contain removed elements.
         * @param index
         *            Index where elements will be placed.
         * @param length
         *            How many elements to remove.
         * @return
         */
        public final int pop(short[] shorts, int index, int length) {
            // TODO: This is experimental. Memory usage needs to be improved later.

            Objects.requireNonNull(shorts);

            int available = sizeUsed();

            if (length > available) {
                length = available;
            }

            java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(length * 2);

            int read = ByteRingBuffer.this.pop(bb.array(), bb.arrayOffset() + bb.position(), bb.limit());

            bb.asShortBuffer().get(shorts, index, length);

            return read / 2;
        }

        /**
         * @return How many complete shorts are stored in the buffer.
         */
        public final int sizeUsed() {
            return ByteRingBuffer.this.sizeUsed() / 2;
        }

        /**
         * @return How many complete shorts can be stored in the buffer in total.
         */
        public final int sizeTotal() {
            return ByteRingBuffer.this.sizeTotal() / 2;
        }

        /**
         * @return How many complete shorts are free in the buffer.
         */
        public final int sizeFree() {
            return ByteRingBuffer.this.sizeFree() / 2;
        }
    }

    /**
     * For efficient access to elements in the buffer.
     */
    public interface PeekCallback {
        /**
         * Receives a chunk of elements. This method can be called several times. You are not allowed to modify the
         * buffer.
         * 
         * @param chunk
         *            A chunk of elements. You can only use this object while the method is running.
         */
        void borrow(ByteBuffer chunk);
    }
}

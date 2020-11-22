package com.daniel_araujo.byteringbuffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PeekCallbackTracker implements ByteRingBuffer.PeekCallback {
    public final List<byte[]> calls = new ArrayList<>();

    @Override
    public void borrow(ByteBuffer chunk) {
        byte[] arr = new byte[chunk.remaining()];
        chunk.get(arr);

        calls.add(arr);
    }
}

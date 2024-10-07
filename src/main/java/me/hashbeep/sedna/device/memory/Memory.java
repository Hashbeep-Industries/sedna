package me.hashbeep.sedna.device.memory;

import me.hashbeep.sedna.api.device.PhysicalMemory;

import java.nio.ByteOrder;

public final class Memory {
    public static PhysicalMemory create(final int sizeInBytes) {
        if (ByteOrder.nativeOrder() != ByteOrder.LITTLE_ENDIAN) {
            return new ByteBufferMemory(sizeInBytes);
        } else {
            return UnsafeMemory.create(sizeInBytes);
        }
    }
}

package me.hashbeep.sedna.api.memory;

import me.hashbeep.sedna.api.device.MemoryMappedDevice;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Function;

public interface MemoryRangeAllocationStrategy {
    OptionalLong findMemoryRange(MemoryMappedDevice device, Function<MemoryRange, Optional<? extends MemoryRange>> intersectProvider);

    OptionalLong findMemoryRange(MemoryMappedDevice device, Function<MemoryRange, Optional<? extends MemoryRange>> intersectProvider, long start);

    static Function<MemoryRange, Optional<? extends MemoryRange>> getMemoryMapIntersectionProvider(final MemoryMap memoryMap) {
        return memoryMap::getMemoryRange;
    }
}

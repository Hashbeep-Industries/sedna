package me.hashbeep.sedna.api.devicetree;

import me.hashbeep.sedna.api.device.Device;
import me.hashbeep.sedna.api.memory.MemoryMap;

import java.util.Optional;

public interface DeviceTreeProvider {
    default Optional<String> getName(final Device device) {
        return Optional.empty();
    }

    default Optional<DeviceTree> createNode(final DeviceTree root, final MemoryMap memoryMap, final Device device, final String deviceName) {
        return Optional.empty();
    }

    void visit(final DeviceTree node, final MemoryMap memoryMap, final Device device);
}

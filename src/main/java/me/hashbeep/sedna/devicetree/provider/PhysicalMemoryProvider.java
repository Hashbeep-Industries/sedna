package me.hashbeep.sedna.devicetree.provider;

import me.hashbeep.sedna.api.device.Device;
import me.hashbeep.sedna.api.devicetree.DeviceNames;
import me.hashbeep.sedna.api.devicetree.DevicePropertyNames;
import me.hashbeep.sedna.api.devicetree.DeviceTree;
import me.hashbeep.sedna.api.devicetree.DeviceTreeProvider;
import me.hashbeep.sedna.api.memory.MemoryMap;

import java.util.Optional;

public class PhysicalMemoryProvider implements DeviceTreeProvider {
    @Override
    public Optional<String> getName(final Device device) {
        return Optional.of(DeviceNames.MEMORY);
    }

    @Override
    public void visit(final DeviceTree node, final MemoryMap memoryMap, final Device device) {
        node.addProp(DevicePropertyNames.DEVICE_TYPE, DeviceNames.MEMORY);
    }
}

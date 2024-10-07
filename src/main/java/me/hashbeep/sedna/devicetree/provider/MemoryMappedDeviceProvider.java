package me.hashbeep.sedna.devicetree.provider;

import me.hashbeep.sedna.api.device.Device;
import me.hashbeep.sedna.api.device.MemoryMappedDevice;
import me.hashbeep.sedna.api.devicetree.DevicePropertyNames;
import me.hashbeep.sedna.api.devicetree.DeviceTree;
import me.hashbeep.sedna.api.devicetree.DeviceTreeProvider;
import me.hashbeep.sedna.api.memory.MappedMemoryRange;
import me.hashbeep.sedna.api.memory.MemoryMap;

import java.util.Optional;

public class MemoryMappedDeviceProvider implements DeviceTreeProvider {
    @Override
    public Optional<DeviceTree> createNode(final DeviceTree root, final MemoryMap memoryMap, final Device device, final String deviceName) {
        final Optional<MappedMemoryRange> range = memoryMap.getMemoryRange((MemoryMappedDevice) device);
        return range.map(r -> root.getChild(deviceName, r.address()));
    }

    @Override
    public void visit(final DeviceTree node, final MemoryMap memoryMap, final Device device) {
        final MemoryMappedDevice mappedDevice = (MemoryMappedDevice) device;
        final Optional<MappedMemoryRange> range = memoryMap.getMemoryRange(mappedDevice);

        range.ifPresent(r -> node.addProp(DevicePropertyNames.REG,
            r.address() & 0xFFFFFFFFL,
            r.size() & 0xFFFFFFFFL));
    }
}

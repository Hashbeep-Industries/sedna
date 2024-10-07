package me.hashbeep.sedna.riscv.devicetree;

import me.hashbeep.sedna.api.device.Device;
import me.hashbeep.sedna.api.device.MemoryMappedDevice;
import me.hashbeep.sedna.api.devicetree.DevicePropertyNames;
import me.hashbeep.sedna.api.devicetree.DeviceTree;
import me.hashbeep.sedna.api.devicetree.DeviceTreeProvider;
import me.hashbeep.sedna.api.memory.MappedMemoryRange;
import me.hashbeep.sedna.api.memory.MemoryMap;

import java.util.Optional;

public final class R5CoreLocalInterrupterProvider implements DeviceTreeProvider {
    @Override
    public Optional<String> getName(final Device device) {
        return Optional.of("clint");
    }

    @Override
    public Optional<DeviceTree> createNode(final DeviceTree root, final MemoryMap memoryMap, final Device device, final String deviceName) {
        final Optional<MappedMemoryRange> range = memoryMap.getMemoryRange((MemoryMappedDevice) device);
        return range.map(r -> root.find("/soc").getChild(deviceName, r.address()));
    }

    @Override
    public void visit(final DeviceTree node, final MemoryMap memoryMap, final Device device) {
        node.addProp(DevicePropertyNames.COMPATIBLE, "riscv,clint0");
    }
}

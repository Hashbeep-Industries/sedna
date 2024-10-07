package me.hashbeep.sedna.riscv.devicetree;

import me.hashbeep.sedna.api.device.Device;
import me.hashbeep.sedna.api.device.MemoryMappedDevice;
import me.hashbeep.sedna.api.devicetree.DeviceNames;
import me.hashbeep.sedna.api.devicetree.DevicePropertyNames;
import me.hashbeep.sedna.api.devicetree.DeviceTree;
import me.hashbeep.sedna.api.devicetree.DeviceTreeProvider;
import me.hashbeep.sedna.api.memory.MappedMemoryRange;
import me.hashbeep.sedna.api.memory.MemoryMap;
import me.hashbeep.sedna.riscv.device.R5PlatformLevelInterruptController;

import java.util.Optional;

public final class R5PlatformLevelInterruptControllerProvider implements DeviceTreeProvider {
    @Override
    public Optional<String> getName(final Device device) {
        return Optional.of("plic");
    }

    @Override
    public Optional<DeviceTree> createNode(final DeviceTree root, final MemoryMap memoryMap, final Device device, final String deviceName) {
        final Optional<MappedMemoryRange> range = memoryMap.getMemoryRange((MemoryMappedDevice) device);
        return range.map(r -> root.find("/soc").getChild(deviceName, r.address()));
    }

    @Override
    public void visit(final DeviceTree node, final MemoryMap memoryMap, final Device device) {
        node
            .addProp("#address-cells", 0)
            .addProp("#interrupt-cells", 1)
            .addProp(DeviceNames.INTERRUPT_CONTROLLER)
            .addProp(DevicePropertyNames.COMPATIBLE, "riscv,plic0")
            .addProp("riscv,ndev", R5PlatformLevelInterruptController.INTERRUPT_COUNT)
            .addProp(DevicePropertyNames.PHANDLE, node.getPHandle(device));
    }
}

package li.cil.sedna.vm.devicetree.provider;

import li.cil.sedna.api.vm.MemoryMap;
import li.cil.sedna.api.vm.MemoryRange;
import li.cil.sedna.api.vm.device.Device;
import li.cil.sedna.api.vm.device.memory.MemoryMappedDevice;
import li.cil.sedna.api.vm.devicetree.DeviceTree;
import li.cil.sedna.api.vm.devicetree.DeviceTreePropertyNames;
import li.cil.sedna.api.vm.devicetree.DeviceTreeProvider;

import java.util.Optional;

public final class CoreLocalInterrupterProvider implements DeviceTreeProvider {
    public static final DeviceTreeProvider INSTANCE = new CoreLocalInterrupterProvider();

    @Override
    public Optional<String> getName(final Device device) {
        return Optional.of("clint");
    }

    @Override
    public Optional<DeviceTree> createNode(final DeviceTree root, final MemoryMap memoryMap, final Device device, final String deviceName) {
        final Optional<MemoryRange> range = memoryMap.getMemoryRange((MemoryMappedDevice) device);
        return range.map(r -> root.find("/soc").getChild(deviceName, r.address()));
    }

    @Override
    public void visit(final DeviceTree node, final MemoryMap memoryMap, final Device device) {
        node.addProp(DeviceTreePropertyNames.COMPATIBLE, "riscv,clint0");
    }
}

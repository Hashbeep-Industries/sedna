package me.hashbeep.sedna.devicetree.provider;

import me.hashbeep.sedna.api.device.Device;
import me.hashbeep.sedna.api.device.MemoryMappedDevice;
import me.hashbeep.sedna.api.devicetree.DeviceNames;
import me.hashbeep.sedna.api.devicetree.DevicePropertyNames;
import me.hashbeep.sedna.api.devicetree.DeviceTree;
import me.hashbeep.sedna.api.devicetree.DeviceTreeProvider;
import me.hashbeep.sedna.api.memory.MappedMemoryRange;
import me.hashbeep.sedna.api.memory.MemoryMap;
import me.hashbeep.sedna.device.syscon.AbstractSystemController;

import java.util.Optional;

public final class SystemControllerProvider implements DeviceTreeProvider {
    @Override
    public Optional<String> getName(final Device device) {
        return Optional.of(DeviceNames.SYSCON);
    }

    @Override
    public Optional<DeviceTree> createNode(final DeviceTree root, final MemoryMap memoryMap, final Device device, final String deviceName) {
        final Optional<MappedMemoryRange> range = memoryMap.getMemoryRange((MemoryMappedDevice) device);
        return range.map(r -> root.find("/soc").getChild(deviceName, r.address()));
    }

    @Override
    public void visit(final DeviceTree node, final MemoryMap memoryMap, final Device device) {
        final int handle = node.getPHandle(device);
        node
            .addProp(DevicePropertyNames.COMPATIBLE, "syscon")
            .addProp(DevicePropertyNames.PHANDLE, handle);

        final DeviceTree soc = node.find("/soc");
        soc.putChild("reboot", reboot -> reboot
            .addProp(DevicePropertyNames.COMPATIBLE, "syscon-reboot")
            .addProp("regmap", handle)
            .addProp("offset", 0)
            .addProp("value", AbstractSystemController.SYSCON_RESET));
        soc.putChild("poweroff", poweroff -> poweroff
            .addProp(DevicePropertyNames.COMPATIBLE, "syscon-poweroff")
            .addProp("regmap", handle)
            .addProp("offset", 0)
            .addProp("value", AbstractSystemController.SYSCON_POWEROFF));
    }
}

package me.hashbeep.sedna.api.devicetree;

import me.hashbeep.sedna.api.device.Device;
import me.hashbeep.sedna.devicetree.FlattenedDeviceTree;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface DeviceTree {
    FlattenedDeviceTree flatten();

    int getPHandle(final Device device);

    DeviceTree find(final String path);

    String getPath();

    DeviceTree addProp(final String name, final Object... values);

    DeviceTree getChild(final String name, @Nullable final String address);

    default DeviceTree getChild(final String name, final long address) {
        return getChild(name, String.format("%x", address));
    }

    default DeviceTree getChild(final String name) {
        return getChild(name, null);
    }

    default DeviceTree putChild(final String name, @Nullable final String address, final Consumer<DeviceTree> builder) {
        builder.accept(getChild(name, address));
        return this;
    }

    default DeviceTree putChild(final String name, final long address, final Consumer<DeviceTree> builder) {
        return putChild(name, String.format("%x", address), builder);
    }

    default DeviceTree putChild(final String name, final Consumer<DeviceTree> builder) {
        return putChild(name, null, builder);
    }
}

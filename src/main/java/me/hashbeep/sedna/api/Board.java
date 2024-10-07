package me.hashbeep.sedna.api;

import me.hashbeep.sedna.api.device.InterruptController;
import me.hashbeep.sedna.api.device.MemoryMappedDevice;
import me.hashbeep.sedna.api.device.Resettable;
import me.hashbeep.sedna.api.device.Steppable;
import me.hashbeep.sedna.api.memory.MemoryMap;
import me.hashbeep.sedna.api.memory.MemoryRangeAllocationStrategy;

import javax.annotation.Nullable;
import java.util.OptionalLong;

public interface Board extends Steppable, Resettable {
    MemoryMap getMemoryMap();

    InterruptController getInterruptController();

    MemoryRangeAllocationStrategy getAllocationStrategy();

    boolean addDevice(long address, MemoryMappedDevice device);

    OptionalLong addDevice(MemoryMappedDevice device);

    void removeDevice(MemoryMappedDevice device);

    int getInterruptCount();

    long getDefaultProgramStart();

    void setBootArguments(String value);

    void setStandardOutputDevice(@Nullable MemoryMappedDevice device);
}

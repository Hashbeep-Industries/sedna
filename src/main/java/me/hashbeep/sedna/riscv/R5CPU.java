package me.hashbeep.sedna.riscv;

import me.hashbeep.sedna.api.device.InterruptController;
import me.hashbeep.sedna.api.device.Resettable;
import me.hashbeep.sedna.api.device.Steppable;
import me.hashbeep.sedna.api.device.rtc.RealTimeCounter;
import me.hashbeep.sedna.api.memory.MemoryMap;
import me.hashbeep.sedna.gdbstub.CPUDebugInterface;

import javax.annotation.Nullable;

public interface R5CPU extends Steppable, Resettable, RealTimeCounter, InterruptController {
    static R5CPU create(final MemoryMap physicalMemory, @Nullable final RealTimeCounter rtc) {
        return R5CPUGenerator.create(physicalMemory, rtc);
    }

    static R5CPU create(final MemoryMap physicalMemory) {
        return create(physicalMemory, null);
    }

    long getISA();

    void setXLEN(int value);

    void reset(boolean hard, long pc);

    void invalidateCaches();

    void setFrequency(int value);

    CPUDebugInterface getDebugInterface();
}

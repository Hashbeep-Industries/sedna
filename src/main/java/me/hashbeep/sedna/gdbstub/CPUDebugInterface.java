package me.hashbeep.sedna.gdbstub;

import me.hashbeep.sedna.riscv.exception.R5MemoryAccessException;

import java.util.function.LongConsumer;

public interface CPUDebugInterface {
    long getProgramCounter();

    void setProgramCounter(long value);

    void step();

    long[] getGeneralRegisters();

    byte[] loadDebug(final long address, final int size) throws R5MemoryAccessException;

    int storeDebug(final long address, final byte[] data) throws R5MemoryAccessException;

    void addBreakpointListener(LongConsumer listener);

    void removeBreakpointListener(LongConsumer listener);

    void addBreakpoint(long address);

    void removeBreakpoint(long address);
}

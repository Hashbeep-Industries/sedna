package li.cil.sedna.device.memory;

import li.cil.sedna.api.Sizes;
import li.cil.sedna.api.device.PhysicalMemory;
import li.cil.sedna.api.memory.MemoryAccessException;
import li.cil.sedna.utils.UnsafeGetter;
import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.misc.VM;

import java.nio.ByteBuffer;

// Tends to be around 10% faster than ByteBufferMemory during regular emulation.
public final class UnsafeMemory implements PhysicalMemory {
    private static final Unsafe UNSAFE = UnsafeGetter.get();

    private final long address;
    private final long size;
    private final Cleaner cleaner;

    public UnsafeMemory(final int size) {
        if ((size & 0b11) != 0)
            throw new IllegalArgumentException("size must be a multiple of four");

        // Handling for page aligned memory taken from DirectByteBuffer.

        final boolean isAligned = VM.isDirectMemoryPageAligned();
        final int pageSize = UNSAFE.pageSize();

        this.size = Math.max(4L, (long) size + (isAligned ? pageSize : 0));

        final long base = UNSAFE.allocateMemory(this.size);

        UNSAFE.setMemory(base, this.size, (byte) 0);

        if (isAligned && (base % pageSize != 0)) {
            address = base + pageSize - (base & (pageSize - 1));
        } else {
            address = base;
        }

        cleaner = Cleaner.create(this, new Deallocator(address));
    }

    public void dispose() {
        cleaner.clean();
    }

    @Override
    public int getLength() {
        return (int) size;
    }

    @Override
    public int load(final int offset, final int sizeLog2) throws MemoryAccessException {
        if (offset < 0 || offset > size - (1 << sizeLog2)) {
            throw new MemoryAccessException(offset, MemoryAccessException.Type.LOAD_FAULT);
        }
        switch (sizeLog2) {
            case Sizes.SIZE_8_LOG2:
                return UNSAFE.getByte(address + offset);
            case Sizes.SIZE_16_LOG2:
                return UNSAFE.getShort(address + offset);
            case Sizes.SIZE_32_LOG2:
                return UNSAFE.getInt(address + offset);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void store(final int offset, final int value, final int sizeLog2) throws MemoryAccessException {
        if (offset < 0 || offset > size - (1 << sizeLog2)) {
            throw new MemoryAccessException(offset, MemoryAccessException.Type.STORE_FAULT);
        }
        switch (sizeLog2) {
            case Sizes.SIZE_8_LOG2:
                UNSAFE.putByte(address + offset, (byte) value);
                break;
            case Sizes.SIZE_16_LOG2:
                UNSAFE.putShort(address + offset, (short) value);
                break;
            case Sizes.SIZE_32_LOG2:
                UNSAFE.putInt(address + offset, value);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void load(int offset, final ByteBuffer dst) throws MemoryAccessException {
        if (offset < 0 || offset > size - dst.remaining()) {
            throw new MemoryAccessException(offset, MemoryAccessException.Type.LOAD_FAULT);
        }
        while (dst.hasRemaining()) {
            dst.put(UNSAFE.getByte(address + offset++));
        }
    }

    @Override
    public void store(int offset, final ByteBuffer src) throws MemoryAccessException {
        if (offset < 0 || offset > size - src.remaining()) {
            throw new MemoryAccessException(offset, MemoryAccessException.Type.STORE_FAULT);
        }
        while (src.hasRemaining()) {
            UNSAFE.putByte(address + offset++, src.get());
        }
    }

    private static final class Deallocator implements Runnable {
        private long address;

        public Deallocator(final long address) {
            this.address = address;
        }

        @Override
        public void run() {
            if (address != 0) {
                UNSAFE.freeMemory(address);
                address = 0;
            }
        }
    }
}

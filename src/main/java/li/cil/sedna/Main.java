package li.cil.sedna;

import li.cil.sedna.api.device.PhysicalMemory;
import li.cil.sedna.api.Sizes;
import li.cil.sedna.device.block.ByteBufferBlockDevice;
import li.cil.sedna.device.serial.UART16550A;
import li.cil.sedna.device.memory.Memory;
import li.cil.sedna.device.virtio.VirtIOBlockDevice;
import li.cil.sedna.riscv.R5Board;
import li.cil.sedna.riscv.R5CPU;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;

public final class Main {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final BooleanArgument BENCHMARK = new BooleanArgument("benchmark", "b");
    public static final BooleanArgument VERBOSE = new BooleanArgument("verbose", "v");

    public static void main(final String[] args) throws Exception {
        Arguments.parse(args);

        if (BENCHMARK.get()) {
            runBenchmark();
        } else {
            runEmulator();
        }
    }

    private static void runEmulator() throws Exception {
        final String firmware = "buildroot/output/images/fw_jump.bin";
        final String kernel = "buildroot/output/images/Image";
        final File rootfsFile = new File("buildroot/output/images/rootfs.ext2");

        final R5Board board = new R5Board();
        final PhysicalMemory rom = Memory.create(128 * 1024);
        final PhysicalMemory memory = Memory.create(32 * 1014 * 1024);
        final UART16550A uart = new UART16550A();
        final VirtIOBlockDevice hdd = new VirtIOBlockDevice(board.getMemoryMap(), ByteBufferBlockDevice.createFromFile(rootfsFile, true));

        uart.getInterrupt().set(0xA, board.getInterruptController());
        hdd.getInterrupt().set(0x1, board.getInterruptController());

        board.addDevice(0x80000000, rom);
        board.addDevice(0x80000000 + 0x400000, memory);
        board.addDevice(uart);
        board.addDevice(hdd);

        board.setBootargs("console=ttyS0 root=/dev/vda ro");

        board.reset();

        loadProgramFile(memory, 0, kernel);
        loadProgramFile(rom, 0, firmware);

        final int cyclesPerSecond = board.getCpu().getFrequency();
        final int cyclesPerStep = 1_000;

        try (final InputStreamReader isr = new InputStreamReader(System.in)) {
            final BufferedReader br = new BufferedReader(isr);

            int remaining = 0;
            for (; ; ) {
                final long stepStart = System.currentTimeMillis();

                remaining += cyclesPerSecond;
                while (remaining > 0) {
                    board.step(cyclesPerStep);
                    remaining -= cyclesPerStep;

                    int value;
                    while ((value = uart.read()) != -1) {
                        System.out.print((char) value);
                    }

                    while (br.ready() && uart.canPutByte()) {
                        uart.putByte((byte) br.read());
                    }
                }

                final long stepDuration = System.currentTimeMillis() - stepStart;
                final long sleep = 1000 - stepDuration;
                if (sleep > 0) {
                    Thread.sleep(sleep);
                } else if (VERBOSE.get()) {
                    LOGGER.warn("Running behind by {}ms...", -sleep);
                }
            }
        }
    }

    private static void runBenchmark() throws Exception {
        final R5Board board = new R5Board();
        final PhysicalMemory rom = Memory.create(128 * 1024);
        final PhysicalMemory memory = Memory.create(128 * 1014 * 1024);
        final UART16550A uart = new UART16550A();

        uart.getInterrupt().set(0xA, board.getInterruptController());

        board.addDevice(0x10000000, uart);
        board.addDevice(0x80000000, rom);
        board.addDevice(0x80000000 + 0x400000, memory);

        board.setBootargs("console=ttyS0");

        final String firmware = "buildroot/output/images/fw_jump.bin";
        final String kernel = "buildroot/output/images/Image_Benchmark";

        LOGGER.info("Waiting for profiler...");
        Thread.sleep(5 * 1000);
        LOGGER.info("Starting!");

        final long cyclesPerRun = 300_000_000;
        final int cyclesPerStep = 1_000;
        final int hz = board.getCpu().getFrequency();

        final int samples = 10;
        int minRunDuration = Integer.MAX_VALUE;
        int maxRunDuration = Integer.MIN_VALUE;
        int accRunDuration = 0;

        final R5CPU cpu = board.getCpu();
        final StringBuilder sb = new StringBuilder(16 * 1024);

        for (int i = 0; i < samples; i++) {
            board.reset();
            loadProgramFile(memory, 0, kernel);
            loadProgramFile(rom, 0, firmware);
            sb.setLength(0);

            final long runStart = System.currentTimeMillis();

            final long limit = cpu.getTime() + cyclesPerRun;
            int remaining = 0;
            while (cpu.getTime() < limit) {
                remaining += hz;
                while (remaining > 0) {
                    board.step(cyclesPerStep);
                    remaining -= cyclesPerStep;

                    int value;
                    while ((value = uart.read()) != -1) {
                        sb.append((char) value);
                    }
                }
            }

            final int runDuration = (int) (System.currentTimeMillis() - runStart);
            accRunDuration += runDuration;
            minRunDuration = Integer.min(minRunDuration, runDuration);
            maxRunDuration = Integer.max(maxRunDuration, runDuration);

            System.out.print(sb.toString());

            System.out.printf("\n\ntime: %.2fs\n", runDuration / 1000.0);
        }

        final int avgDuration = accRunDuration / samples;
        System.out.printf("\n\ntimes: min=%.2fs, max=%.2fs, avg=%.2fs\n",
                minRunDuration / 1000.0, maxRunDuration / 1000.0, avgDuration / 1000.0);
    }

    private static void loadProgramFile(final PhysicalMemory memory, int address, final String path) throws Exception {
        try (final FileInputStream is = new FileInputStream(path)) {
            final BufferedInputStream bis = new BufferedInputStream(is);
            for (int value = bis.read(); value != -1; value = bis.read()) {
                memory.store(address++, (byte) value, Sizes.SIZE_8_LOG2);
            }
        }
    }

    private static final class Arguments {
        private static final ArrayList<AbstractArgument> ALL_ARGUMENTS = new ArrayList<>();

        public static void parse(final String[] args) {
            int index = 0;
            while (index < args.length) {
                final int nextIndex = parse(args, index);
                if (nextIndex == index) {
                    LOGGER.warn("Unknown argument [{}].", args[index]);
                    index++;
                } else {
                    index = nextIndex;
                }
            }
        }

        private static int parse(final String[] args, final int index) {
            for (final AbstractArgument argument : ALL_ARGUMENTS) {
                final int nextIndex = argument.tryParse(args, index);
                if (nextIndex != index) {
                    return nextIndex;
                }
            }

            return index;
        }
    }

    private static abstract class AbstractArgument {
        protected final String name;
        @Nullable protected final String shortName;

        AbstractArgument(final String name, @Nullable final String shortName) {
            this.name = name;
            this.shortName = shortName;
            Arguments.ALL_ARGUMENTS.add(this);
        }

        AbstractArgument(final String name) {
            this(name, null);
        }

        protected boolean matchesName(final String arg) {
            return arg.equals("--" + name) || arg.equals("-" + shortName);
        }

        protected abstract int tryParse(final String[] args, int index);
    }

    private static final class BooleanArgument extends AbstractArgument {
        private boolean value;

        public BooleanArgument(final String name, @Nullable final String shortName) {
            super(name, shortName);
        }

        public BooleanArgument(final String name) {
            super(name);
        }

        public boolean get() {
            return value;
        }

        @Override
        protected int tryParse(final String[] args, final int index) {
            if (matchesName(args[index])) {
                value = true;
                return index + 1;
            }
            return index;
        }
    }
}

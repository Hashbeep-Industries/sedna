package me.hashbeep.sedna.device.virtio;

import it.unimi.dsi.fastutil.bytes.ByteArrayFIFOQueue;
import li.cil.ceres.api.Serialized;
import me.hashbeep.sedna.api.device.serial.SerialDevice;
import me.hashbeep.sedna.api.memory.MemoryAccessException;
import me.hashbeep.sedna.api.memory.MemoryMap;

@SuppressWarnings("PointlessBitwiseExpression")
public final class VirtIOConsoleDevice extends AbstractVirtIODevice implements SerialDevice {
    private static final short DEFAULT_COLUMN_COUNT = 80;
    private static final short DEFAULT_ROW_COUNT = 25;

    private static final int BUFFER_SIZE = 4 * 1024;

    private static final long VIRTIO_CONSOLE_F_SIZE = 1L << 0; // Configuration for cols and rows.
    private static final long VIRTIO_CONSOLE_F_MULTIPORT = 1L << 1; // Configuration max_nr_ports, control virtqueues.
    private static final long VIRTIO_CONSOLE_F_EMERG_WRITE = 1L << 2; // Supports emergency write.

    private static final int VIRTIO_CONSOLE_DEVICE_READY = 0;
    private static final int VIRTIO_CONSOLE_DEVICE_ADD = 1;
    private static final int VIRTIO_CONSOLE_DEVICE_REMOVE = 2;
    private static final int VIRTIO_CONSOLE_PORT_READY = 3;
    private static final int VIRTIO_CONSOLE_CONSOLE_PORT = 4;
    private static final int VIRTIO_CONSOLE_RESIZE = 5;
    private static final int VIRTIO_CONSOLE_PORT_OPEN = 6;
    private static final int VIRTIO_CONSOLE_PORT_NAME = 7;

    private static final int VIRTIO_CONSOLE_CFG_COLS_OFFSET = 0;
    private static final int VIRTIO_CONSOLE_CFG_ROWS_OFFSET = 2;
    private static final int VIRTIO_CONSOLE_CFG_MAX_NR_PORTS_OFFSET = 4;
    private static final int VIRTIO_CONSOLE_CFG_EMERG_WR_OFFSET = 8;

    private static final int VIRTQ_RECEIVE = 0; // receiveq(port0)
    private static final int VIRTQ_TRANSMIT = 1; // transmitq(port0)
    private static final int VIRTQ_RECEIVE_CONTROL = 2; // control receiveq
    private static final int VIRTQ_TRANSMIT_CONTROL = 3; // control transmitq

    // Store input and output in own buffers to avoid storing chains for serialization.
    @Serialized private final ByteArrayFIFOQueue transmitBuffer = new ByteArrayFIFOQueue(BUFFER_SIZE);
    @Serialized private final ByteArrayFIFOQueue receiveBuffer = new ByteArrayFIFOQueue(BUFFER_SIZE);

    public VirtIOConsoleDevice(final MemoryMap memoryMap) {
        super(memoryMap, VirtIODeviceSpec
            .builder(VirtIODeviceType.VIRTIO_DEVICE_ID_CONSOLE)
            .features(VIRTIO_CONSOLE_F_SIZE)
            .queueCount(2)
            .configSpaceSize(4)
            .build());
    }

    @Override
    public int read() {
        if (hasDeviceFailed()) {
            return -1;
        }

        if (transmitBuffer.isEmpty()) {
            try {
                // 5.3.6.1: The driver MUST NOT put a device-writable buffer in a transmitq.
                final DescriptorChain transmit = validateReadOnlyDescriptorChain(VIRTQ_TRANSMIT, null);
                if (transmit != null) {
                    while (transmit.readableBytes() > 0) {
                        transmitBuffer.enqueue(transmit.get());
                    }
                    transmit.use();
                }
            } catch (final VirtIODeviceException | MemoryAccessException e) {
                error();
                return -1;
            }
        }

        if (transmitBuffer.isEmpty()) {
            return -1;
        }

        return transmitBuffer.dequeueByte() & 0xFF;
    }

    @Override
    public boolean canPutByte() {
        if (hasDeviceFailed()) {
            return false;
        }

        return receiveBuffer.size() < BUFFER_SIZE;
    }

    @Override
    public void putByte(final byte value) {
        if (hasDeviceFailed()) {
            return;
        }

        if (receiveBuffer.size() < BUFFER_SIZE) {
            receiveBuffer.enqueue(value);
        }

        if (receiveBuffer.size() >= BUFFER_SIZE) {
            flush();
        }
    }

    @Override
    public void flush() {
        if (hasDeviceFailed()) {
            return;
        }

        while (!receiveBuffer.isEmpty()) {
            try {
                // 5.3.6.1: The driver MUST NOT put a device-readable in a receiveq.
                final DescriptorChain receive = validateWriteOnlyDescriptorChain(VIRTQ_RECEIVE, null);
                if (receive == null) {
                    return;
                }

                while (receive.writableBytes() > 0 && !receiveBuffer.isEmpty()) {
                    receive.put(receiveBuffer.dequeueByte());
                }
                receive.use();
            } catch (final VirtIODeviceException | MemoryAccessException e) {
                error();
                return;
            }
        }
    }

    @Override
    protected void initializeConfig() {
        setConfigValue(VIRTIO_CONSOLE_CFG_COLS_OFFSET, DEFAULT_COLUMN_COUNT);
        setConfigValue(VIRTIO_CONSOLE_CFG_ROWS_OFFSET, DEFAULT_ROW_COUNT);
    }

    @Override
    protected void storeConfig(final int offset, final long value, final int sizeLog2) {
        if (offset == VIRTIO_CONSOLE_CFG_EMERG_WR_OFFSET) {
            // 5.3.5.1: The device SHOULD transmit the lower byte written to emerg_wr [...]
        }
    }

    @Override
    protected void handleFeaturesNegotiated() {
        setQueueNotifications(VIRTQ_RECEIVE, false);
        setQueueNotifications(VIRTQ_TRANSMIT, false);
    }

    private boolean hasDeviceFailed() {
        return (getStatus() & VIRTIO_STATUS_FAILED) != 0;
    }
}

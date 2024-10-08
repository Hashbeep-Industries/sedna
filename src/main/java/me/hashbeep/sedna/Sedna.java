package me.hashbeep.sedna;

import it.unimi.dsi.fastutil.bytes.ByteArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.Int2LongArrayMap;
import li.cil.ceres.Ceres;
import me.hashbeep.sedna.api.device.InterruptSource;
import me.hashbeep.sedna.api.device.MemoryMappedDevice;
import me.hashbeep.sedna.api.device.PhysicalMemory;
import me.hashbeep.sedna.device.block.SparseBlockDevice;
import me.hashbeep.sedna.device.flash.FlashMemoryDevice;
import me.hashbeep.sedna.device.rtc.GoldfishRTC;
import me.hashbeep.sedna.device.serial.UART16550A;
import me.hashbeep.sedna.device.syscon.AbstractSystemController;
import me.hashbeep.sedna.device.virtio.AbstractVirtIODevice;
import me.hashbeep.sedna.device.virtio.VirtIOFileSystemDevice;
import me.hashbeep.sedna.devicetree.DeviceTreeRegistry;
import me.hashbeep.sedna.devicetree.provider.*;
import me.hashbeep.sedna.riscv.R5CPU;
import me.hashbeep.sedna.riscv.device.R5CoreLocalInterrupter;
import me.hashbeep.sedna.riscv.device.R5PlatformLevelInterruptController;
import me.hashbeep.sedna.riscv.devicetree.R5CoreLocalInterrupterProvider;
import me.hashbeep.sedna.riscv.devicetree.R5PlatformLevelInterruptControllerProvider;
import me.hashbeep.sedna.serializers.*;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

public final class Sedna {
    private static boolean isInitialized = false;

    static {
        initialize();
    }

    public static void initialize() {
        if (isInitialized) {
            return;
        }

        isInitialized = true;

        Ceres.putSerializer(AtomicInteger.class, new AtomicIntegerSerializer());
        Ceres.putSerializer(BitSet.class, new BitSetSerializer());
        Ceres.putSerializer(ByteArrayFIFOQueue.class, new ByteArrayFIFOQueueSerializer());
        Ceres.putSerializer(VirtIOFileSystemDevice.FileSystemFileMap.class, new FileSystemFileMapSerializer());
        Ceres.putSerializer(Int2LongArrayMap.class, new Int2LongArrayMapSerializer());
        Ceres.putSerializer(R5CPU.class, new R5CPUSerializer());
        Ceres.putSerializer(SparseBlockDevice.SparseBlockMap.class, new SparseBlockMapSerializer());

        DeviceTreeRegistry.putProvider(FlashMemoryDevice.class, new FlashMemoryProvider());
        DeviceTreeRegistry.putProvider(GoldfishRTC.class, new GoldfishRTCProvider());
        DeviceTreeRegistry.putProvider(InterruptSource.class, new InterruptSourceProvider());
        DeviceTreeRegistry.putProvider(MemoryMappedDevice.class, new MemoryMappedDeviceProvider());
        DeviceTreeRegistry.putProvider(PhysicalMemory.class, new PhysicalMemoryProvider());
        DeviceTreeRegistry.putProvider(AbstractSystemController.class, new SystemControllerProvider());
        DeviceTreeRegistry.putProvider(UART16550A.class, new UART16550AProvider());
        DeviceTreeRegistry.putProvider(AbstractVirtIODevice.class, new VirtIOProvider());
        DeviceTreeRegistry.putProvider(R5CoreLocalInterrupter.class, new R5CoreLocalInterrupterProvider());
        DeviceTreeRegistry.putProvider(R5PlatformLevelInterruptController.class, new R5PlatformLevelInterruptControllerProvider());
    }
}

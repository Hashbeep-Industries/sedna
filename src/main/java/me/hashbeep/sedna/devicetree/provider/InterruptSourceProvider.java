package me.hashbeep.sedna.devicetree.provider;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.hashbeep.sedna.api.Interrupt;
import me.hashbeep.sedna.api.device.Device;
import me.hashbeep.sedna.api.device.InterruptController;
import me.hashbeep.sedna.api.device.InterruptSource;
import me.hashbeep.sedna.api.devicetree.DeviceTree;
import me.hashbeep.sedna.api.devicetree.DeviceTreeProvider;
import me.hashbeep.sedna.api.memory.MemoryMap;

public final class InterruptSourceProvider implements DeviceTreeProvider {
    @Override
    public void visit(final DeviceTree node, final MemoryMap memoryMap, final Device device) {
        final InterruptSource interruptSource = (InterruptSource) device;
        final IntList interrupts = new IntArrayList();
        for (final Interrupt interrupt : interruptSource.getInterrupts()) {
            final InterruptController controller = interrupt.controller;
            if (controller != null) {
                interrupts.add(node.getPHandle(controller));
                interrupts.add(interrupt.id);
            }
        }
        node.addProp("interrupts-extended", interrupts.toArray());
    }
}
